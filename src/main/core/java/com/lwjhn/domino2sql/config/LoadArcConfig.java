package com.lwjhn.domino2sql.config;

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
public class LoadArcConfig {
    static public ArcConfig load(InputStream is) throws Exception{
        return JSONObject.parseObject(is,ArcConfig.class);
    }

    static public ArcConfig load(File file){
        InputStream is=null;
        try{
            is = new FileInputStream(file);
            return JSONObject.parseObject(is,ArcConfig.class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally{
            if(is!=null) try{is.close();}catch(Exception e){}
        }
    }

    static public ArcConfig load(String json){
        return JSONObject.parseObject(json,ArcConfig.class);
    }
}
