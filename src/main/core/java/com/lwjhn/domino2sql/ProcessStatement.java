package com.lwjhn.domino2sql;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.Document;
import lotus.domino.NotesException;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public interface ProcessStatement {
    public void action(PreparedStatement preparedStatement, Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception;
    public void recycle() throws Exception;
}
