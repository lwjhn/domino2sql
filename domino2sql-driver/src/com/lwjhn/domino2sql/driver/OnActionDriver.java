package com.lwjhn.domino2sql.driver;

import lotus.domino.Document;

import java.sql.PreparedStatement;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public interface OnActionDriver extends ActionDriver{
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception;
}
