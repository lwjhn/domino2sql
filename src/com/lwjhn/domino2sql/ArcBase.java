package com.lwjhn.domino2sql;

import com.lwjhn.domino.AutoCloseableBase;
import com.lwjhn.domino.LotusBase;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public abstract class ArcBase extends LotusBase implements AutoCloseable {
    public static void close(AutoCloseable... args) {
        AutoCloseableBase.close(args);
    }

    public abstract void recycle();

    public void close() {
        this.recycle();
    }
}
