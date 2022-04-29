package com.rjsoft.driver;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import lotus.domino.*;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@SuppressWarnings("all")
public class ExtendedOptions implements Cloneable{
    protected String attachment_server = "MSSSERVER";
    protected String attachment_dbpath = "MSSDATABASE";
    /**
     * OA: "!@Contains(Form;\"DelForm\") & (DOCUNID = \"" + @Text(@DocumentUniqueID) + "\"" + @If(UniAppUnid="";"";" | DOCUNID = \"" + UniAppUnid + "\"") + @If(MSSUNID="";"";" | @Text(@DocumentUniqueID) = \"" + MSSUNID + "\"") + ")"
     * EX: "(Form=\"MSS\" | Form = \"Attachment\") & (INITUNID = \"" + @If(INITUNID=""; @Text(@DocumentUniqueID); INITUNID) + "\"" + @If(MSSUNID="";"";" | @Text(@DocumentUniqueID) = \"" + MSSUNID + "\"") + ")"
     */
    protected String attachment_query = "\"!@Contains(Form;\\\"DelForm\\\") & (DOCUNID = \\\"\" + @Text(@DocumentUniqueID) + \"\\\"\" + @If(UniAppUnid=\"\";\"\";\" | DOCUNID = \\\"\" + UniAppUnid + \"\\\"\") + @If(MSSUNID=\"\";\"\";\" | @Text(@DocumentUniqueID) = \\\"\" + MSSUNID + \"\\\"\") + \")\"";
    protected boolean debug = false;

    protected boolean export_opinion = false;
    protected boolean export_flow = false;
    protected boolean export_processing = false;

    public void action(Document doc, DatabaseCollection databaseCollection, Consumer<Document> caller) throws Exception {
        this.apply(doc, databaseCollection, dc -> {
            Document mssDoc = null, tdoc = null;
            try {
                mssDoc = dc.getFirstDocument();
                while (mssDoc != null) {
                    caller.accept(mssDoc);
                    mssDoc = dc.getNextDocument(tdoc = mssDoc);
                    BaseUtils.recycle(tdoc);
                }
            } catch (NotesException e) {
                throw new RuntimeException(e);
            } finally {
                BaseUtils.recycle(tdoc, mssDoc, dc);
            }
        });
    }

    public void apply(Document doc, DatabaseCollection databaseCollection, Consumer<DocumentCollection> caller) throws Exception {
        String srv, dbpath, query;
        DocumentCollection dc = null;
        try {
            Session session = databaseCollection.getSession();
            srv = (String) session.evaluate(attachment_server, doc).get(0);
            dbpath = (String) session.evaluate(attachment_dbpath, doc).get(0);
            query = (String) session.evaluate(attachment_query, doc).get(0);

            if (StringUtils.isBlank(srv))
                srv = doc.getParentDatabase().getServer();
            if (StringUtils.isBlank(dbpath) || StringUtils.isBlank(query))
                return;
            if (debug) {
                System.out.println("ExtendedOptions.action: server is " + srv + ", dbpath is " + dbpath + ", formula is " + query);
            }
            Database db = databaseCollection.getDatabase(srv, dbpath);
            if (db == null || !db.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);
            caller.accept(dc = db.search(query, null, 0));
        } finally {
            BaseUtils.recycle(dc);
        }
    }

    public String getAttachment_server() {
        return attachment_server;
    }

    public void setAttachment_server(String attachment_server) {
        this.attachment_server = attachment_server;
    }

    public String getAttachment_dbpath() {
        return attachment_dbpath;
    }

    public void setAttachment_dbpath(String attachment_dbpath) {
        this.attachment_dbpath = attachment_dbpath;
    }

    public String getAttachment_query() {
        return attachment_query;
    }

    public void setAttachment_query(String attachment_query) {
        this.attachment_query = attachment_query;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isExport_opinion() {
        return export_opinion;
    }

    public void setExport_opinion(boolean export_opinion) {
        this.export_opinion = export_opinion;
    }

    public boolean isExport_flow() {
        return export_flow;
    }

    public void setExport_flow(boolean export_flow) {
        this.export_flow = export_flow;
    }

    public boolean isExport_processing() {
        return export_processing;
    }

    public void setExport_processing(boolean export_processing) {
        this.export_processing = export_processing;
    }

    @Override
    public ExtendedOptions clone() {
        try {
            return (ExtendedOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
