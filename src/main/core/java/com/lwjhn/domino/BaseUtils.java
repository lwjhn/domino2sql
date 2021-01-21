package com.lwjhn.domino;

import lotus.domino.Base;

import java.util.Map;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino
 * @Version: 1.0
 */
public class BaseUtils {
    public static void recycle(Base ltobj) {
        if(ltobj==null) return;
        synchronized(ltobj) {
            try {
                if (ltobj != null) ltobj.recycle();
            } catch (Exception e) {
            }
        }
    }

    public static void recycle(Object ltobj) {
        if(ltobj==null) return;
        synchronized(ltobj) {
            if (ltobj instanceof Base) {
                recycle((Base) ltobj);
            } else if (ltobj instanceof Iterable) {
                recycle((Iterable) ltobj);
            } else if (ltobj instanceof Map) {
                recycle((Map) ltobj);
            }
        }
    }

    public static void recycle(Object... lotusObjects) {
        if(lotusObjects==null) return;
        synchronized(lotusObjects) {
            for(Object ltobj : lotusObjects)
                recycle(ltobj);
        }
    }

    public static void recycle(Iterable lotusObjects) {
        if(lotusObjects==null) return;
        synchronized(lotusObjects) {
            for(Object ltobj : lotusObjects)
                recycle(ltobj);
        }
    }

    public static void recycle(Map lotusObjects) {
        if(lotusObjects==null) return;
        synchronized(lotusObjects) {
            recycle(lotusObjects.values());
        }
    }
}
