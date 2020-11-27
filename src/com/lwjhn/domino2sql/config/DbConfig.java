package com.lwjhn.domino2sql.config;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class DbConfig {
    private String vesion = DefaultConfig.VERSION;
    private String ftppath = DefaultConfig.FTPPATH;
    private boolean enable = true;
    private String domino_server = null;
    private String domino_dbpath = null;
    private String domino_query = null;
    private String domino_error_flag_field = DefaultConfig.Domino_Error_Flag_Field;
    private String domino_succ_flag_field = DefaultConfig.Domino_Succ_Flag_Field;
    private String sql_table = null;
    private ItemConfig sql_field_attachment = null;
    private ItemConfig[] sql_field_others = null;
    private String action_error_log = null;
    private int action_all_count = 0;
    private int action_succ_count = 0;
    private int action_err_count = 0;
    private String domino_before_prepared_driver = null;
    private String domino_after_prepared_driver = null;
    private JSONObject extended_options = null;

    public DbConfig(String domino_server, String domino_dbpath, String domino_query) {
        this.domino_server = domino_server;
        this.domino_dbpath = domino_dbpath;
        this.domino_query = domino_query;
    }

    public DbConfig() {
    }

    public String getAction_error_log() {
        return action_error_log;
    }

    public void setAction_error_log(String action_error_log) {
        this.action_error_log = action_error_log;
    }

    public String getDomino_succ_flag_field() {
        return domino_succ_flag_field;
    }

    public void setDomino_succ_flag_field(String domino_succ_flag_field) {
        this.domino_succ_flag_field = domino_succ_flag_field;
    }

    public int getAction_all_count() {
        return action_all_count;
    }

    public void setAction_all_count(int action_all_count) {
        this.action_all_count = action_all_count;
    }

    public int getAction_succ_count() {
        return action_succ_count;
    }

    public void setAction_succ_count(int action_succ_count) {
        this.action_succ_count = action_succ_count;
    }

    public int getAction_err_count() {
        return action_err_count;
    }

    public void setAction_err_count(int action_err_count) {
        this.action_err_count = action_err_count;
    }

    public String getVesion() {
        return vesion;
    }

    public void setVesion(String vesion) {
        this.vesion = vesion;
    }

    public String getFtppath() {
        return ftppath;
    }

    public void setFtppath(String ftppath) {
        this.ftppath = ftppath;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDomino_server() {
        return domino_server;
    }

    public void setDomino_server(String domino_server) {
        this.domino_server = domino_server;
    }

    public String getDomino_dbpath() {
        return domino_dbpath;
    }

    public void setDomino_dbpath(String domino_dbpath) {
        this.domino_dbpath = domino_dbpath;
    }

    public String getDomino_query() {
        return domino_query;
    }

    public void setDomino_query(String domino_query) {
        this.domino_query = domino_query;
    }

    public String getDomino_error_flag_field() {
        return domino_error_flag_field;
    }

    public void setDomino_error_flag_field(String domino_error_flag_field) {
        this.domino_error_flag_field = domino_error_flag_field;
    }

    public String getSql_table() {
        return sql_table;
    }

    public void setSql_table(String sql_table) {
        this.sql_table = sql_table;
    }

    public ItemConfig getSql_field_attachment() {
        return sql_field_attachment;
    }

    public void setSql_field_attachment(ItemConfig sql_field_attachment) {
        this.sql_field_attachment = sql_field_attachment;
    }

    public ItemConfig[] getSql_field_others() {
        return sql_field_others;
    }

    public void setSql_field_others(ItemConfig[] sql_field_others) {
        this.sql_field_others = sql_field_others;
    }

    public String getDomino_before_prepared_driver() {
        return domino_before_prepared_driver;
    }

    public void setDomino_before_prepared_driver(String domino_before_prepared_driver) {
        this.domino_before_prepared_driver = domino_before_prepared_driver;
    }

    public String getDomino_after_prepared_driver() {
        return domino_after_prepared_driver;
    }

    public void setDomino_after_prepared_driver(String domino_after_prepared_driver) {
        this.domino_after_prepared_driver = domino_after_prepared_driver;
    }

    public JSONObject getExtended_options() {
        return extended_options;
    }

    public void setExtended_options(JSONObject extended_options) {
        this.extended_options = extended_options;
    }
}
