package com.lwjhn.domino;

import lotus.domino.Base;

import java.util.Vector;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino
 * @Version: 1.0
 */
public abstract class LotusBase extends Message implements Base {
    public static void recycle(Base... lotusObjects) {
        BaseUtils.recycle(lotusObjects);
    }

    public void recycle(Vector vector) {
        BaseUtils.recycle(vector);
    }
}
