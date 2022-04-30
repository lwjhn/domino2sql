package com.rjsoft.driver;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.driver.DefaultDriverConfig;
import com.lwjhn.domino2sql.driver.OnActionDriver;
import com.lwjhn.domino2sql.driver.OnActionExtensionDocuments;
import com.lwjhn.util.AutoCloseableBase;
import com.lwjhn.util.BeanFieldsIterator;
import com.lwjhn.util.Common;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lotus.domino.Document;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonString;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Vector;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.driver
 * @Version: 1.0
 */
public class OnActionDriverMongoDb extends AbstractOnActionDriver {
    protected ExtendedOptions4MongoDb extendedOptions = null;

    Connection connection = null;
    private MongoClient mongoClient = null;
    private GridFSBucket bucket = null;
    private Statement statement = null;
    private OnActionDriver actionExtensionDocuments = null;

    @Override
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception {
        actionExtensionDocuments.action(preparedStatement, doc);
        if(extendedOptions.mongo_url!=null){
            handle(doc);
        }
    }

    @Override
    protected ExtendedOptions getExtended_options() {
        return extendedOptions;
    }

    @Override
    public void upload(InputStream input, String name, String alias, String type, Document doc) {
        try {
            Vector<?> vector = session.evaluate(extendedOptions.mongodbFileId(), doc);
            if(Common.isEmptyCollection(vector) || vector.get(0)==null){
                throw new Exception("evaluate the field of extended_options.mongo_file_id_formula, and return null !! " + extendedOptions.mongo_file_id_formula);
            }
            BsonString fileId = new BsonString(vector.get(0).toString());
            vector = session.evaluate(extendedOptions.sqlFormula(alias), doc);
            for(Object v : vector){
                this.dbgMsg(v.toString());
                statement.execute(v.toString());
            }
            bucket.uploadFromStream(fileId, name, input);
        }catch (Exception e){
            this.dbgMsg(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection) throws Exception {
        this.databaseCollection = databaseCollection;
        this.connection = connection;
        this.setDebug(dbConfig.isDebugger());
        session = databaseCollection.getSession();
        actionExtensionDocuments = new OnActionExtensionDocuments();
        actionExtensionDocuments.init(dbConfig, connection, databaseCollection);
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        actionExtensionDocuments.initDbConfig(this.dbConfig = dbConfig);
        extendedOptions = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), ExtendedOptions4MongoDb.class);

        BeanFieldsIterator.iterator(ExtendedOptions4MongoDb.class, field -> {
            if (String.class.equals(field.getType()) && (StringUtils.isBlank((String) BeanFieldsIterator.getFieldValue(field, extendedOptions)))) {
                throw new RuntimeException("the field of extended_options." + field.getName() + " is blank .");
            }
            return false;
        });

        this.setDebug(dbConfig.isDebugger());
        this.dbgMsg("connect mongodb ... \n" + extendedOptions.mongo_url);

        ConnectionString connString = new ConnectionString(extendedOptions.mongo_url);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(extendedOptions.mongo_db);
        bucket = GridFSBuckets.create(database, extendedOptions.mongo_bucket);

        AutoCloseableBase.close(statement);
        try {
            statement = connection.createStatement();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void recycle() {
        AutoCloseableBase.close(mongoClient, statement);
        statement = null; bucket = null;
        BaseUtils.recycle(actionExtensionDocuments);
        actionExtensionDocuments = null;
    }
}
