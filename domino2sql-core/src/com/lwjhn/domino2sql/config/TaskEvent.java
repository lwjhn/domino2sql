package com.lwjhn.domino2sql.config;

import com.lwjhn.domino2sql.driver.AfterActionDriver;
import com.lwjhn.domino2sql.driver.BeforeActionDriver;
import com.lwjhn.domino2sql.driver.OnActionDriver;
import com.lwjhn.domino2sql.driver.PreparedSqlDriver;

public enum TaskEvent {
    prepared_sql_driver(PreparedSqlDriver.class, "code_0 : driver SQL"),
    before_action_driver(BeforeActionDriver.class, "code_1 : before the action to archive document"),
    on_action_driver(OnActionDriver.class, "code_2 : the action to archive document"),
    after_action_driver(AfterActionDriver.class, "code_2 : after the action to archive document");
    private Class<?> clazz;
    private String desc;
    public static final TaskEvent[] ALL = {prepared_sql_driver, before_action_driver, on_action_driver, after_action_driver};

    TaskEvent(Class<?> clazz, String desc) {
        this.desc = desc;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}