package com.rjsoft.prepared;

import com.alibaba.fastjson.JSONObject;
import com.lwjhn.FileOperator;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2json.Document2Json;
import com.lwjhn.domino2sql.PreparedDocument;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import lotus.domino.*;

import java.io.File;
import java.sql.Connection;

/**
 * @Author: lwjhn
 * @Date: 2020-11-25
 * @Description: com.lwjhn.archive.prepared
 * @Version: 1.0
 */
public class BeforeArchive implements PreparedDocument {
    private String ftppath = null, version = null;

    @Override
    public void action(Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws Exception {
        String srv = null, dbpath = null, unid = null;
        Database mssdb = null;
        try {
            if ((ftppath = dbConfig.getFtppath()) == null)
                ftppath = DefaultConfig.FTPPATH;
            if ((version = dbConfig.getVesion()) == null)
                version = DefaultConfig.VERSION;

            if ((srv = srcdoc.getItemValueString("MSSSERVER")) == null || "".equals(srv))
                srv = srcdoc.getParentDatabase().getServer();
            if (srv == null || (dbpath = srcdoc.getItemValueString("MSSDATABASE")) == null)
                throw new Exception("can not find item of mssdatabase from document . " + srcdoc.getUniversalID());
            mssdb = mssdbc.getDatabase(srv, dbpath);
            if (mssdb == null || !mssdb.isOpen())
                throw new Exception("can't open attachment database ! " + srv + " !! " + dbpath);

            doc2json(mssdbc, mssdb, srv,
                    srcdoc.getItemValueString("MssOpinion"),
                    unid = srcdoc.getUniversalID(),
                    "opinion", "Form=\"Opinion\" & PARENTUNID = \"" + unid + "\"",
                    "意见表.json")
                    .doc2json(mssdbc, mssdb, srv,
                            srcdoc.getItemValueString("MssFlow"),
                            unid,
                            "flow", "Form=\"FlowForm\" & DOCUNID = \"" + unid + "\"",
                            "流程记录.json");

        } catch (Exception e) {
            throw e;
        } finally {

        }
    }

    private BeforeArchive doc2json(DatabaseCollection mssdbc, Database attachdb, String srv, String dbpath, String unid, String form, String srcQuery, String filename) throws Exception {
        String query = "";
        File file;
        Database mssdb = null;
        DocumentCollection mssdc = null;
        Document mssdoc = null;
        RichTextItem item = null;
        JSONObject res = new JSONObject();
        try {
            if (srv == null || dbpath == null || unid == null)
                return this;
            mssdb = mssdbc.getDatabase(srv, dbpath);
            if (mssdb == null || !mssdb.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);

            query = "Form=\"" + form + "\" & DOCUNID = \"" + unid + "\"";
            mssdc = attachdb.search(query, null, 1);
            if (mssdc.getCount() == 1) {
                mssdoc = mssdc.getFirstDocument();
                if (!version.equals(mssdoc.getItemValueString("$before_archive_version"))) {
                    mssdoc.remove(true);
                    BaseUtils.recycle(mssdoc, mssdc);
                    mssdoc = null;
                    mssdc = null;
                }
            }
            if (mssdoc != null) return this;
            (file = new File(FileOperator.getAvailablePath(
                    ftppath,
                    srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                    attachdb.getFilePath().replaceAll("[/\\\\.]", "-"),
                    unid, form
            ).toLowerCase())).mkdirs();
            BaseUtils.recycle(mssdoc, mssdc);
            mssdc = mssdb.search(srcQuery, null, 0);

            Document2Json.toJSONFile(mssdc, file = new File(file.getCanonicalPath() + "/" + form + unid + ".json"));
            if (!file.exists()) throw new Exception("Document2Json error : create file error ! " + file.getName());
            BaseUtils.recycle(mssdoc, mssdc);
            mssdoc = attachdb.createDocument();
            mssdoc.replaceItemValue("form", form);
            mssdoc.replaceItemValue("AttachFile", file.getName());
            mssdoc.replaceItemValue("AttachTitle", filename);
            mssdoc.replaceItemValue("DOCUNID", unid);
            mssdoc.replaceItemValue("UNID", mssdoc.getUniversalID());
            (item = mssdoc.createRichTextItem("tempbody"))
                    .embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getCanonicalPath(), file.getName());
            mssdoc.replaceItemValue("$before_archive_version", version);
            mssdoc.save(true);
            FileOperator.deleteDir(file);
            return this;
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(item, mssdoc, mssdc);
        }
    }

    public String getFtppath() {
        return ftppath;
    }

    public void setFtppath(String ftppath) {
        this.ftppath = ftppath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void recycle() throws Exception{

    }
}
