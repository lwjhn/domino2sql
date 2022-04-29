package com.rjsoft.driver;

import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.driver.DefaultDriverConfig;
import com.lwjhn.domino2sql.driver.OnActionDriver;
import com.lwjhn.util.FileOperator;
import lotus.domino.Document;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.driver
 * @Version: 1.0
 */
public class OnActionDriverLocalFile extends AbstractOnActionDriver implements OnActionDriver {
    protected String ftppath = null;
    protected ExtendedOptions4LocalFile extended_options = null;
    protected int index = -1;
    protected JSONObject response;
    protected String exportPath;

    @Override
    public void action(PreparedStatement preparedStatement, Document doc) throws Exception {
        if (extended_options.sql_field_attachment == null) return;
        response = new JSONObject();
        exportPath = FileOperator.getAvailablePath(ftppath,
                (String) databaseCollection.getSession().evaluate(extended_options.ftppath_formula, doc).get(0)
        ).toLowerCase();
        handle(doc);
        preparedStatement.setObject(index, response.toString(), extended_options.sql_field_attachment.getJdbc_type().getVendorTypeNumber(), extended_options.sql_field_attachment.getScale_length());
    }

    @Override
    public void upload(InputStream input, String name, String alias, String type, Document doc) {
        File file;String local;
        try {
            (file = new File(local = FileOperator.getAvailablePath(exportPath, type).toLowerCase())).mkdirs();
            FileOperator.newFile(file = new File(file.getCanonicalPath() + "/"  + name), input);
        }catch (Exception e){
            throw new RuntimeException(this.dbgMsg(e.getMessage()));
        }

        if (!file.exists())
            throw new RuntimeException(this.dbgMsg("OnActionDriverLocalFile.upload error : create file error ! " + file.getName()));
        DefaultDriverConfig.putFile(response, type, name, alias, local);
    }

    @Override
    protected ExtendedOptions getExtended_options() {
        return this.extended_options;
    }

    @Override
    public void initDbConfig(DbConfig dbConfig) {
        super.initDbConfig(dbConfig);
        if ((ftppath = dbConfig.getFtppath()) == null)
            ftppath = DefaultConfig.FTPPATH;

        extended_options = DefaultDriverConfig.parseExtendedOptions(dbConfig.getExtended_options(), ExtendedOptions4LocalFile.class);
        index = dbConfig.getSql_field_others().length + 1;
    }

    @Override
    public void recycle() {

    }
}
