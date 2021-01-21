package com.lwjhn.domino2sql.config;

import java.sql.JDBCType;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class ReadersMap {
    private String sql_field = null;
    private JDBCType sql_jdbc_type = JDBCType.VARCHAR;
    private int sql_scale_length = 0;
    private String[] domino_field = null;
    private String mapping_table = null;
    private String mapping_domino = null;
    private String mapping_xc = null;

    public ReadersMap(String sql_field, JDBCType sql_jdbc_type) {
        this.sql_field = sql_field;
        this.sql_jdbc_type = sql_jdbc_type;
    }

    public ReadersMap(String sql_field, JDBCType sql_jdbc_type, int sql_scale_length) {
        this.sql_field = sql_field;
        this.sql_jdbc_type = sql_jdbc_type;
        this.sql_scale_length = sql_scale_length;
    }

    public ReadersMap(String sql_field, JDBCType sql_jdbc_type, int sql_scale_length, String[] domino_field, String mapping_table, String mapping_domino, String mapping_xc) {
        this.sql_field = sql_field;
        this.sql_jdbc_type = sql_jdbc_type;
        this.sql_scale_length = sql_scale_length;
        this.domino_field = domino_field;
        this.mapping_table = mapping_table;
        this.mapping_domino = mapping_domino;
        this.mapping_xc = mapping_xc;
    }

    public ReadersMap() {
    }

    public JDBCType getSql_jdbc_type() {
        return sql_jdbc_type;
    }

    public void setSql_jdbc_type(JDBCType sql_jdbc_type) {
        this.sql_jdbc_type = sql_jdbc_type;
    }

    public int getSql_scale_length() {
        return sql_scale_length;
    }

    public void setSql_scale_length(int sql_scale_length) {
        this.sql_scale_length = sql_scale_length;
    }

    public String getSql_field() {
        return sql_field;
    }

    public void setSql_field(String sql_field) {
        this.sql_field = sql_field;
    }

    public String[] getDomino_field() {
        return domino_field;
    }

    public void setDomino_field(String[] domino_field) {
        this.domino_field = domino_field;
    }

    public String getMapping_table() {
        return mapping_table;
    }

    public void setMapping_table(String mapping_table) {
        this.mapping_table = mapping_table;
    }

    public String getMapping_domino() {
        return mapping_domino;
    }

    public void setMapping_domino(String mapping_domino) {
        this.mapping_domino = mapping_domino;
    }

    public String getMapping_xc() {
        return mapping_xc;
    }

    public void setMapping_xc(String mapping_xc) {
        this.mapping_xc = mapping_xc;
    }
}
