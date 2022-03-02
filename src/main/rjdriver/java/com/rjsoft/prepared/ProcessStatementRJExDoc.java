package com.rjsoft.prepared;

import lotus.domino.Document;
import lotus.domino.NotesException;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class ProcessStatementRJExDoc extends ProcessStatement {
    @Override
    protected String getQuery(Document doc) throws NotesException {
        String key = doc.getItemValueString("INITUNID"),
                query = "(Form=\"MSS\" | Form = \"Attachment\") & (INITUNID = \""
                        + (key==null || "".equals(key) ? doc.getUniversalID() : key) + "\"";
        if (!((key = doc.getItemValueString("UniAppUnid")) == null || "".equals(key)))
            query += " | INITUNID = \"" + key + "\"";
        if (!((key = doc.getItemValueString("MSSUNID")) == null || "".equals(key)))
            query += " | @Text(@DocumentUniqueID) = \"" + key + "\"";
        return query + ")";
    }
}
