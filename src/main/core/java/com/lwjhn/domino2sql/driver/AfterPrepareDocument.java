package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.PreparedDocument;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.Document;
import lotus.domino.NotesException;

import java.sql.Connection;

/**
 * @Author: lwjhn
 * @Date: 2020-11-24
 * @Description: com.lwjhn.test
 * @Version: 1.0
 */
public class AfterPrepareDocument implements PreparedDocument {
    @Override
    public void action(Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException {
        System.out.println("Test AfterPrepareDocument.action : "+ srcdoc.getUniversalID());
    }

    public void recycle() throws Exception{
        System.out.println("Test AfterPrepareDocument.recycle ");
    }
}
