package com.lwjhn.domino2sql.config;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class DefaultConfig {
    public static final String VERSION = "1";
    public static final String FTPPATH = "/FTP_XC/";
    public static final String FTPPATH_REGEX = "^[a-zA-Z]:";
    public static final String Domino_Error_Flag_Field = "ArcXC_Error_FLAG";
    public static final String Domino_Succ_Flag_Field = "ArcXC_Succ_FLAG";
    public static final String String_Join_Delimiter ="; ";
    public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String Domino_UUID_Prefix = "ArcXC_UUID_";  //16 及 32
    public static final String DownloadPrefix = "/servlet/DownloadAttachCap?open";
    public static final Pattern PATTERN_EXT = Pattern.compile("\\.[0-9a-z]+$", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_NAME = Pattern.compile("^(_|[a-zA-Z])(_|[a-zA-Z0-9])*$");
    public static final Pattern PATTERN_TABLE = Pattern.compile("^(_|[a-zA-Z])(_|[a-zA-Z0-9])*((\\.(_|[a-zA-Z])(_|[a-zA-Z0-9])*)$|$)");
    public static final boolean DEBUGGER = true;
    public static final boolean ENABLE = true;
    public static final boolean PROCESS_ERROR_CONTINUNE = true;
    public static final boolean UPDATE_MODE_NO_INSERT = true;
}
