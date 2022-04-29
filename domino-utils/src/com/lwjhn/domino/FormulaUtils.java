package com.lwjhn.domino;

import com.lwjhn.function.*;
import com.lwjhn.util.BeanFieldsIterator;
import com.lwjhn.util.Common;
import lotus.domino.Document;
import lotus.domino.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

public class FormulaUtils {
    private final Session session;

    public FormulaUtils(Session session) {
        this.session = session;
    }

    public <T> void evaluate(Collection<T> property, UniFunction<T, String> formulaAction, Document doc, UniConsumer<T, Vector<?>> caller) {
        property.forEach(o -> {
            try {
                caller.accept(o, session.evaluate(formulaAction.apply(o), doc));
            } catch (Exception e) {
                throw new RuntimeException("execute the formula key of " + o.toString() + " find error . " + e.getMessage());
            }
        });
    }

    public <T> void evaluateToString(Map<T, Formula> property, UniFunction<T, String> formulaAction, Document doc, UniConsumer<T, String> caller) {
        this.evaluate(property.keySet(), formulaAction, doc, (t, vector) -> {
            Formula formula = property.get(t);
            String result = Common.isEmptyCollection(vector) ? formula.defaultValue : vector.get(0).toString();
            if (formula.required && StringUtils.isBlank(result)) {
                throw new RuntimeException("the result of " + t.toString() + " is null . formula : " + formula.formula);
            }
            caller.accept(t, result);
        });
    }

    public <T> void evaluateBean(Object target, Collection<T> property, Document doc, UniConsumer<T, Vector<?>> caller) {
        evaluate(property, s -> String.valueOf(BeanFieldsIterator.getFieldValue(s.toString(), target)), doc, caller);
    }

    public void evaluateBeanToString(Object target, Map<?, Formula> properties, Document doc) {
        evaluateToString(properties, property -> String.valueOf(BeanFieldsIterator.getFieldValue(property.toString(), target)), doc, (property, value) -> BeanFieldsIterator.setField(property.toString(), target, value));
    }

    public String evaluate(String formula, Document doc) {
        try {
            Vector<?> vector = session.evaluate(formula, doc);
            return vector.get(0).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Formula {
        private String formula;
        private String defaultValue = "";
        private boolean required = true;

        public Formula() {
        }

        public Formula(String formula, boolean required) {
            this.formula = formula;
            this.required = required;
        }

        public Formula(String formula, String defaultValue, boolean required) {
            this.formula = formula;
            this.defaultValue = defaultValue;
            this.required = required;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
