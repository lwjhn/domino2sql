package com.rjsoft.driver;

import com.lwjhn.domino2sql.config.ItemConfig;
import com.lwjhn.domino2sql.driver.DefaultDriverConfig;

@SuppressWarnings("unused")
public class ExtendedOptions4LocalFile extends ExtendedOptions {
    protected ItemConfig sql_field_attachment = null;
    protected String ftppath_formula = DefaultDriverConfig.ftppath_formula;

    public ItemConfig getSql_field_attachment() {
        return sql_field_attachment;
    }

    public void setSql_field_attachment(ItemConfig sql_field_attachment) {
        this.sql_field_attachment = sql_field_attachment;
    }

    public String getFtppath_formula() {
        return ftppath_formula;
    }

    public void setFtppath_formula(String ftppath_formula) {
        this.ftppath_formula = ftppath_formula;
    }
}
