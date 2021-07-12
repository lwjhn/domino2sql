package com.lwjhn.domino2sql;

import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.DominoQuery;
import com.lwjhn.domino2sql.config.ItemConfig;
import lotus.domino.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcDocument extends ArcBase {
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatementCheck = null;
    PreparedStatement preparedStatementUpdate = null;
    ResultSet resultSet = null;
    private Session session = null;
    private Database db = null;
    private DocumentCollection dc = null;
    private Document doc = null, tdoc = null;
    private int succ_ct = 0, err_ct = 0;
    private String error_flag_field = null, succ_flag_field = null, version = null;

    private PreparedDocument beforePreparedMethod = null;
    private PreparedDocument afterPreparedMethod = null;
    private ProcessStatement processStatement = null;
    private List<String> sqlFieldNames = null;

    public ArcDocument processing(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        try {
            this.setDebug(dbConfig.isDebugger());
            prepareSql(dbConfig, connection, databaseCollection, mssdbc);
            session = databaseCollection.getSession();  //using lotus formula .

            DominoQuery[] dominoQueries = dbConfig.getDomino_queries();
            if (dbConfig.getDomino_server() != null && dbConfig.getDomino_dbpath() != null && dbConfig.getDomino_query() != null) {
                if (dominoQueries == null || dominoQueries.length < 1) {
                    if (dbConfig.isEnable()) processLoop(dbConfig, connection, databaseCollection, mssdbc);
                    return this;
                }
            }
            if (dominoQueries == null) return this;

            DominoQuery defaultQuery = dbConfig.clone(), initQuery = new DominoQuery();
            Object val;
            for (DominoQuery dominoQuery : dominoQueries) {
                if (!dominoQuery.isEnable()) continue;
                for (Field field : DominoQuery.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    if ((val = field.get(dominoQuery)) == field.get(initQuery))
                        val = field.get(defaultQuery);
                    field.set(dbConfig, val);
                }

                processLoop(dbConfig, connection, databaseCollection, mssdbc);

                for (Field field : DominoQuery.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(dominoQuery, field.get(dbConfig));
                }
            }

            for (Field field : DominoQuery.class.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(dbConfig, field.get(defaultQuery));
            }
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

    private void processLoop(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        String server = null, dbpath = null, query = null;
        try {
            this.setDebug(dbConfig.isDebugger());
            if ((server = dbConfig.getDomino_server()) == null || (dbpath = dbConfig.getDomino_dbpath()) == null || (query = dbConfig.getDomino_query()) == null)
                return;
            dbgMsg("start to archive default database : " + server + " !! " + dbpath);
            searchDb(server, dbpath, query, databaseCollection);
            if (dc == null)
                throw new Exception("the document colllection is null , not initialization !");

            dbgMsg("search database return document count : " + dc.getCount() + " ! " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath() + "\n formula: " + dbConfig.getDomino_query());
            dbConfig.setAction_err_count(err_ct = 0);
            dbConfig.setAction_succ_count(succ_ct = 0);
            dbConfig.setAction_all_count(dc.getCount());
            doc = dc.getFirstDocument();
            while (doc != null) {
                if (beforePreparedMethod != null) {
                    beforePreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);
                    beforePreparedMethod.recycle();
                }
                if (archive(doc, dbConfig, connection, databaseCollection, mssdbc)) {
                    if (afterPreparedMethod != null) {
                        afterPreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);
                        afterPreparedMethod.recycle();
                    }
                    succ_ct++;
                } else {
                    err_ct++;
                    if (!dbConfig.isError_continue())
                        return;
                }
                doc = dc.getNextDocument(tdoc = doc);
                recycle(tdoc);
                tdoc = null;
            }
            dbConfig.setAction_error_log(null);
        } catch (Exception e) {
            dbConfig.setAction_error_log(dbgMsg(getStackMsg(e)));
        } finally {
            if (server != null && dbpath != null && query != null) {
                dbConfig.setAction_err_count(err_ct);
                dbConfig.setAction_succ_count(succ_ct);
                dbgMsg(
                        "end to archive default database : " + server + " !! " + dbpath + "!!  formula : " + query + (dc != null ?
                                "all count is " + dbConfig.getAction_all_count() + ", " + "success count is " + succ_ct + ", " + "error count is " + err_ct : ""
                        )
                );
            }
            recycle(tdoc, doc, dc);
            tdoc = null;
            doc = null;
            dc = null;
        }
    }

    private PreparedStatement getPreparedStatement(Document doc, DbConfig dbConfig) throws Exception {
        String formula;
        Object value = null;
        Item item = null;
        try {
            if (preparedStatementCheck == null || preparedStatementUpdate == null)
                return preparedStatement;
            ItemConfig itemConfig = dbConfig.getSql_update_primary_key();
            if ((formula = itemConfig.getDomino_name()) != null) {
                value = Domino2SqlHelp.domino2JdbcType(item = doc.getFirstItem(formula), itemConfig);
            } else if ((formula = itemConfig.getDomino_formula()) != null && !"".equals(formula)) {
                value = Domino2SqlHelp.domino2JdbcType(session.evaluate(formula, doc), itemConfig);
            }
            if (value == null){
                if(dbConfig.isUpdate_mode_no_insert()){
                    this.dbgMsg("udpate mdoe: primary key " + itemConfig.getSql_name() + ", demonio value is null" + String.valueOf(value));
                    return null;
                }else{
                    return preparedStatement;
                }
            }

            preparedStatementCheck.setObject(1, value, itemConfig.getJdbc_type().getVendorTypeNumber(), itemConfig.getScale_length());
            ResultSet resultSet = preparedStatementCheck.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) > 0) {
                if(resultSet.getInt(1)>1){
                    this.dbgMsg("udpate mdoe: multiple record error , find count(" + resultSet.getInt(1) +") relative doc by primary key " + itemConfig.getSql_name() + " = " + value);
                    return null;
                }
                preparedStatementUpdate.setObject(sqlFieldNames.size() + 1, value);
                return preparedStatementUpdate;
            }else if(dbConfig.isUpdate_mode_no_insert()){
                this.dbgMsg("udpate mdoe: can't find relative doc by primary key " + itemConfig.getSql_name() + " = " + value);
                return null;
            }else{
                doc.removeItem(dbConfig.getDomino_uuid_prefix() + "8");
                doc.removeItem(dbConfig.getDomino_uuid_prefix() + "16");
                doc.removeItem(dbConfig.getDomino_uuid_prefix() + "32");
                return preparedStatement;
            }
        } finally {
            if(!doc.hasItem(dbConfig.getDomino_uuid_prefix() + "8"))
                doc.replaceItemValue(dbConfig.getDomino_uuid_prefix() + "8", ArcUtils.getUUID8());
            if(!doc.hasItem(dbConfig.getDomino_uuid_prefix() + "16"))
                doc.replaceItemValue(dbConfig.getDomino_uuid_prefix() + "16", ArcUtils.getUUID16());
            if(!doc.hasItem(dbConfig.getDomino_uuid_prefix() + "32"))
                doc.replaceItemValue(dbConfig.getDomino_uuid_prefix() + "32", ArcUtils.getUUID32());
            recycle(item);
        }
    }

    private boolean archive(Document doc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        String formula;
        Object value;
        Item item = null;
        int index = 0;
        try {
            doc.replaceItemValue(succ_flag_field + "_OR_Error_FLAG_Time", session.createDateTime(new Date()));
            PreparedStatement preparedStatement = getPreparedStatement(doc, dbConfig);
            if(preparedStatement==null && dbConfig.isUpdate_mode_no_insert()){
                return true;
            }
            for (ItemConfig itemConfig : dbConfig.getSql_field_others()) {
                if ((formula = itemConfig.getDomino_name()) != null) {
                    recycle(item);
                    value = Domino2SqlHelp.domino2JdbcType(item = doc.getFirstItem(formula), itemConfig);
                } else if ((formula = itemConfig.getDomino_formula()) != null && !"".equals(formula)) {
                    value = Domino2SqlHelp.domino2JdbcType(session.evaluate(formula, doc), itemConfig);
                } else {
                    value = null;
                }
                try {
                    if (value instanceof String && itemConfig.getScale_length() > 0 && ((String) value).length() > itemConfig.getScale_length())
                        throw new Exception("value too long . string length is " + ((String) value).length() + " , scale length is " + itemConfig.getScale_length() + ".");
                    preparedStatement.setObject(++index, value, itemConfig.getJdbc_type().getVendorTypeNumber(), itemConfig.getScale_length());
                } catch (Exception setError) {
                    throw itemConfig.getSql_name() != null
                            ? new Exception("at sql_name of " + itemConfig.getSql_name() + System.lineSeparator() + " error: " + setError.getMessage(), setError.getCause())
                            : setError;
                }
            }

            if (processStatement != null)
                processStatement.action(preparedStatement, doc, dbConfig, connection, databaseCollection, mssdbc);

            if (preparedStatement.executeUpdate() < 1)
                throw new Exception("the row count for SQL statements return nothing or 0 .");
            doc.removeItem(error_flag_field);
            doc.replaceItemValue(succ_flag_field, version);
            doc.save(true, false);
            return true;
        } catch (Exception e) {
            dbConfig.setAction_error_log(dbgMsg(getStackMsg(e)));
            setDocErrLog(doc, dbConfig.getAction_error_log());
            return false;
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
                throw new Exception("prepareSql:: itemConfig [sql_name] is null or non-standard ! " + (name == null ? "" : name));
            names.add(name);
            values.add("?");
            if (itemConfig.getJdbc_type() == null)
                throw new Exception("prepareSql:: itemConfig [jdbc_type] is null or non-standard ! " + (name));
            if (itemConfig.getScale_length() < 1) itemConfig.setScale_length(0);
            if ((name = itemConfig.getDomino_name()) != null && !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: itemConfig [domino_name] is non-standard ! " + (name));
        }

        PreparedSqlQuery preparedSqlQuery = (name = dbConfig.getDomino_prepared_sqlquery_driver()) == null || "".equals(name) ?
                null : (PreparedSqlQuery) Class.forName(name).newInstance();
        if (preparedSqlQuery != null) {
            preparedSqlQuery.action(names, values, dbConfig, connection, databaseCollection, mssdbc);
            preparedSqlQuery.recycle();
        }

        close(preparedStatement, preparedStatementCheck, preparedStatementUpdate);
        preparedStatement = preparedStatementCheck = preparedStatementUpdate = null;
        preparedStatement = connection.prepareStatement(
                name = "INSERT INTO " + dbConfig.getSql_table() + " (" + String.join(", ", names) + ") "
                        + "VALUES (" + String.join(", ", values) + ")"
        );
        dbgMsg(name);

        ItemConfig primaryKey = dbConfig.getSql_update_primary_key();
        if (primaryKey != null) {
            if ((name = primaryKey.getSql_name()) == null || !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: the proce for update cmd, sql_update_primary_key [sql_name] is null or non-standard ! " + (name == null ? "" : name));

            if (primaryKey.getJdbc_type() == null)
                throw new Exception("prepareSql:: the proce for update cmd, sql_update_primary_key [jdbc_type] is null or non-standard ! " + (name));
            if (primaryKey.getScale_length() < 1) primaryKey.setScale_length(0);
            if ((name = primaryKey.getDomino_name()) != null && !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: the proce for update cmd, sql_update_primary_key [domino_name] is non-standard ! " + (name));

            preparedStatementCheck = connection.prepareStatement(name = "select count(*) from dual where exists(select 0 from " + dbConfig.getSql_table() + " where " + primaryKey.getSql_name() + "=?)");
            dbgMsg(name);
            preparedStatementUpdate = connection.prepareStatement(name = "UPDATE " + dbConfig.getSql_table() + " SET " + String.join("=? , ", names) + "=? WHERE " + primaryKey.getSql_name() + "=?");
            dbgMsg(name);
        }
        sqlFieldNames = names;
    }

    private void searchDb(String server, String dbname, String query, DatabaseCollection databaseCollection) throws Exception {
        db = databaseCollection.getDatabase(server, dbname);
        if (db == null || !db.isOpen()) {
            throw new Exception("can't open database ! " + server + " !! " + dbname);
        }
        if (query == null)
            throw new Exception("can not find Domino_query field at database config ! " + server + " !! " + dbname);

        recycle(dc);
        dc = null;
        dc = db.search(query, null, 0);
    }

    private void setDocErrLog(Document doc, String msg) {
        if (doc == null) return;
        try {
            doc.replaceItemValue(error_flag_field, msg);
            doc.save(true, false);
        } catch (Exception ignored) {
        }
    }

    public void recycle() {
        try {
            if (beforePreparedMethod != null) beforePreparedMethod.recycle();
        } catch (Exception ignored) {
        }
        try {
            if (afterPreparedMethod != null) afterPreparedMethod.recycle();
        } catch (Exception ignored) {
        }
        close(resultSet, preparedStatement, preparedStatementCheck, preparedStatementUpdate);
        resultSet = null;
        preparedStatement = preparedStatementCheck = preparedStatementUpdate = null;
        recycle(tdoc, doc, dc);
        tdoc = null;
        doc = null;
        dc = null;
    }
}
