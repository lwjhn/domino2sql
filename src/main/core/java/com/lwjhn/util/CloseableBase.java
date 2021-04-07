package com.lwjhn.util;

import java.io.Closeable;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino
 * @Version: 1.0
 */
public class CloseableBase {
    public static void close(Closeable... args) {
        if (args == null) return;
        for (Closeable arg : args) {
            close(arg);
        }
    }

    public static void close(Closeable arg) {
        try {
            if (arg != null) arg.close();
        } catch (Exception e) {
        }
    }
}
