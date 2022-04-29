package com.lwjhn.domino2sql.driver;

import lotus.domino.Document;

/**
 * @Author: lwjhn
 * @Date: 2020-11-24
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public interface BeforeActionDriver extends ActionDriver{
    void action(Document doc) throws Exception;
}
