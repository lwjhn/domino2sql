package com.rjsoft.driver;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2json.Document2Json;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.driver.OnActionDriver;
import com.lwjhn.domino2sql.driver.OnActionExtensionDocuments;
import com.lwjhn.util.AutoCloseableBase;
import com.lwjhn.util.Common;
import com.rjsoft.archive.ExportOldFlow;
import com.rjsoft.archive.RJUtilDSXml;
import lotus.domino.*;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.util.regex.Matcher;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.driver
 * @Version: 1.0
 */
public abstract class AbstractOnActionDriver extends Message implements OnActionDriver {
    protected Session session = null;
    protected DatabaseCollection databaseCollection;
    protected DbConfig dbConfig = null;
    private OnActionDriver actionExtensionDocuments = null;

    protected abstract ExtendedOptions getExtended_options();

    public abstract void upload(InputStream input, String name, String alias, String type, Document doc);

    @Override
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception {
        actionExtensionDocuments.action(preparedStatement, doc);
    }

    public void upload(String input, String name, String alias, String type, Document doc) {
        upload(input.getBytes(StandardCharsets.UTF_8), name, alias, type, doc);
    }

    public void upload(byte[] input, String name, String alias, String type, Document doc){
        upload(Common.byte2InputStream(input), name, alias, type, doc);
    }

    protected void handle(Document doc) throws Exception {
        String mssOpinion, srv, unid = doc.getUniversalID();
        if (StringUtils.isBlank(srv = doc.getItemValueString("MSSSERVER")))
            srv = doc.getParentDatabase().getServer();
        if (getExtended_options().export_opinion) {
            dbConfig.printTimestamp(unid + " :: 意见表 : begin - ");

            mssOpinion = doc.getItemValueString("MSSOpinion");
            if (StringUtils.isBlank(mssOpinion)) {
                mssOpinion = doc.getItemValueString("OpinionlogDatabase");
            }
            if (StringUtils.isBlank(mssOpinion)) {
                mssOpinion = doc.getParentDatabase().getFilePath();
            }
            doc2json(srv, mssOpinion,
                    "opinion", "Form=\"Opinion\" & PARENTUNID = \"" + unid + "\"",
                    "意见表.json", doc);

            dbConfig.printTimestamp(unid + " :: 意见表 : end - ");
        }

        if (this.getExtended_options().export_flow) {
            dbConfig.printTimestamp(unid + " :: 流程记录 : begin - ");

            mssOpinion = doc.getItemValueString("MssFlow");
            if (StringUtils.isBlank(mssOpinion)) {
                oldFlow(doc);
            } else {
                doc2json(srv, mssOpinion,
                        "flow", "Form=\"FlowForm\" & DOCUNID = \"" + unid + "\"",
                        "流程记录.json", doc);
            }

            dbConfig.printTimestamp(unid + " :: 流程记录 : end - ");
        }

        if (this.getExtended_options().export_processing) {
            dbConfig.printTimestamp(unid + " :: 阅办单 : begin - ");

            final String form = "processing", filename = "阅办单.html";
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            RJUtilDSXml.debug = getExtended_options().debug;
            RJUtilDSXml.parseDSHtml(doc, databaseCollection.getSession(), swapStream, StandardCharsets.UTF_8);

            dbConfig.printTimestamp(unid + " :: 阅办单 : parseDSHtml complete - ");

            upload(swapStream.toByteArray(), filename, filename, form, doc);

            dbConfig.printTimestamp(unid + " :: 阅办单 : end - ");
        }

        extractFiles(doc);
    }

    protected void oldFlow(Document doc) throws Exception {
        upload(ExportOldFlow.generate(doc), "流程记录.html", "流程记录.html", "flow", doc);
    }

    protected void doc2json(String srv, String dbpath, String form, String srcQuery, String filename, Document doc) throws Exception {
        DocumentCollection dc = null;
        try {
            if (StringUtils.isBlank(srv) || StringUtils.isBlank(dbpath))
                return;
            Database db = databaseCollection.getDatabase(srv, dbpath);
            if (db == null || !db.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);

            upload(Document2Json.toJSON(dc = db.search(srcQuery, null, 0)).toJSONString(), filename, filename, form, doc);
        } finally {
            BaseUtils.recycle(dc);
        }
    }

    @SuppressWarnings("all")
    protected void extractFiles(Document doc) throws Exception {
        getExtended_options().action(doc, databaseCollection, mssDoc -> {
            String key, dir, form;
            int i;
            EmbeddedObject eo = null;
            InputStream inputStream = null;
            Matcher matcher;
            try {
                if (mssDoc == null || (form = mssDoc.getItemValueString("form")) == null) return;
                Vector<String> all = session.evaluate("@AttachmentNames", mssDoc);
                if (all == null || all.size() < 1) return;

                Vector<String> files = mssDoc.hasItem("AttachFile") ? mssDoc.getItemValue("AttachFile") : new Vector(),
                        vAlias = mssDoc.hasItem("AttachTitle") ? mssDoc.getItemValue("AttachTitle") : new Vector();
                String alias;
                for (String file : all) {
                    i = files.indexOf(file);
                    if (i < 0 || vAlias.size() <= i || "".equals(key = String.valueOf(vAlias.get(i)))) {
                        alias = file;
                    } else {
                        matcher = DefaultConfig.PATTERN_EXT.matcher(file);
                        alias = key + (matcher.find() ? matcher.group() : "");
                    }
                    BaseUtils.recycle(eo);
                    if ((eo = mssDoc.getAttachment(file)) == null) continue;
                    AutoCloseableBase.close(inputStream);

                    upload(inputStream = eo.getInputStream(), file, alias, form, doc);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                AutoCloseableBase.close(inputStream);
                BaseUtils.recycle(eo);
            }
        });
    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception {
        this.databaseCollection = databaseCollection;
        session = databaseCollection.getSession();

        BaseUtils.recycle(actionExtensionDocuments);
        actionExtensionDocuments = new OnActionExtensionDocuments();
        actionExtensionDocuments.init(dbConfig, connection, databaseCollection);
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        actionExtensionDocuments.initDbConfig(this.dbConfig = dbConfig);
        this.setDebug(dbConfig.isDebugger());
    }

    @Override
    public void recycle() {
        BaseUtils.recycle(actionExtensionDocuments);
        actionExtensionDocuments = null;
    }
}
