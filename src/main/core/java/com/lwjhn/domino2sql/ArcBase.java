package com.lwjhn.domino2sql;

import com.lwjhn.domino.LotusBase;
import com.lwjhn.util.AutoCloseableBase;
import com.lwjhn.util.CloseableBase;

import java.io.Closeable;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public abstract class ArcBase extends LotusBase implements Closeable {
    public static void close(AutoCloseable... args) {
        AutoCloseableBase.close(args);
    }
    public static void close(Closeable... args) {
        CloseableBase.close(args);
    }

    public abstract void recycle();

    public void close() {
        this.recycle();
    }
}
