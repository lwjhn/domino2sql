package com.lwjhn.domino2sql.driver;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.util.AutoCloseableBase;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

public abstract class ActionDriverFormula extends Message implements ActionDriver {
    protected Session session;
    protected Connection connection = null;
    protected Statement statement = null;

    public abstract String sqlFormula();

    public void action(Document doc) throws Exception {
        if (StringUtils.isNoneBlank(sqlFormula())) {
            try {
                String Formula;
                Vector<?> vector = session.evaluate(sqlFormula(), doc);
                for (Object v : vector) {
                    if (StringUtils.isBlank(Formula = (String) v))
                        continue;
                    this.dbgMsg(this.getClass().getName() + ".action: " + Formula);
                    this.dbgMsg("statement result: " + statement.execute(Formula));
                }
                this.dbgMsg(this.getClass().getName() + ".action: success. " + doc.getUniversalID());
            } catch (Exception e) {
                this.dbgMsg(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception {
        session = databaseCollection.getSession();
        this.setDebug(dbConfig.isDebugger());
        this.connection = connection;
        AutoCloseableBase.close(statement);
        try {
            statement = connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void recycle() throws NotesException {
        AutoCloseableBase.close(statement);
        statement = null;
    }
}
