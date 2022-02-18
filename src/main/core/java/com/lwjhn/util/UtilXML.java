package com.lwjhn.util;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Author: lwjhn
 * @Date: 2020-11-16
 * @Description: com.lwjhn
 * @Version: 1.0
 */
public class UtilXML {
    private static final TransformerFactory factory = TransformerFactory.newInstance();
    public final static Charset DefaultEncoding = StandardCharsets.UTF_8;

    public static void transformer(InputStream xslSource, InputStream xmlSource, OutputStream htmlResult) throws Exception {
        Transformer transformer = factory.newTransformer(new StreamSource(xslSource));
        Properties properties = transformer.getOutputProperties();
        properties.setProperty(OutputKeys.ENCODING, DefaultEncoding.displayName());
        transformer.transform(new StreamSource(xmlSource), new StreamResult(htmlResult));
    }

    public static void transformer(InputStream xslSource, InputStream xmlSource, OutputStream htmlResult, Charset charset) throws Exception {
        Transformer transformer = factory.newTransformer(new StreamSource(xslSource));
        Properties properties = transformer.getOutputProperties();
        properties.setProperty(OutputKeys.ENCODING, charset.displayName());
        transformer.transform(new StreamSource(xmlSource), new StreamResult(htmlResult));
    }

    public static void transformer(InputStream xslSource, String xmlSource, OutputStream htmlResult) throws Exception {
        transformer(xslSource, xmlSource, htmlResult, StandardCharsets.UTF_8);
    }

    public static void transformer(InputStream xslSource, String xmlSource, OutputStream htmlResult, Charset charset) throws Exception {
        ByteArrayInputStream binput = null;
        try {
            transformer(xslSource, binput = new ByteArrayInputStream(xmlSource.getBytes(charset)), htmlResult);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (binput != null) binput.reset();
            } catch (Exception ignored) {
            }
        }
    }

    public static void transformer(InputStream xslSource, String xmlSource, File output, Charset charset) throws Exception {
        FileOutputStream htmlResult = null;
        try {
            transformer(xslSource, xmlSource, htmlResult = new FileOutputStream(output), charset);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (htmlResult != null) htmlResult.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void transformer(File xsl, File xml, File html) throws Exception {
        if (!(xml.exists() && xml.isFile()))
            throw new Exception("xml file can not find ! " + xml.getCanonicalPath());
        if (!(xsl.exists() && xsl.isFile()))
            throw new Exception("xsl file can not find ! " + xsl.getCanonicalPath());

        FileOutputStream htmlResult = null;
        InputStream xslSource = null, xmlSource = null;
        try {
            htmlResult = new FileOutputStream(html);
            xslSource = new FileInputStream(xsl);
            xmlSource = new FileInputStream(xml);
            transformer(xslSource, xmlSource, htmlResult);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (htmlResult != null) htmlResult.close();
            } catch (Exception ignored) {
            }
            try {
                if (xslSource != null) xslSource.close();
            } catch (Exception ignored) {
            }
            try {
                if (xmlSource != null) xmlSource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
