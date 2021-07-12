package com.lwjhn.domino;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @Author: lwjhn
 * @Date: 2020-11-20
 * @Description: com.lwjhn.domino
 * @Version: 1.0
 */
public class Message {
    private boolean m_debug = true;

    public String getStackMsg(Throwable e) {
        StringBuilder res = new StringBuilder();
        res.append(e.toString() + "\n");
        for (StackTraceElement traceElement : e.getStackTrace())
            res.append("\tat " + traceElement.toString() + "\n");
        return res.toString();
    }

    public String dbgMsg(String msg) {
        if (this.m_debug) {
            this.dbgMsg(msg, System.out);
        }
        return msg;
    }

    public String dbgMsg(String msg, PrintWriter out) {
        if (this.m_debug) {
            out.println("dbg: " + msg);
        }
        return msg;
    }

    private String dbgMsg(String msg, PrintStream out) {
        if (this.m_debug) {
            out.println("dbg: " + msg);
        }
        return msg;
    }

    public void setDebug(boolean m_debug) {
        this.m_debug = m_debug;
    }

}
