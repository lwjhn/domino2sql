package com.lwjhn.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @Author: lwjhn
 * @Date: 2020-12-8
 * @Description: com.lwjhn.cangnan
 * @Version: 1.0
 */
public class XmlBase {
    public static Charset DefualtCharset = Charset.forName("UTF-8");
    private static SAXReader saxReader = new SAXReader();

    public static void write(org.dom4j.Document document, File output) throws Exception {
        write(document, output, DefualtCharset);
    }

    public static void write(org.dom4j.Document document, File output, Charset charset) throws Exception {
        FileOutputStream fo = null;
        Writer writer = null;
        XMLWriter xmlwriter = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(charset.toString());
            fo = new FileOutputStream(output);
            writer = new OutputStreamWriter(fo, charset);
            xmlwriter = new XMLWriter(writer, format);
            xmlwriter.write(document);
        } catch (Exception e) {
            throw e;
        } finally {
            closeQuiet(xmlwriter);
            AutoCloseableBase.close(writer, fo);
        }
    }

    public static String write(Charset charset, org.dom4j.Document document) throws Exception {
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();    //swapStream.reset();
            write(document, swapStream, charset);
            return new String(swapStream.toByteArray(), charset);
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }

    public static InputStream write(org.dom4j.Document document, Charset charset) throws Exception {
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();    //swapStream.reset();
            write(document, swapStream, charset);
            return new ByteArrayInputStream(swapStream.toByteArray());
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }

    public static void write(org.dom4j.Document document, OutputStream output, Charset charset) throws Exception {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(output, charset);
            write(document, outputStreamWriter, charset);
        } catch (Exception e) {
            throw e;
        } finally {
            //AutoCloseableBase.close(outputStreamWriter);
        }
    }

    public static void write(org.dom4j.Document document, Writer output) throws Exception {
        write(document, output, DefualtCharset);
    }

    public static void write(org.dom4j.Document document, Writer output, Charset charset) throws Exception {
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(charset.toString());
            writer = new XMLWriter(output, format);
            writer.write(document);
            writer.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            //closeQuiet(writer);
        }
    }

    private static void closeQuiet(XMLWriter... args) {
        if (args == null) return;
        for (XMLWriter arg : args) {
            try {
                if (arg != null) arg.close();
            } catch (Exception e) {
            }
        }
    }

    public static Document read(File file) throws Exception {
        return read(file, DefualtCharset);
    }

    public static Document read(URL url) throws Exception {
        return read(url, DefualtCharset);
    }

    public static Document read(String systemId) throws Exception {
        return read(systemId, DefualtCharset);
    }

    public static Document read(InputStream in) throws Exception {
        return read(in, DefualtCharset);
    }

    public Document read(Reader reader) throws DocumentException {
        return read(reader, DefualtCharset);
    }

    public static Document read(File file, Charset encoding) throws Exception {
        saxReader.setEncoding(encoding.displayName());
        return saxReader.read(file);
    }

    public static Document read(URL url, Charset encoding) throws Exception {
        saxReader.setEncoding(encoding.displayName());
        return saxReader.read(url);
    }

    public static Document read(String systemId, Charset encoding) throws Exception {
        saxReader.setEncoding(encoding.displayName());
        return saxReader.read(systemId);
    }

    public static Document read(InputStream in, Charset encoding) throws Exception {
        saxReader.setEncoding(encoding.displayName());
        return saxReader.read(in);
    }

    public Document read(Reader reader, Charset encoding) throws DocumentException {
        saxReader.setEncoding(encoding.displayName());
        return saxReader.read(reader);
    }
}
