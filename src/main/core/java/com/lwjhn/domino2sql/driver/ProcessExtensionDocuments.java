package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2sql.ArcDocument;
import com.lwjhn.domino2sql.Domino2SqlHelp;
import com.lwjhn.domino2sql.ProcessStatement;
import com.lwjhn.domino2sql.config.DbConfig;
import lotus.domino.*;
import sun.nio.cs.ext.MacHebrew;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class ProcessExtensionDocuments extends Message implements ProcessStatement {
    protected DbConfig dbConfig = null;
    private JSONObject extended_options = null;
    private DbConfig[] childDbConfig = null;
    private ArcDocument arcdoc = null;
    private String formula = null;
    private Iterable value = null;
    private final String show_log = "show_log";
    private final String throws_error_field = "throws_error";
    private boolean throws_error = true;

    @Override
    public void action(PreparedStatement preparedStatement, Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception {
        initConfig(dbConfig);
        if (childDbConfig == null) return;
        Vector<?> servers, dbpaths, queries;
        for (DbConfig dbcfg : childDbConfig) {
            if(!dbcfg.isEnable()) continue;
            servers=databaseCollection.getSession().evaluate(dbcfg.getDomino_server(), srcdoc);
            if (servers == null || servers.size()<1) {
                throwsError("processing children : parameter server is null !");
                continue;
            }
            //System.out.println("servers: "+Domino2SqlHelp.join(servers, ", ", null));
            dbpaths=databaseCollection.getSession().evaluate(dbcfg.getDomino_dbpath(), srcdoc);
            if (dbpaths == null || dbpaths.size()<1) {
                throwsError("processing children : parameter dbpath is null !");
                continue;
            }
            //System.out.println("dbpaths: "+Domino2SqlHelp.join(dbpaths, ", ", null));
            queries=databaseCollection.getSession().evaluate(dbcfg.getDomino_query(), srcdoc);
            if (queries == null || queries.size()<1) {
                throwsError("processing children : parameter query is null !");
                continue;
            }
            //System.out.println("queries: "+Domino2SqlHelp.join(queries, ", ", null));

            for(int i = 0; i < Math.max(Math.max(servers.size(), dbpaths.size()), queries.size()); i++){
                dbcfg.setDomino_server(formula = servers.get(i).toString());
                if (formula == null) {
                    throwsError("processing children : parameter server is null !");
                    continue;
                }
                this.dbgMsg("processing children : parameter server . " + formula);

                dbcfg.setDomino_dbpath(formula = dbpaths.get(i).toString());
                if (formula == null) {
                    throwsError("processing children : parameter dbpath is null !");
                    continue;
                }
                this.dbgMsg("processing children : parameter dbpath . " + formula);

                dbcfg.setDomino_query(formula = queries.get(i).toString());
                if (formula == null) {
                    throwsError("processing children : parameter dbpath is null !");
                    continue;
                }
                this.dbgMsg("processing children : parameter dbpath . " + formula);

                if (dbcfg.isEnable()) arcdoc.processing(dbcfg, connection, databaseCollection, mssdbc).recycle();
            }
        }
    }

    private void throwsError(String message) throws Exception {
        this.dbgMsg(message);
        if (dbConfig != null) dbConfig.setAction_error_log(message);
        if (throws_error) throw new Exception(message);
    }

    private String evaluate(String formula, Document doc, Session session) throws Exception {
        this.dbgMsg("ProcessExtensionDocuments :: formula -> " + formula == null ? "null" : formula);
        return (value = session.evaluate(formula, doc)) == null ? null : Domino2SqlHelp.join(value, null, null);
    }

    private void initConfig(DbConfig dbConfig) throws Exception {
        if (dbConfig == this.dbConfig) return;
        if ((extended_options = dbConfig.getExtended_options()) == null) {
            childDbConfig = null;
        } else {
            childDbConfig = extended_options.getObject("children", DbConfig[].class);
            if (arcdoc == null) arcdoc = new ArcDocument();
        }
        this.setDebug(extended_options.getBoolean(show_log));
        throws_error = extended_options.getBoolean(throws_error_field);
    }

    @Override
    public void recycle() throws Exception {
        BaseUtils.recycle(arcdoc);
    }
}
