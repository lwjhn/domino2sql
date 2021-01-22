package com.zjjt.prepare;

import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.PreparedSqlQuery;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import lotus.domino.NotesException;

import java.sql.Connection;
import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class PrepareSqlQueryZJJT implements PreparedSqlQuery {
    private DbConfig dbConfig = null;
    private JSONObject extended_options = null;
    private ItemConfig item_config_attachment = null;

    @Override
    public void action(List<String> names, List<String> values, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception {
        initConfig(dbConfig, databaseCollection);
        if (item_config_attachment == null) return;
        names.add(item_config_attachment.getSql_name());
        values.add("?");
    }

    private void initConfig(DbConfig dbConfig, DatabaseCollection databaseCollection) throws Exception{
        if(dbConfig == this.dbConfig) return;
        if((extended_options = dbConfig.getExtended_options()) == null ) return;
        item_config_attachment = extended_options.getObject("sql_field_attachment", ItemConfig.class);
    }
    @Override
    public void recycle() throws Exception {

    }
}
