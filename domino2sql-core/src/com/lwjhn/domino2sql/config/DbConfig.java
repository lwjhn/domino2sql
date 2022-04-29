package com.lwjhn.domino2sql.config;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class DbConfig extends DominoQuery{
    private DominoQuery[] domino_queries = null;
    private String sql_table = null;
    private ItemConfig sql_update_primary_key = null;
    private ItemConfig[] sql_field_others = null;

    private String prepared_sql_driver = null;
    private String before_action_driver = null;
    private String on_action_driver = null;
    private String after_action_driver = null;
    private JSONObject extended_options = null;

    public DbConfig(String domino_server, String domino_dbpath, String domino_query) {
        super(domino_server, domino_dbpath, domino_query);
    }

    public DbConfig() {
    }

    public ItemConfig getSql_update_primary_key() {
        return sql_update_primary_key;
    }

    public void setSql_update_primary_key(ItemConfig sql_update_primary_key) {
        this.sql_update_primary_key = sql_update_primary_key;
    }

    public DominoQuery[] getDomino_queries() {
        return domino_queries;
    }

    public void setDomino_queries(DominoQuery[] domino_queries) {
        this.domino_queries = domino_queries;
    }

    public String getSql_table() {
        return sql_table;
    }

    public void setSql_table(String sql_table) {
        this.sql_table = sql_table;
    }

    public ItemConfig[] getSql_field_others() {
        return sql_field_others;
    }

    public void setSql_field_others(ItemConfig[] sql_field_others) {
        this.sql_field_others = sql_field_others;
    }

    public String getBefore_action_driver() {
        return before_action_driver;
    }

    public void setBefore_action_driver(String before_action_driver) {
        this.before_action_driver = before_action_driver;
    }

    public String getAfter_action_driver() {
        return after_action_driver;
    }

    public void setAfter_action_driver(String after_action_driver) {
        this.after_action_driver = after_action_driver;
    }

    public String getPrepared_sql_driver() {
        return prepared_sql_driver;
    }

    public void setPrepared_sql_driver(String prepared_sql_driver) {
        this.prepared_sql_driver = prepared_sql_driver;
    }

    public String getOn_action_driver() {
        return on_action_driver;
    }

    public void setOn_action_driver(String on_action_driver) {
        this.on_action_driver = on_action_driver;
    }

    public JSONObject getExtended_options() {
        return extended_options;
    }

    public void setExtended_options(JSONObject extended_options) {
        this.extended_options = extended_options;
    }

    @Override
    public DbConfig clone() throws CloneNotSupportedException {
        return (DbConfig) super.clone();
    }
}
