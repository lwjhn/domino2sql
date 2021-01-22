package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.PreparedSqlQuery;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.NotesException;

import java.sql.Connection;
import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class PrepareSqlQuery implements PreparedSqlQuery {
    @Override
    public void action(List<String> names, List<String> values, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception {
        System.out.println("Test PrepareSqlQuery.action . ");
    }

    @Override
    public void recycle() throws Exception {
        System.out.println("Test PrepareSqlQuery.recycle . ");
    }
}
