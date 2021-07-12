package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2sql.ProcessStatement;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import com.lwjhn.util.FileOperator;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.Session;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

/**
 * @Author: lwjhn
 * @Date: 2021-1-22
 * @Description: com.lwjhn.test
 * @Version: 1.0
 */
public class ProcessExtractFile extends Message implements ProcessStatement {
    private DbConfig dbConfig = null;
    private JSONObject extended_options = null;
    private ItemConfig item_config_attachment = null;
    private String ftppath = null;
    private int index = -1;

    private final String throws_error_field = "throws_error";
    private final String sql_field_attachment = "sql_field_attachment";
    private boolean throws_error = true;

    @Override
    public void action(PreparedStatement preparedStatement, Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws Exception {
        initConfig(dbConfig);
        if (item_config_attachment == null) return;
        preparedStatement.setObject(index, arcMssDoc(srcdoc, databaseCollection.getSession()).toJSONString(), item_config_attachment.getJdbc_type().getVendorTypeNumber(), item_config_attachment.getScale_length());
    }

    private void throwsError(String message) throws Exception {
        this.dbgMsg(message);
        if (dbConfig != null) dbConfig.setAction_error_log(message);
        if (throws_error) throw new Exception(message);
    }

    private void initConfig(DbConfig dbConfig) throws Exception {
        if (dbConfig == this.dbConfig) return;
        index = dbConfig.getSql_field_others().length + 1;
        if ((ftppath = dbConfig.getFtppath()) == null)
            ftppath = DefaultConfig.FTPPATH;

        if ((extended_options = dbConfig.getExtended_options()) == null) return;
        item_config_attachment = extended_options.getObject(sql_field_attachment, ItemConfig.class);
        this.setDebug(throws_error = extended_options.containsKey(throws_error_field) ? extended_options.getBoolean(throws_error_field) : true);
    }

    public JSONArray arcMssDoc(Document mssdoc, Session session) throws Exception {
        String key = null, dir = null;
        EmbeddedObject eo = null;
        JSONArray res = new JSONArray();
        JSONObject obj = null;
        try {
            if (mssdoc == null) return res;
            if (ftppath != null)
                new File(dir = FileOperator.getAvailablePath(
                        ftppath,
                        mssdoc.getParentDatabase().getServer().replaceAll("(/[^/]*)|([^/]*=)", ""),
                        mssdoc.getParentDatabase().getFilePath().replaceAll("[/\\\\.]", "-"),
                        (key = mssdoc.getItemValueString("DOCUNID")) != null ? key : mssdoc.getUniversalID(), mssdoc.getItemValueString("form")
                ).toLowerCase()).mkdirs();

            Vector<String> all = session.evaluate("@AttachmentNames", mssdoc);
            if (all == null || all.size() < 1) return res;
            for (String file : all) {
                obj = new JSONObject();
                obj.put("name", file);
                if (dir != null) {
                    BaseUtils.recycle(eo);
                    if ((eo = mssdoc.getAttachment(file)) == null) continue;
                    eo.extractFile(key = dir + "/" + file);
                    obj.put("local", key);
                }
                res.add(obj);
            }
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(eo);
        }
    }

    @Override
    public void recycle() throws Exception {

    }
}
