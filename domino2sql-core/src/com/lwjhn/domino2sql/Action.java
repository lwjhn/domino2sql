package com.lwjhn.domino2sql;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.ArcConfig;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.Session;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class Action extends ArcBase {
    private Connection connection = null;
    private final ArcConfig arcConfig;
    private final DatabaseCollection databaseCollection;
    private final Task task;

    public Action(ArcConfig arcConfig, Session session) throws Exception {
        this(arcConfig, session, 10);
    }

    public Action(ArcConfig arcConfig, Session session, int dbPoolSize) throws Exception {
        if (session == null) throw new Exception("parameter session is null ! ");
        this.arcConfig = arcConfig;
        checkArcConfig();
        createConnection();
        databaseCollection = new DatabaseCollection(session, dbPoolSize);
        task = new Task();
        this.setDebug(arcConfig.getDebugger());
    }

    public void checkArcConfig() throws Exception {
        if (this.arcConfig == null) throw new Exception("parameter arcConfig is null ! ");
        if (arcConfig.getSql_driver() == null) throw new Exception("arcConfig:: sql_driver is null ! ");
        if (arcConfig.getSql_url() == null) throw new Exception("arcConfig:: sql_url is null ! ");
        if (arcConfig.getSql_username() == null) throw new Exception("arcConfig:: sql_username is null ! ");
        if (arcConfig.getSql_password() == null) throw new Exception("arcConfig:: sql_password is null ! ");
        if (arcConfig.getOptions() == null) throw new Exception("arcConfig:: options is null ! ");
    }

    private void createConnection() throws Exception {
        Class.forName(arcConfig.getSql_driver());
        DriverManager.setLogWriter(arcConfig.getDriver_manager_log() ? new PrintWriter(System.out) : null);
        this.connection = DriverManager.getConnection(arcConfig.getSql_url(), arcConfig.getSql_username(), arcConfig.getSql_password());
        if(arcConfig.getDriver_manager_log()) this.dbgMsg("connection is Valid ? " + (connection != null && connection.isValid(6000)));
    }

    public void archive() {
        for (DbConfig dbConfig : arcConfig.getOptions()) {
            if (dbConfig.isEnable()) {
                task.processing(dbConfig, connection, databaseCollection).recycle();
            }
        }
    }

    public void recycle() {
        close(connection);
        recycle(task, databaseCollection);
    }
}
