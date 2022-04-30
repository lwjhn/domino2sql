package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSON;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino.FormulaUtils;
import com.lwjhn.domino.Message;
import com.lwjhn.domino2sql.Task;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.util.BeanFieldsIterator;
import com.lwjhn.util.Common;
import lotus.domino.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.driver
 * @Version: 1.0
 */
public class OnActionExtensionDocuments extends Message implements OnActionDriver {
    protected DbConfig dbConfig;
    protected DatabaseCollection databaseCollection = null;
    protected Connection connection = null;
    protected ExtendedOptions extendedOptions;
    protected Task task = null;
    protected FormulaUtils formula;

    public enum DbSetField {domino_server, domino_dbpath, domino_query}

    protected static final Collection<DbSetField> DbSetFields = Arrays.asList(DbSetField.domino_server, DbSetField.domino_dbpath, DbSetField.domino_query);

    public static class ExtendedOptions {
        protected DbConfig[] children = null;
    }

    @Override
    public void recycle() {
        BaseUtils.recycle(task);
        databaseCollection = null;
        connection = null;
    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception {
        this.databaseCollection = databaseCollection;
        this.connection = connection;
        task = new Task();
        formula = new FormulaUtils(databaseCollection.getSession());
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        this.dbConfig=dbConfig;
        extendedOptions = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), ExtendedOptions.class);
        this.setDebug(dbConfig.isDebugger());
    }

    @Override
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception {
        if (extendedOptions == null || extendedOptions.children == null) return;

        for (DbConfig config : extendedOptions.children) {
            if (!config.isEnable()) continue;
            Map<DbSetField, Vector<?>> response = new HashMap<>();
            final int[] size = {Integer.MAX_VALUE};
            try {
                this.formula.evaluateBean(config, DbSetFields, doc, (s, objects) -> {
                    if (Common.isEmptyCollection(objects)) {
                        throw new RuntimeException("processing children : parameter " + s.toString() + " is empty !");
                    }
                    size[0] = Math.min(objects.size(), size[0]);
                    response.put(s, objects);
                });

                DbConfig setting = config.clone();
                for (int i = 0; i < size[0]; i++) {
                    int finalI = i;
                    DbSetFields.forEach(dbSetField -> BeanFieldsIterator.setField(dbSetField.toString(), setting, response.get(dbSetField).get(finalI)));
                    if (config.isEnable()){
                        task.processing(setting, connection, databaseCollection).recycle();
                    }
                }
            } catch (Exception e) {
                this.dbgMsg("OnActionExtensionDocuments.action Error : " + JSON.toJSONString(config));
                throwsError(e.getMessage());
            }
        }
    }

    private void throwsError(String message) {
        this.dbgMsg(message);
        dbConfig.setAction_error_log(message);
        if (!dbConfig.isError_continue()){
            throw new RuntimeException(message);
        }
    }
}
