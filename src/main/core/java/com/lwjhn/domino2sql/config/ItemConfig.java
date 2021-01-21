package com.lwjhn.domino2sql.config;

import com.alibaba.fastjson.JSONObject;

import java.sql.JDBCType;
import java.text.SimpleDateFormat;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class ItemConfig {
    public SimpleDateFormat date_format = DefaultConfig.DateFormat;
    public String join_delimiter = DefaultConfig.String_Join_Delimiter;
    private String sql_name = null;
    private String domino_name = null;
    private JDBCType jdbc_type = JDBCType.NULL;
    private String domino_formula = null;
    private int scale_length = 0;
    private JSONObject extended_options = null;

    public ItemConfig(String sql_name, String domino_name, JDBCType jdbc_type, int scale_length) {
        this.sql_name = sql_name;
        this.domino_name = domino_name;
        this.jdbc_type = jdbc_type;
        this.scale_length = scale_length;
    }

    public ItemConfig(String sql_name, JDBCType jdbc_type, int scale_length) {
        this.sql_name = sql_name;
        this.jdbc_type = jdbc_type;
        this.scale_length = scale_length;
    }

    public ItemConfig(String sql_name, String domino_formula, JDBCType jdbc_type) {
        this.sql_name = sql_name;
        this.domino_formula = domino_formula;
        this.jdbc_type = jdbc_type;
    }

    public ItemConfig(String sql_name, String domino_name, String domino_formula, JDBCType jdbc_type, int scale_length) {
        this.sql_name = sql_name;
        this.domino_name = domino_name;
        this.jdbc_type = jdbc_type;
        this.domino_formula = domino_formula;
        this.scale_length = scale_length;
    }

    public ItemConfig(String sql_name, String domino_name, String domino_formula, JDBCType jdbc_type) {
        this.sql_name = sql_name;
        this.domino_name = domino_name;
        this.jdbc_type = jdbc_type;
        this.domino_formula = domino_formula;
    }

    public ItemConfig() {
    }

    public int getScale_length() {
        return scale_length;
    }

    public void setScale_length(int scale_length) {
        this.scale_length = scale_length;
    }

    public String getSql_name() {
        return sql_name;
    }

    public void setSql_name(String sql_name) {
        this.sql_name = sql_name;
    }

    public String getDomino_name() {
        return domino_name;
    }

    public void setDomino_name(String domino_name) {
        this.domino_name = domino_name;
    }

    public JDBCType getJdbc_type() {
        return jdbc_type;
    }

    public void setJdbc_type(JDBCType jdbc_type) {
        this.jdbc_type = jdbc_type;
    }

    public String getDomino_formula() {
        return domino_formula;
    }

    public void setDomino_formula(String domino_formula) {
        this.domino_formula = domino_formula;
    }

    public SimpleDateFormat getDate_format() {
        return date_format;
    }

    public void setDate_format(SimpleDateFormat date_format) {
        this.date_format = date_format;
    }

    public String getJoin_delimiter() {
        return join_delimiter;
    }

    public void setJoin_delimiter(String join_delimiter) {
        this.join_delimiter = join_delimiter;
    }

    public JSONObject getExtended_options() {
        return extended_options;
    }

    public void setExtended_options(JSONObject extended_options) {
        this.extended_options = extended_options;
    }
}
