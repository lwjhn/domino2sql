package com.lwjhn.domino2sql.driver;

import lotus.domino.NotesException;

import java.util.List;

/**
 * @Author: lwjhn
 * @Date: 2020-11-24
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public interface PreparedSqlDriver extends ActionDriver{
    void action(List<String> names , List<String> value) throws NotesException, Exception;
}
