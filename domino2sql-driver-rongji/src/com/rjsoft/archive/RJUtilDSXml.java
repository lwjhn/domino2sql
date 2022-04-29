package com.rjsoft.archive;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.util.AutoCloseableBase;
import com.lwjhn.util.UtilXML;
import com.rjsoft.ds.DSInitProcess;
import com.rjsoft.ds.DSProcess;
import com.rjsoft.ds.DSProcessDispatch;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RJUtilDSXml extends UtilXML {
    private static DSInitProcess dspp = new DSInitProcess();
    private static DSProcess dsp = new DSProcessDispatch();
    public static boolean debug = false;

    public static String parseDSXml(InputStream xml, Document doc, Session session) throws Exception {
        dspp.parse(xml);
        dsp.process(dspp.getDataSourcese(), doc, session);
        return dsp.getResultXml();
    }

    private static String getDataSourceTName(Document configDoc, String name) {
        if (configDoc == null) return "";
        try {
            Vector vectorTName = configDoc.getItemValue(name);
            if (vectorTName == null || vectorTName.isEmpty()) return "";
            return (String) vectorTName.lastElement();
        } catch (NotesException e) {
            return "";
        }
    }

    public static void parseDSHtml(Document doc, Session session, File outpath, Charset charset) throws Exception {
        OutputStream os = null;
        try {
            parseDSHtml(doc, session, os = new FileOutputStream(outpath), charset);
        } catch (Exception e) {
            throw e;
        } finally {
            AutoCloseableBase.close(os);
        }
    }

    public static void parseDSHtml(Document doc, Session session, OutputStream output, Charset charset) throws Exception {
        String DataSourceTName = "", FileStyleTName = "";
        List<String> query = new ArrayList<>();
        Document xmldoc = null, xsldoc = null, tdoc = null;
        EmbeddedObject oxml = null, oxsl = null;
        InputStream isxml = null, isxsl = null;
        try {
            if (!((DataSourceTName = doc.getItemValueString("DocWord")) == null || "".equals(DataSourceTName)))
                query.add("Form=\"WordConfig\" & DocWord=\"" + DataSourceTName.replaceAll("\"", "\\\"") + "\"");
            if (!((DataSourceTName = doc.getItemValueString("C_FlowCategory")) == null || "".equals(DataSourceTName)) && !((FileStyleTName = doc.getItemValueString("C_FlowName")) == null || "".equals(FileStyleTName)))
                query.add("Form=\"WordConfig\" & FlowName=\"" + DataSourceTName.replaceAll("\"", "\\\"") + "\\\\" + FileStyleTName.replaceAll("\"", "\\\"") + "\"");
            query.add("Form=\"WordConfig\" & (Name=\"DefaultConfig\" | FlowName=\"DefaultConfig\")");
            DataSourceTName = FileStyleTName = "";
            for (String formula : query) {
                if(debug) System.out.println("Formula: " + formula);
                if ((tdoc = doc.getParentDatabase().search(formula, null, 1).getFirstDocument()) == null) continue;
                if (xmldoc == null && !"".equals(DataSourceTName = getDataSourceTName(tdoc, "DataSourceTName"))) {
                    xmldoc = tdoc;
                    if(debug) System.out.println(">> xml: "+xmldoc+" file: "+DataSourceTName);
                }
                if (xsldoc == null && !"".equals(FileStyleTName = getDataSourceTName(tdoc, "FileStyleTName"))) {
                    xsldoc = tdoc;
                    if(debug) System.out.println(">> xsl: "+xsldoc+" file: "+FileStyleTName);
                }
                if (xmldoc != tdoc && xsldoc != tdoc){
                    BaseUtils.recycle(tdoc);
                    tdoc = null;
                }
                if(xmldoc!=null && xsldoc!=null) break;
            }
            if (xmldoc == null)
                throw new Exception("Can't find the xml Template in all profiles ! " + doc.getUniversalID());
            if (xsldoc == null)
                throw new Exception("Can't find the xsl Template in all profiles ! " + doc.getUniversalID());
            if(debug) System.out.print(">>" + doc.getUniversalID() + " !! xml:" +xmldoc.getUniversalID()+" !! DataSourceTName : ");
            if(debug) System.out.println(DataSourceTName);
            if(debug) System.out.print(">>" + doc.getUniversalID() + " !! xsl:" +xsldoc.getUniversalID()+" !! FileStyleTName : ");
            if(debug) System.out.println(FileStyleTName);
            if ((oxml = xmldoc.getAttachment(DataSourceTName)) == null)
                throw new Exception("Can't find xml Template File:" + DataSourceTName + "in Configration Document . " + xmldoc.getUniversalID());
            if ((oxsl = xsldoc.getAttachment(FileStyleTName)) == null)
                throw new Exception("Can't find xsl Template File:" + DataSourceTName + "in Configration Document . " + xsldoc.getUniversalID());
            transformer(isxsl = oxsl.getInputStream(), parseDSXml(isxml = oxml.getInputStream(), doc, session), output, charset);
        } finally {
            AutoCloseableBase.close(isxml,isxsl);
            BaseUtils.recycle(oxsl, oxml, xmldoc, xsldoc, tdoc);
        }
    }
}
