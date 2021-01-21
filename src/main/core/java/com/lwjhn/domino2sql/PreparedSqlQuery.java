package com.lwjhn.domino2sql;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.NotesException;

import java.sql.Connection;
import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2020-11-24
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public interface PreparedSqlQuery {
    public void action(List<String> names , List<String> values , DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception;
    public void recycle() throws Exception;
}
