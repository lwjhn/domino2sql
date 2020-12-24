package com.lwjhn.domino2sql;

import java.util.Random;
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

    public static String getUUID16() {
        int first = new Random(10).nextInt(8) + 1,
                hashCodeV = UUID.randomUUID().toString().hashCode();
        return first + String.format("%015d", hashCodeV < 0 ? -hashCodeV : hashCodeV);
    }
}
