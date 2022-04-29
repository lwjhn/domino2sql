package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.ItemConfig;

import java.sql.Connection;
import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.driver
 * @Version: 1.0
 */
public class PreparedSql4ExtractFile implements PreparedSqlDriver {
    protected DbConfig dbConfig = null;
    protected JSONObject extended_options = null;
    protected ItemConfig item_config_attachment = null;

    @Override
    public void action(List<String> names, List<String> values) throws Exception {
        if (item_config_attachment == null) return;
        names.add(item_config_attachment.getSql_name());
        values.add("?");
    }

    @Override
    public void recycle() {

    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) {

    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        this.dbConfig=dbConfig;
        item_config_attachment =null;
        if((extended_options = dbConfig.getExtended_options()) == null ) return;
        item_config_attachment = extended_options.getObject("sql_field_attachment", ItemConfig.class);
    }
}
