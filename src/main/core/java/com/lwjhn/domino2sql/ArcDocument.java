package com.lwjhn.domino2sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import com.lwjhn.util.FileOperator;
import lotus.domino.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcDocument extends ArcBase {
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    private Session session = null;
    private Database db = null;
    private DocumentCollection dc = null;
    private Document doc = null, tdoc = null;
    private int succ_ct = 0, err_ct = 0;
    private String query = null;
    private String error_flag_field = null, succ_flag_field = null, version = null;

    private PreparedDocument beforePreparedMethod = null;
    private PreparedDocument afterPreparedMethod = null;
    private ProcessStatement processStatement = null;
    private PreparedSqlQuery preparedSqlQuery = null;

    public ArcDocument processing(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        try {
            this.setDebug(dbConfig.getDebugger());
            dbgMsg("start to archive database : " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath());
            prepareSql(dbConfig, connection, databaseCollection, mssdbc);
            searchDb(dbConfig, databaseCollection);
            session = databaseCollection.getSession();  //using lotus formula .

            dbgMsg("search database return document count : " + dc.getCount() + " ! " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath() + "\n formula: " + query);
            dbConfig.setAction_all_count(dc.getCount());
            dbConfig.setAction_err_count(err_ct = 0);
            dbConfig.setAction_succ_count(succ_ct = 0);
            doc = dc.getFirstDocument();
            while (doc != null) {
                if (beforePreparedMethod != null) {
                    beforePreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);
                    beforePreparedMethod.recycle();
                }
                archive(doc, dbConfig, connection, databaseCollection, mssdbc);
                if (afterPreparedMethod != null) {
                    afterPreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);
                    afterPreparedMethod.recycle();
                }
                doc = dc.getNextDocument(tdoc = doc);
                recycle(tdoc);
                tdoc = null;
            }
            dbConfig.setAction_err_count(err_ct);
            dbConfig.setAction_succ_count(succ_ct);
            dbConfig.setAction_error_log(null);
            dbgMsg("end to archive database : " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath());
        } catch (Exception e) {
            dbConfig.setAction_error_log(dbgMsg(getStackMsg(e)));
        } finally {
            recycle(tdoc, doc, dc);
            tdoc = null;
            doc = null;
            dc = null;
            session = null;
        }
        return this;
    }

    private void archive(Document doc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        String formula;
        Object value;
        Item item = null;
        int index = 0;
        try {
            doc.replaceItemValue(DefaultConfig.Domino_UUID_Prefix + "16", ArcUtils.getUUID16());
            doc.replaceItemValue(DefaultConfig.Domino_UUID_Prefix + "32", ArcUtils.getUUID32());
            for (ItemConfig itemConfig : dbConfig.getSql_field_others()) {
                if ((formula = itemConfig.getDomino_name()) != null) {
                    recycle(item);
                    value = Domino2SqlHelp.domino2JdbcType(item = doc.getFirstItem(formula), itemConfig);
                } else if ((formula = itemConfig.getDomino_formula()) != null && !"".equals(formula)) {
                    value = Domino2SqlHelp.domino2JdbcType(session.evaluate(formula, doc), itemConfig);
                } else {
                    value = null;
                }
                preparedStatement.setObject(++index, value, itemConfig.getJdbc_type().getVendorTypeNumber(), itemConfig.getScale_length());
            }

            if (processStatement != null)
                processStatement.action(preparedStatement, doc, dbConfig, connection, databaseCollection, mssdbc);

            if (preparedStatement.executeUpdate() < 1)
                throw new Exception("the row count for SQL statements return nothing or 0 .");
            doc.removeItem(error_flag_field);
            doc.replaceItemValue(succ_flag_field, version);
            doc.save(true, false);
        } catch (Exception e) {
            dbConfig.setAction_error_log(dbgMsg(getStackMsg(e)));
            setDocErrLog(doc, dbConfig.getAction_error_log());
        } finally {
            recycle(item);
        }
    }

    private void prepareSql(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws Exception {
        if ((error_flag_field = dbConfig.getDomino_error_flag_field()) == null || !DefaultConfig.PATTERN_NAME.matcher(error_flag_field).matches())
            error_flag_field = DefaultConfig.Domino_Error_Flag_Field;
        if ((succ_flag_field = dbConfig.getDomino_succ_flag_field()) == null || !DefaultConfig.PATTERN_NAME.matcher(succ_flag_field).matches())
            succ_flag_field = DefaultConfig.Domino_Succ_Flag_Field;

        if ((version = dbConfig.getVesion()) == null)
            version = DefaultConfig.VERSION;

        String name;
        afterPreparedMethod = (name = dbConfig.getDomino_after_prepared_driver()) == null || "".equals(name) ?
                null : (PreparedDocument) Class.forName(name).newInstance();
        beforePreparedMethod = (name = dbConfig.getDomino_before_prepared_driver()) == null || "".equals(name) ?
                null : (PreparedDocument) Class.forName(name).newInstance();
        processStatement = (name = dbConfig.getDomino_process_statement_driver()) == null || "".equals(name) ?
                null : (ProcessStatement) Class.forName(name).newInstance();

        if ((name = dbConfig.getSql_table()) == null || !DefaultConfig.PATTERN_TABLE.matcher(name).matches())
            throw new Exception("prepareSql:: parameter sql_table is null or non-standard ! ");

        ItemConfig[] itemConfigs = dbConfig.getSql_field_others();
        if (itemConfigs == null || itemConfigs.length < 1)
            throw new Exception("prepareSql:: sql_field_others is null , or size is 0 ! ");

        List<String> names = new ArrayList<>(), values = new ArrayList<>();
        for (ItemConfig itemConfig : itemConfigs) {
            if ((name = itemConfig.getSql_name()) == null || !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: itemConfig sql_name is null or non-standard ! " + (name == null ? "" : name));
            names.add(name);
            values.add("?");
            if (itemConfig.getJdbc_type() == null)
                throw new Exception("prepareSql:: itemConfig jdbc_type is null or non-standard ! " + (name));
            if (itemConfig.getScale_length() < 1) itemConfig.setScale_length(0);
            if ((name = itemConfig.getDomino_name()) != null && !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: itemConfig domino_name is null or non-standard ! " + (name));
        }

        preparedSqlQuery = (name = dbConfig.getDomino_prepared_sqlquery_driver()) == null || "".equals(name) ?
                null : (PreparedSqlQuery) Class.forName(name).newInstance();
        if (preparedSqlQuery != null) {
            preparedSqlQuery.action(names, values, dbConfig, connection, databaseCollection, mssdbc);
            preparedSqlQuery.recycle();
        }

        close(preparedStatement);
        preparedStatement = connection.prepareStatement(
                name = "INSERT INTO " + dbConfig.getSql_table() + " (" + String.join(", ", names) + ") "
                        + "VALUES (" + String.join(", ", values) + ")"
        );
        dbgMsg(name);
    }

    private void searchDb(DbConfig dbConfig, DatabaseCollection databaseCollection) throws Exception {
        db = databaseCollection.getDatabase(dbConfig.getDomino_server(), dbConfig.getDomino_dbpath());
        if (db == null || !db.isOpen()) {
            throw new Exception("can't open database ! " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath());
        }
        if ((query = dbConfig.getDomino_query()) == null) {
            throw new Exception("can not find Domino_query field at database config ! " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath());
        }
        recycle(dc);
        dc = null;
        dc = db.search(query, null, 0);
    }

    private void setDocErrLog(Document doc, String msg) {
        if (doc == null) return;
        try {
            doc.replaceItemValue(error_flag_field, msg);
        } catch (Exception e) {
        }
    }

    public void recycle() {
        try {
            if (beforePreparedMethod != null) beforePreparedMethod.recycle();
        } catch (Exception e) {
        }
        try {
            if (afterPreparedMethod != null) afterPreparedMethod.recycle();
        } catch (Exception e) {
        }
        close(resultSet, preparedStatement);
        resultSet = null;
        preparedStatement = null;
        recycle(tdoc, doc, dc);
        tdoc = null;
        doc = null;
        dc = null;
    }
}
