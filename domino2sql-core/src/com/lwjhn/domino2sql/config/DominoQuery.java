package com.lwjhn.domino2sql.config;

import java.io.Serializable;

/**
 * @Author: lwjhn
 * @Date: 2021-4-2
 * @Description: com.lwjhn.domino2sql.config
 * @Version: 1.0
 */
public class DominoQuery implements Cloneable, Serializable {
    private String vesion = DefaultConfig.VERSION;
    private String ftppath = DefaultConfig.FTPPATH;
    private String ftppath_regex = DefaultConfig.FTPPATH_REGEX;
    private String domino_uuid_prefix = DefaultConfig.Domino_UUID_Prefix;
    private String domino_server = null;
    private String domino_dbpath = null;
    private String domino_query = null;
    private boolean enable = DefaultConfig.ENABLE;
    private boolean error_continue = DefaultConfig.PROCESS_ERROR_CONTINUNE;
    private boolean update_mode_no_insert = DefaultConfig.UPDATE_MODE_NO_INSERT;
    private String domino_error_flag_field = DefaultConfig.Domino_Error_Flag_Field;
    private String domino_succ_flag_field = DefaultConfig.Domino_Succ_Flag_Field;
    private String action_error_log = null;
    private int action_all_count = 0;
    private int action_succ_count = 0;
    private int action_err_count = 0;
    private boolean debugger = DefaultConfig.DEBUGGER;

    public String getDomino_uuid_prefix() {
        return domino_uuid_prefix;
    }

    public void setDomino_uuid_prefix(String domino_uuid_prefix) {
        this.domino_uuid_prefix = domino_uuid_prefix;
    }

    public String getVesion() {
        return vesion;
    }

    public void setVesion(String vesion) {
        this.vesion = vesion;
    }

    public String getFtppath() {
        return ftppath;
    }

    public void setFtppath(String ftppath) {
        this.ftppath = ftppath;
    }

    public String getFtppath_regex() {
        return ftppath_regex;
    }

    public void setFtppath_regex(String ftppath_regex) {
        this.ftppath_regex = ftppath_regex;
    }

    public String getDomino_error_flag_field() {
        return domino_error_flag_field;
    }

    public void setDomino_error_flag_field(String domino_error_flag_field) {
        this.domino_error_flag_field = domino_error_flag_field;
    }

    public String getDomino_succ_flag_field() {
        return domino_succ_flag_field;
    }

    public void setDomino_succ_flag_field(String domino_succ_flag_field) {
        this.domino_succ_flag_field = domino_succ_flag_field;
    }

    public String getAction_error_log() {
        return action_error_log;
    }

    public void setAction_error_log(String action_error_log) {
        this.action_error_log = action_error_log;
    }

    public int getAction_all_count() {
        return action_all_count;
    }

    public void setAction_all_count(int action_all_count) {
        this.action_all_count = action_all_count;
    }

    public int getAction_succ_count() {
        return action_succ_count;
    }

    public void setAction_succ_count(int action_succ_count) {
        this.action_succ_count = action_succ_count;
    }

    public int getAction_err_count() {
        return action_err_count;
    }

    public void setAction_err_count(int action_err_count) {
        this.action_err_count = action_err_count;
    }

    public boolean isDebugger() {
        return debugger;
    }

    public void setDebugger(boolean debugger) {
        this.debugger = debugger;
    }

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

    public boolean isError_continue() {
        return error_continue;
    }

    public void setError_continue(boolean error_continue) {
        this.error_continue = error_continue;
    }

    public boolean isUpdate_mode_no_insert() {
        return update_mode_no_insert;
    }

    public void setUpdate_mode_no_insert(boolean update_mode_no_insert) {
        this.update_mode_no_insert = update_mode_no_insert;
    }

    public DominoQuery() {
    }

    public DominoQuery(String domino_server, String domino_dbpath, String domino_query) {
        this.domino_server = domino_server;
        this.domino_dbpath = domino_dbpath;
        this.domino_query = domino_query;
    }

    public DominoQuery clone() throws CloneNotSupportedException {
        return (DominoQuery) super.clone();
    }
}
