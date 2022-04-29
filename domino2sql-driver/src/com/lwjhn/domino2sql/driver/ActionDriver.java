package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.Base;

import java.sql.Connection;
import java.util.Vector;

public interface ActionDriver extends Base {
    void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception;

    void initDbConfig(DbConfig dbConfig);

    default void recycle(Vector vector) {
        BaseUtils.recycle(vector);
    }
}