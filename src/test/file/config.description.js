export default {
  "sql_driver": "com.mysql.cj.jdbc.Driver",  //驱动
  "sql_url": "jdbc:mysql://192.168.210.153:3399", //连接
  "sql_username": "******", //用户
  "sql_password": "******", //密码
  "options": [ //数组类型，各数据库归档配置
    {
      "enable": true, //是否执行
      "version": "1.1", //版本号，导出文件路径含版本号，及domino文件将保持版本号，当迁移中断后，可以排除错误文档后继续归档。
      "ftppath": "/FTP_XC/", //导出本地附件路径
      "ftppath_regex": "", //导出路径替换特定字符
      "domino_server": "server1", //迁移数据库所属服务器
      "domino_query": "Form=\"Document\" & Published=\"1\" ", //迁移文档dbsearch查询公式
      "domino_dbpath": "oa1/dispatch.nsf",
      "domino_queries": [{ //多模块使用相同配置，外层domino_server，domino_dbpath，domino_query三个参数不全的情况作为默认值
        "domino_dbpath": "oa1/dispatch.nsf",
      },{
        "domino_dbpath": "oa2/dispatch.nsf",
        "domino_query": "Form=\"Document\" & Published=\"0\""
      },{
        "domino_server": "server2",
        "domino_dbpath": "oa2/dispatch.nsf",
        "domino_query": "Form=\"Document\" & Published=\"1\""
      } ,{
          "enable": false,  //设置为false不执行
          "domino_server": "server3",
          "domino_dbpath": "oa4/dispatch.nsf"
        }],
      /*"sql_update_primary_key": {   //有配置，按Update模式处理。
        "sql_name": "ID",
        "domino_name": "ArcXC_UUID_16",
        "jdbc_type": "VARCHAR",
        "scale_length": 0
      },
      "update_mode_no_insert": true, //true - 只更新， false - 更新,无关联文档则插入
      */
      "domino_error_flag_field": "ArcXC_Error_FLAG", //Domino归档错误标记字段名，用于获取当前归档中错误的文件。二次归档时，可以在domino_query增加此字段的条件
      "sql_table": "EX_NPXC.EGOV_EX_DOC_HISTORY", //归档对应的表名称
      "domino_before_prepared_driver": "com.rjsoft.prepared.BeforePreparedRJDoc", //1. 不配置：仅导出正文附件； 2. 配置com.rjsoft.prepared.BeforePreparedRJDoc,正文附件，流程，意见，办理单； 3. 配置com.rjsoft.prepared.BeforeArchive，正文附件，流程，意见。
      "domino_after_prepared_driver": "com.lwjhn.test.AfterPrepareDocument",
      "domino_prepared_sqlquery_driver" : "com.rjsoft.prepared.PrepareSqlQueryRJDoc",
      "domino_process_statement_driver": "com.rjsoft.prepared.ProcessStatementRJDoc",
      "extended_options": {
        "sql_field_attachment": { //配置则导出MssDatabase相关文件附件，不配置则不导出附件，可以结合domino_before_prepared_driver设置导出内容
          "sql_name": "JSONATT",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        }
      },
      "sql_field_others": [{
          "sql_name": "SUBJECT", //sql字段名
          "domino_name": "subject", //domino字段名          
          "jdbc_type": "VARCHAR", //常用类型，CHAR, VARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR, LONGVARCHAR, INTEGER, DECIMAL, DOUBLE, FLOAT, NULL, NUMERIC, TIMESTAMP, DATE, TIME, TIME_WITH_TIMEZONE, TIMESTAMP_WITH_TIMEZONE
          //所有類型如下：ARRAY, BIGINT, BINARY, BIT, BLOB, BOOLEAN, CHAR, CLOB, DATALINK, DATE, DECIMAL, DISTINCT, DOUBLE, FLOAT, INTEGER, JAVA_OBJECT, LONGNVARCHAR, LONGVARBINARY, LONGVARCHAR, NCHAR, NCLOB, NULL, NUMERIC, NVARCHAR, OTHER, REAL, REF, REF_CURSOR, ROWID, SMALLINT, SQLXML, STRUCT, TIME, TIME_WITH_TIMEZONE, TIMESTAMP, TIMESTAMP_WITH_TIMEZONE, TINYINT, VARBINARY, VARCHAR
          "scale_length": 0   //長度，未配置默認為0
        },
        {
          "sql_name": "PUBLISHTIME",
          "domino_name": "publictime",
          "jdbc_type": "Timestamp"
        },
        {
          "sql_name": "PRINTCOUNT",
          "domino_name": "PrintCount",
          "jdbc_type": "Int"
        },
        {
          "sql_name": "SYSTEMNUMBER",
          "domino_formula": "@LeftBack(@Subset(@DbName;-1);\"\\\\\")",  //domino_formula使用公式賦值。如@UserName、"1"、1.1，分別返回用戶名，字符串1，數值1.1
          "jdbc_type": "String"
        },
        {
          "sql_name": "test",
          "domino_formula": "@Left(testField;32)",
          "jdbc_type": "String"
        },
        {
          "sql_name": "checkField",
          "domino_formula": "ret:=@Implode(checkField;\"; \");@If(@Length(ret)>32;@Error; ret);",
          "jdbc_type": "String"
        },
      ]
    }
  ]
}