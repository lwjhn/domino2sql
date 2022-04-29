package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSONArray;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import com.lwjhn.util.FileOperator;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
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
public class OnActionExtractFile extends Message implements OnActionDriver {
    protected DatabaseCollection databaseCollection;
    protected ExtendedOptions extended_options = null;
    protected String ftpPath = null;
    protected int index = -1;

    public static class ExtendedOptions {
        protected ItemConfig sql_field_attachment = null;
        protected String ftppath_formula = DefaultDriverConfig.ftppath_formula;
    }

    @Override
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception {
        if (extended_options == null || extended_options.sql_field_attachment == null) return;
        preparedStatement.setObject(index, extractFile(doc, databaseCollection.getSession()).toJSONString(), extended_options.sql_field_attachment.getJdbc_type().getVendorTypeNumber(), extended_options.sql_field_attachment.getScale_length());
    }

    @SuppressWarnings("all")
    public JSONArray extractFile(Document doc, Session session) throws Exception {
        String key, dir;
        EmbeddedObject eo = null;
        JSONArray response = new JSONArray();
        try {
            if (doc == null) return response;
            new File(dir = FileOperator.getAvailablePath(
                    ftpPath,
                    (String) databaseCollection.getSession().evaluate(extended_options.ftppath_formula, doc).get(0)
            ).toLowerCase()).mkdirs();

            Vector<String> all = session.evaluate("@AttachmentNames", doc);
            if (all == null || all.size() < 1) return response;
            for (String file : all) {
                if ((eo = doc.getAttachment(file)) == null) continue;
                eo.extractFile(key = dir + "/" + file);
                DefaultDriverConfig.putFile(response, file, file, key);
                BaseUtils.recycle(eo);
            }
            return response;
        } finally {
            BaseUtils.recycle(eo);
        }
    }

    @Override
    public void recycle() {

    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        if ((ftpPath = dbConfig.getFtppath()) == null)
            ftpPath = DefaultConfig.FTPPATH;

        extended_options = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), ExtendedOptions.class);
        index = dbConfig.getSql_field_others().length + 1;
    }
}
