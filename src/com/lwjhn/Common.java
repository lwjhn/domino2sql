package com.lwjhn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * @Author: lwjhn
 * @Date: 2020-11-5
 * @Description: com.lwjhn
 * @Version: 1.0
 */
public class Common {
    public static void printXML(java.io.PrintWriter pw,String strXML){
        try{
            pw.println("content-type:text/xml; charset=UTF-8");
            pw.println("<?xml version='1.0' encoding='UTF-8'?>");
            pw.println("<center><![CDATA[" + strXML + "]]></center>");
        }catch(Exception e){}
    }

    public static String getXMLParaValue(String para_name,String query_string){
        String query=query_string+"&";
        para_name="&"+para_name+"=";
        int start=query.indexOf(para_name);

        if(start<0 || (start+para_name.length())>query.length()){
            return "";
        }
        start=start+para_name.length();

        String para_value=query.substring(start, query.indexOf("&",start));
        return para_value;
    }

    public static String getTimeStamp(){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        return formatter.format(new java.util.Date());
    }

    public static String getDate(java.util.Date date,String format){
        if("".equals(format)){
            format="yyyy-MM-dd";
        }
        SimpleDateFormat formatter=new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getDate(java.util.Date date){
        return getDate(date,"yyyy-MM-dd");
    }

    public static String substring(String src, int len,String charset){
        if(charset.equals("")){
            charset="GBK";
        }
        byte bt[] = null;
        try{
            bt=src.getBytes(charset);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

        if (len > bt.length) {
            len = bt.length;
        }

        return new String(bt, 0, len);
    }
    public static String substring(String src, int len){
        return substring(src, len,"GBK");
    }

    public static String substr(String src, int len,String charset){
        if(charset.equals("")){
            charset="GBK";
        }
        StringBuffer buff = new StringBuffer();
        int s_len=src.length();
        for (int i = 0; i < s_len; i++) {
            try{
                len-=String.valueOf(src.charAt(i)).getBytes(charset).length;
                if(len<0) break;
                buff.append(src.charAt(i));
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return buff.toString();
    }

    public static String substr(String src, int len){
        return substr(src, len,"GBK");
    }

    public static String joinstr(String[] src,String deli){
        if(src.length<1){
            return "";
        }
        StringBuffer buff = new StringBuffer();
        buff.append(src[0]);
        for (int i = 1; i < src.length; i++) {
            buff.append(deli);
            buff.append(src[i]);
        }
        return buff.toString();
    }

    public static InputStream byte2InputStream(byte[] buf){
        return new ByteArrayInputStream(buf);
    }

    public static byte[] inputStream2Byte(InputStream input) throws IOException{
        ByteArrayOutputStream swapStream=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int rc=0;
        while((rc=input.read(buff, 0, 1024))>0){
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static String iso2GB(String src) {
        String strRet = null;
        try {
            strRet = new String(src.getBytes("ISO_8859_1"), "GB2312");
        } catch (Exception e) {

        }
        return strRet;
    }

    public static String iso2UTF(String src) {
        String strRet = null;
        try {
            strRet = new String(src.getBytes("ISO_8859_1"), "UTF-8");
        } catch (Exception e) {

        }
        return strRet;
    }
}
