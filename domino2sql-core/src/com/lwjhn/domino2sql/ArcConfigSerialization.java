package com.lwjhn.domino2sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino2sql.config.ArcConfig;
import com.lwjhn.util.FileOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @Author: lwjhn
 * @Date: 2020-11-22
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcConfigSerialization {
    public static boolean prettyFormat = true;

    public static ArcConfig parseArcConfig(InputStream is) throws IOException {
        return JSONObject.parseObject(is, ArcConfig.class);
    }

    public static ArcConfig parseArcConfig(File file) throws Exception {
        if (!(file != null && file.exists() && file.isFile()))
            throw new Exception("ConfigSerialization.parseArcConfig : parameter config file is null , or is not file .");
        InputStream is = null;
        try {
            return parseArcConfig(is = new FileInputStream(file));
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    public static ArcConfig parseArcConfig(String config) {
        return JSONObject.parseObject(config, ArcConfig.class);
    }

    public static String toJSONString(ArcConfig arcConfig) {
        return JSON.toJSONString(arcConfig, prettyFormat);
    }

    public static void toJSONFile(ArcConfig arcConfig, String path) throws Exception {
        toJSONFile(arcConfig, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(ArcConfig arcConfig, File path) throws Exception {
        toJSONFile(arcConfig, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(ArcConfig arcConfig, String path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(arcConfig).getBytes(charset));
    }

    public static void toJSONFile(ArcConfig arcConfig, File path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(arcConfig).getBytes(charset));
    }
}
