package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.Document;

import java.sql.Connection;

/**
 * @Author: lwjhn
 * @Date: 2020-11-24
 * @Description: com.lwjhn.test
 * @Version: 1.0
 */
public class ActionShowLog implements BeforeActionDriver {
    public void recycle() {
        System.out.println("ActionShowLog.recycle ");
    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception {

    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {

    }

    @Override
    public void action(Document srcdoc) throws Exception {
        System.out.println("ActionShowLog.action : "+ srcdoc.getUniversalID());
    }
}
