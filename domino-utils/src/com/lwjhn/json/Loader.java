package com.lwjhn.json;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author: lwjhn
 * @Date: 2020-11-19
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class Loader {
    public static <T> T load(InputStream is, Class<T> clazz) throws Exception {
        return JSONObject.parseObject(is, clazz);
    }

    public static <T> T load(File file, Class<T> clazz) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return JSONObject.parseObject(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static <T> T load(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }
}
