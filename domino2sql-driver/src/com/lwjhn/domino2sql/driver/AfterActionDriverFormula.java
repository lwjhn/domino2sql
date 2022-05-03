package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino2sql.config.DbConfig;

public class AfterActionDriverFormula extends ActionDriverFormula implements BeforeActionDriver{
    protected AfterExtendedOptionsFormula extendedOptions;

    @Override
    public String sqlFormula() {
        return extendedOptions.after_sql_formula;
    }

    public static class AfterExtendedOptionsFormula {
        protected String after_sql_formula;
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        extendedOptions = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), AfterExtendedOptionsFormula.class);
    }
}
