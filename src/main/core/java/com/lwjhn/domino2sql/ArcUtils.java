package com.lwjhn.domino2sql;

import java.util.UUID;

/**
 * @Author: lwjhn
 * @Date: 2020-11-23
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcUtils {
    public static String getUUID32() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String getUUID8() {
        return String.format("%08X",UUID.randomUUID().hashCode());
    }

    public static String getUUID16() {
        return getUUID8() + getUUID8();
    }
}
