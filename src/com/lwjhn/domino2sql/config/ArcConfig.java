package com.lwjhn.domino2sql.config;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcConfig {
    private String sql_driver = null;
    private String sql_url = null;
    private String sql_username = null;
    private String sql_password = null;
    private DbConfig[] options = null;
    private String error_log = null;
    private JSONObject extended_options = null;

    public ArcConfig() {
    }

    public ArcConfig(String sql_driver, String sql_url, String sql_username, String sql_password) {
        this.sql_driver = sql_driver;
        this.sql_url = sql_url;
        this.sql_username = sql_username;
        this.sql_password = sql_password;
    }

    public String getSql_driver() {
        return sql_driver;
    }

    public void setSql_driver(String sql_driver) {
        this.sql_driver = sql_driver;
    }

    public String getSql_url() {
        return sql_url;
    }

    public void setSql_url(String sql_url) {
        this.sql_url = sql_url;
    }

    public String getSql_username() {
        return sql_username;
    }

    public void setSql_username(String sql_username) {
        this.sql_username = sql_username;
    }

    public String getSql_password() {
        return sql_password;
    }

    public void setSql_password(String sql_password) {
        this.sql_password = sql_password;
    }

    public DbConfig[] getOptions() {
        return options;
    }

    public void setOptions(DbConfig[] options) {
        this.options = options;
    }

    public String getError_log() {
        return error_log;
    }

    public void setError_log(String error_log) {
        this.error_log = error_log;
    }

    public JSONObject getExtended_options() {
        return extended_options;
    }

    public void setExtended_options(JSONObject extended_options) {
        this.extended_options = extended_options;
    }
}
