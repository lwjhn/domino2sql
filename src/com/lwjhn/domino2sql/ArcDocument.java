package com.lwjhn.domino2sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.FileOperator;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
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
    ItemConfig item_config_attachment = null;
    private Session session = null;
    private Database db = null;
    private DocumentCollection dc = null;
    private Document doc = null, tdoc = null;
    private int succ_ct = 0, err_ct = 0;
    private String query = null;
    private String error_flag_field = null, succ_flag_field = null, ftppath = null, version = null;

    private PreparedDocument beforePreparedMethod = null;
    private PreparedDocument afterPreparedMethod = null;

    public ArcDocument processing(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) {
        try {
            dbgMsg("start to archive database : " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath());
            prepareSql(dbConfig, connection);
            searchDb(dbConfig, databaseCollection);
            session = databaseCollection.getSession();  //using lotus formula .

            dbgMsg("search database return document count : " + dc.getCount() + " ! " + dbConfig.getDomino_server() + " !! " + dbConfig.getDomino_dbpath() + "\n formula: " + query);
            dbConfig.setAction_all_count(dc.getCount());
            dbConfig.setAction_err_count(err_ct = 0);
            dbConfig.setAction_succ_count(succ_ct = 0);
            doc = dc.getFirstDocument();
            while (doc != null) {
                if (beforePreparedMethod != null)
                    beforePreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);
                archive(dbConfig, doc, mssdbc);
                if (afterPreparedMethod != null)
                    afterPreparedMethod.action(doc, dbConfig, connection, databaseCollection, mssdbc);

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

    private void archive(DbConfig dbConfig, Document doc, DatabaseCollection mssdbc) {
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
                    value = domino2JdbcType(item = doc.getFirstItem(formula), itemConfig);
                    //value = doc.hasItem(formula) ? domino2JdbcType(doc.getItemValue(formula), itemConfig) : null;
                } else if ((formula = itemConfig.getDomino_formula()) != null && !"".equals(formula)) {
                    value = domino2JdbcType(session.evaluate(formula, doc), itemConfig);
                } else {
                    value = null;
                }
                preparedStatement.setObject(++index, value, itemConfig.getJdbc_type().getVendorTypeNumber(), itemConfig.getScale_length());
            }
            if (item_config_attachment != null)
                preparedStatement.setObject(++index, arcMssFiles(doc, mssdbc).toJSONString(), item_config_attachment.getJdbc_type().getVendorTypeNumber(), item_config_attachment.getScale_length());

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

    private boolean isReturnAllValues(JDBCType jdbcType) {
        return !(jdbcType == JDBCType.INTEGER || jdbcType == JDBCType.BIGINT
                || jdbcType == JDBCType.TINYINT || jdbcType == JDBCType.SMALLINT
                || jdbcType == JDBCType.NUMERIC || jdbcType == JDBCType.FLOAT
                || jdbcType == JDBCType.DOUBLE || jdbcType == JDBCType.DECIMAL
                || jdbcType == JDBCType.BIT || jdbcType == JDBCType.BOOLEAN
                || jdbcType == JDBCType.TIME || jdbcType == JDBCType.DATE
                || jdbcType == JDBCType.TIMESTAMP || jdbcType == JDBCType.TIME_WITH_TIMEZONE || jdbcType == JDBCType.TIMESTAMP_WITH_TIMEZONE);
    }

    private Object domino2JdbcType(Vector values, ItemConfig itemConfig) throws Exception {
        if (values == null || values.size() < 1) return null;
        JDBCType jdbcType = itemConfig.getJdbc_type();
        Object parameterObj = values.get(0);
        if (parameterObj instanceof String || parameterObj instanceof Double) {
            return isReturnAllValues(jdbcType) ? join(values, itemConfig.getJoin_delimiter(), null) : parameterObj;
        } else if (parameterObj instanceof lotus.domino.DateTime) {
            return isReturnAllValues(jdbcType) ? join(values, itemConfig.getJoin_delimiter(), itemConfig.getDate_format())
                    : new Timestamp(((lotus.domino.DateTime) parameterObj).toJavaDate().getTime());
        } else {
            throw new Exception("domino2JdbcType:: this domino item's value is abnormality ! " + parameterObj.getClass().getName());
        }
    }

    private Object domino2JdbcType(Item item, ItemConfig itemConfig) throws Exception {
        if (item == null) return null;
        switch (item.getType()) {
            case Item.RICHTEXT:
                return ((RichTextItem) item).getUnformattedText();
            default:
                return domino2JdbcType(item.getValues(), itemConfig);
        }
    }

    private void prepareSql(DbConfig dbConfig, Connection connection) throws Exception {
        if ((error_flag_field = dbConfig.getDomino_error_flag_field()) == null || !DefaultConfig.PATTERN_NAME.matcher(error_flag_field).matches())
            error_flag_field = DefaultConfig.Domino_Error_Flag_Field;
        if ((succ_flag_field = dbConfig.getDomino_succ_flag_field()) == null || !DefaultConfig.PATTERN_NAME.matcher(succ_flag_field).matches())
            succ_flag_field = DefaultConfig.Domino_Succ_Flag_Field;
        if ((ftppath = dbConfig.getFtppath()) == null)
            ftppath = DefaultConfig.FTPPATH;

        if ((version = dbConfig.getVesion()) == null)
            version = DefaultConfig.VERSION;

        String name;
        afterPreparedMethod = (name = dbConfig.getDomino_after_prepared_driver()) == null || "".equals(name) ?
                null : (PreparedDocument) Class.forName(name).newInstance();
        beforePreparedMethod = (name = dbConfig.getDomino_before_prepared_driver()) == null || "".equals(name) ?
                null : (PreparedDocument) Class.forName(name).newInstance();

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
                throw new Exception("prepareSql:: itemConfig jdbc_type is null or non-standard ! " + (name == null ? "" : name));
            if (itemConfig.getScale_length() < 1) itemConfig.setScale_length(0);
            if ((name = itemConfig.getDomino_name()) != null && !DefaultConfig.PATTERN_NAME.matcher(name).matches())
                throw new Exception("prepareSql:: itemConfig domino_name is null or non-standard ! " + (name == null ? "" : name));
        }
        item_config_attachment = dbConfig.getSql_field_attachment();
        if (item_config_attachment != null && item_config_attachment.getSql_name() != null) {
            names.add(item_config_attachment.getSql_name());
            values.add("?");
        } else {
            item_config_attachment = null;
        }
        close(preparedStatement);
        preparedStatement = connection.prepareStatement(
                name = "INSERT INTO " + dbConfig.getSql_table() + " (" + String.join(", ", names) + ") "
                        + "VALUES (" + String.join(", ", values) + ")"
        );
        dbgMsg(name);
    }

    private JSONObject arcMssFiles(Document doc, DatabaseCollection databaseCollection) throws Exception {
        String srv = null, dbpath = null, key = null, query = "";
        Database mssdb = null;
        DocumentCollection mssdc = null;
        Document mssdoc = null, msstdoc = null;
        JSONObject res = new JSONObject();
        try {
            if ((srv = doc.getItemValueString("MSSSERVER")) == null || "".equals(srv))
                srv = doc.getParentDatabase().getServer();
            if (srv == null || (dbpath = doc.getItemValueString("MSSDATABASE")) == null)
                return res;
            query = "!@Contains(Form;\"DelForm\") & DOCUNID = \"" + doc.getUniversalID() + "\"";
            if (!((key = doc.getItemValueString("UniAppUnid")) == null || "".equals(key)))
                query += " & DOCUNID = \"" + key + "\"";

            mssdb = databaseCollection.getDatabase(srv, dbpath);
            if (mssdb == null || !mssdb.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);
            BaseUtils.recycle(mssdoc, mssdc);
            mssdc = mssdb.search(query, null, 0);
            mssdoc = mssdc.getFirstDocument();
            while (mssdoc != null) {
                arcMssDoc(mssdoc, res);
                mssdoc = mssdc.getNextDocument(msstdoc = mssdoc);
                recycle(msstdoc);
            }
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            recycle(mssdoc, mssdc);
        }
    }

    public void arcMssDoc(Document mssdoc, JSONObject resObject) throws Exception {
        String srv = null, dbpath = null, key = null, url = null, dir = null, form = null;
        int i;
        EmbeddedObject eo = null;
        JSONArray res = null;
        JSONObject obj = null;
        Matcher matcher = null;
        try {
            if (mssdoc == null || (form = mssdoc.getItemValueString("form")) == null) return;
            form = form.toLowerCase();
            url = "&Server=" + (srv = mssdoc.getParentDatabase().getServer()) + "&DbName=" + (dbpath = mssdoc.getParentDatabase().getFilePath()) + "&UNID=" + mssdoc.getUniversalID() + "&FileName=";
            if (ftppath != null)
                new File(dir = FileOperator.getAvailablePath(
                        ftppath,
                        srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                        dbpath.replaceAll("[/\\\\.]", "-"),
                        (key = mssdoc.getItemValueString("DOCUNID")) != null ? key : mssdoc.getUniversalID(), mssdoc.getItemValueString("form")
                ).toLowerCase()).mkdirs();

            Vector<String> all = session.evaluate("@AttachmentNames", mssdoc);
            if (all == null || all.size() < 1) return;
            Vector<String> files = mssdoc.hasItem("AttachFile") ? mssdoc.getItemValue("AttachFile") : new Vector(),
                    vAlias = mssdoc.hasItem("AttachTitle") ? mssdoc.getItemValue("AttachTitle") : new Vector();

            if ((res = resObject.getJSONArray(form)) == null) resObject.put(form, res = new JSONArray());
            for (String file : all) {
                i = files.indexOf(file);
                obj = new JSONObject();
                obj.put("unid", mssdoc.getUniversalID());
                obj.put("name", file);
                obj.put("url", DefaultConfig.DownloadPrefix + url + StringEscapeUtils.escapeJava(file));
                if (i < 0 || vAlias.size() < i || "".equals(dbpath = String.valueOf(vAlias.get(i)))) {
                    obj.put("alias", file);
                } else {
                    matcher = DefaultConfig.PATTERN_EXT.matcher(file);
                    obj.put("alias", dbpath + (matcher.find() ? matcher.group() : ""));
                }
                if (dir != null) {
                    recycle(eo);
                    if ((eo = mssdoc.getAttachment(file)) == null) continue;
                    eo.extractFile(dbpath = dir + "/" + file);
                    obj.put("local", dbpath);
                }
                res.add(obj);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            recycle(eo);
        }
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

    private String join(Iterable elements, CharSequence delimiter, SimpleDateFormat dateFormat) throws Exception {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner joiner = new StringJoiner(delimiter == null ? DefaultConfig.String_Join_Delimiter : delimiter);
        for (Object cs : elements) {
            if (cs == null) {
                joiner.add("");
            } else if (cs instanceof lotus.domino.DateTime) {
                joiner.add((dateFormat == null ? DefaultConfig.DateFormat : dateFormat).format(((lotus.domino.DateTime) cs).toJavaDate()));
            } else {
                joiner.add(cs.toString());
            }
        }
        return joiner.toString();
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
