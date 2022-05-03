package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino2sql.config.DbConfig;

public class BeforeActionDriverFormula extends ActionDriverFormula implements BeforeActionDriver{
    protected BeforeExtendedOptionsFormula extendedOptions;

    @Override
    public String sqlFormula() {
        return extendedOptions.before_sql_formula;
    }

    public static class BeforeExtendedOptionsFormula {
        protected String before_sql_formula;
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        extendedOptions = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), BeforeExtendedOptionsFormula.class);
    }
}
