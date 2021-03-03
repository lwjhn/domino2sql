package com.rjsoft.prepared;

import java.io.File;
import java.sql.Connection;

import com.lwjhn.util.FileOperator;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.rjsoft.archive.RJUitilDSXml;
import lotus.domino.*;
import java.nio.charset.Charset;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */

public class BeforePreparedRJDoc extends BeforeArchive {
    private final String form = "processing", filename = "阅办单.html" ;
    public void action(Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws Exception {
        super.action(srcdoc, dbConfig, connection, databaseCollection, mssdbc);

        String srv = null, dbpath = null, unid = srcdoc.getUniversalID();
        Database attachdb = null;
        File file;
        DocumentCollection mssdc = null;
        Document mssdoc = null;
        RichTextItem item = null;
        try {
            attachdb = mssdbc.getDatabase(srv=srcdoc.getItemValueString("MSSSERVER"), dbpath=srcdoc.getItemValueString("MSSDATABASE"));
            if(attachdb == null || !attachdb.isOpen()) throw new Exception("mssdatabase is nothing , or can't open . master docid " + unid);
            mssdc = attachdb.search("Form=\"" + form + "\" & DOCUNID = \"" + unid + "\"", null, 1);
            if (mssdc.getCount() == 1) {
                mssdoc = mssdc.getFirstDocument();
                if (!this.getVersion().equals(mssdoc.getItemValueString("$before_archive_version"))) {
                    mssdoc.remove(true);
                    if(mssdoc!=null) try{mssdoc.recycle();}catch(Exception e){}
                    if(mssdc!=null) try{mssdc.recycle();}catch(Exception e){}
                    mssdoc = null; mssdc = null;
                }
            }
            if (mssdoc != null) return;
            (file = new File(FileOperator.getAvailablePath(new String[]{
                    this.getFtppath(),
                    srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                    dbpath.replaceAll("[/\\\\.]", "-"),
                    unid, form
            }).toLowerCase())).mkdirs();

            RJUitilDSXml.parseDSHtml(srcdoc, databaseCollection.getSession(), file = new File(file.getCanonicalPath() + "/" + form + unid + ".html"), Charset.forName("UTF-8"));
            if (!file.exists()) throw new Exception("RJUitilDSXml.parseDSHtml error : create file error ! " + file.getName());

            mssdoc = attachdb.createDocument();
            mssdoc.replaceItemValue("form", form);
            mssdoc.replaceItemValue("AttachFile", file.getName());
            mssdoc.replaceItemValue("AttachTitle", FileOperator.getFileAliasByRegex(filename));
            mssdoc.replaceItemValue("DOCUNID", unid);
            mssdoc.replaceItemValue("UNID", mssdoc.getUniversalID());
            (item = mssdoc.createRichTextItem("tempbody"))
                    .embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getCanonicalPath(), file.getName());
            mssdoc.replaceItemValue("$before_archive_version", this.getVersion());
            mssdoc.save(true);
            FileOperator.deleteDir(file);
        } catch (Exception e) {
            throw e;
        } finally {
            if(item!=null) try{item.recycle();}catch(Exception e){}
            if(mssdoc!=null) try{mssdoc.recycle();}catch(Exception e){}
            if(mssdc!=null) try{mssdc.recycle();}catch(Exception e){}
        }
    }
}
