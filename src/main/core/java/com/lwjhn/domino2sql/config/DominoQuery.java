package com.lwjhn.domino2sql.config;

/**
 * @Author: lwjhn
 * @Date: 2021-4-2
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class DominoQuery {
    private String domino_server = null;
    private String domino_dbpath = null;
    private String domino_query = null;
    private boolean enable = DefaultConfig.ENABLE;

    public String getDomino_server() {
        return domino_server;
    }

    public void setDomino_server(String domino_server) {
        this.domino_server = domino_server;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDomino_dbpath() {
        return domino_dbpath;
    }

    public void setDomino_dbpath(String domino_dbpath) {
        this.domino_dbpath = domino_dbpath;
    }

    public String getDomino_query() {
        return domino_query;
    }

    public void setDomino_query(String domino_query) {
        this.domino_query = domino_query;
    }

    public DominoQuery() {
    }

    public DominoQuery(String domino_server, String domino_dbpath, String domino_query) {
        this.domino_server = domino_server;
        this.domino_dbpath = domino_dbpath;
        this.domino_query = domino_query;
    }

}
