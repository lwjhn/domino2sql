package com.lwjhn.domino2sql.config;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class DbConfig extends DominoQuery {
    private DominoQuery[] domino_queries = null;
    private String sql_table = null;
    private ItemConfig sql_update_primary_key = null;
    private ItemConfig[] sql_field_others = null;

    private String domino_before_prepared_driver = null;
    private String domino_after_prepared_driver = null;
    private String domino_prepared_sqlquery_driver = null;
    private String domino_process_statement_driver = null;
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

    public String getDomino_prepared_sqlquery_driver() {
        return domino_prepared_sqlquery_driver;
    }

    public void setDomino_prepared_sqlquery_driver(String domino_prepared_sqlquery_driver) {
        this.domino_prepared_sqlquery_driver = domino_prepared_sqlquery_driver;
    }

    public String getDomino_process_statement_driver() {
        return domino_process_statement_driver;
    }

    public void setDomino_process_statement_driver(String domino_process_statement_driver) {
        this.domino_process_statement_driver = domino_process_statement_driver;
    }

    public JSONObject getExtended_options() {
        return extended_options;
    }

    public void setExtended_options(JSONObject extended_options) {
        this.extended_options = extended_options;
    }
}
