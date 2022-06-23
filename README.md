### domino service

#### 启动diiop服务

```
    load diiop
```

### 启动TCP PROXY [可选]

#### window版

```
    tcp-proxy.exe -l=":9898" -r="192.168.211.53:63148"
```

#### window/Linux版（JRE 8）

```shell
    java -jar tcp-proxy.jar 9898 192.168.211.53 63148

    # linux
    # kill app
    kill -9 $(ps -aef | grep port=9898 | grep -v grep | awk '{print $2}')
    # start tcp proxy
    nohup java -Xmx200m -Xms150m -jar tcp-proxy.jar 9898 192.168.211.53 63148 > nohup.log 2>&1 &
```

### 迁移配置文件
迁移驱动，参看驱动说明。参见配置案例：
+ 附件上传mongodb：[mongodb.rongji案例](./domino2sql-app/resources/arc.sql.config.mongodb.json)
+ 附件上传本地磁盘：[local.file案例](./domino2sql-app/resources/arc.sql.config.local.file.json)
+ 迁移关联子文档模式：[file.extension案例](./domino2sql-app/resources/arc.sql.config.local.file.extension.json)

### 启动迁移服务

`domino2sql_app_jar`，启动`domino2sql-app.jar`。注意相关依赖包，当前目录下已提供达梦驱动

```
java -DDominoHost="192.168.211.53:63148" -DDominoUser="Admin" -DDominoPassword="*****" -classpath . -jar ./domino2sql-app.jar

java -DDominoHost="192.168.210.153:9898" -DDominoUser="Admin" -DDominoPassword="*****" -DDominoPath="./arc.sql.config.json" -DDominoOutput="./arc.sql.config.output.json" -jar ./domino2sql-app.jar



注意：domino服务器需要启动doiip服务，命令load diiop

```

#### 附件乱码

linux 导出附件中文乱码，运行参数宜加上  `-Dsun.jnu.encoding=UTF-8`

#### 驱动依赖

> > 依赖可以到domino2sql-app.jar当前路径的子文件夹`lib`下，或者放到jvm lib下。

##### 查看jvm lib路径

执行 `java -verbose`，查看jvm lib 路径

```shell
C:\Users\Administrator>java -verbose
[Opened C:\Program Files\Java\jdk1.8.0_112\jre\lib\rt.jar]
```

依赖拷贝到lib的`ext`文件夹中

## 信创数据迁移

> > 建议使用中间表，迁移最后合并，或者将新创16位ID全部改成32位

### ID关联

> > 主表增加`DOMINO_ID`字段， 意见表增加`DOMINO_ID`及`DOMINO_PID`字段，迁移后，通SQL语句重新关联

```sql
--查看
SELECT O.*
FROM EGOV_OPINION AS O
         LEFT JOIN EGOV_DISPATCH AS D
                   ON O.DOMINO_PID = D.DOMINO_ID AND O.DOMINO_PID IS NOT NULL
WHERE O.DOC_ID IS NULL

-- 修改
UPDATE EGOV_OPINION AS O
SET (O.DOC_ID) = (
    SELECT D.ID
    FROM EGOV_DISPATCH AS D
    WHERE O.DOMINO_PID = D.DOMINO_ID
)
WHERE O.DOMINO_PID IS NOT NULL
```

## 驱动事件类型

### prepared_sql_driver

`prepared_sql_driver`事件，预处理sql时触发。常用驱动，如`com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile`

### before_action_driver

`before_action_driver`事件，执行当前文档前执行。常用驱动，如：

+ `com.lwjhn.domino2sql.driver.ActionShowLog`，打印UNID
+ `com.lwjhn.domino2sql.driver.BeforeActionDriverFormula`，执行前，执行相关domino公式，如有返回sql语句，执行sql，如用于删除重复迁移记录

### on_action_driver

`on_action_driver`事件，插入记录过程中触发。常用驱动，如：

+ `com.lwjhn.domino2sql.driver.OnActionExtensionDocuments`，递归执行`extended_options`字段配置`children`子迁移项
+ `com.lwjhn.domino2sql.driver.OnActionExtractFile`，导出当前文档附件至本地，配合`com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile`
  使用
+ `com.rjsoft.driver.OnActionDriverLocalFile`
  ，RJ相关驱动，当前文档关联附件、办理单、意见、及流程记录以附件形式导出本地，配合`com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile`使用
+ `com.rjsoft.driver.OnActionDriverMongoDb`，RJ相关驱动，当前文档关联附件、办理单、意见、及流程记录以附件形式上传MongoDb

### after_action_driver

`after_action_driver`事件，迁移完成后触发。常用驱动，如：

+ `com.lwjhn.domino2sql.driver.ActionShowLog`，打印UNID
+ `com.lwjhn.domino2sql.driver.AfterActionDriverFormula`，执行后，执行相关domino公式，如有返回sql语句，执行sql，如通过DominoID，重新关联


## 驱动说明

### com.lwjhn.domino2sql.driver.BeforeActionDriverFormula

执行前，执行相关domino公式，如有返回sql语句，执行sql，如用于删除重复迁移记录。须配置`extended_options`的"before_sql_formula"项，例子见`AfterActionDriverFormula`

### com.lwjhn.domino2sql.driver.AfterActionDriverFormula

执行前，执行相关domino公式，如有返回sql语句，执行sql，如用于删除重复迁移记录。须配置`extended_options`的"after_sql_formula"项

```js
// before_sql_formula：删除已迁移的附件。after_sql_formula：重新关联意见文档，修改意见文档DOC_ID，当然也可以迁移后手动处理
setting = {
    "before_sql_formula": "FileId:= ArcXC_UUID_16; @If(FileId=\"\";@Nothing; \"UPDATE EGOV_ATT SET STATUS = '删除' WHERE DOC_ID = '\"+FileId+\"'\");",
    "after_sql_formula": "docId:= ArcXC_UUID_16; DOMINO_PID := @Text(@DocumentUniqueID);\n@if(docId=\"\";@Nothing; \"UPDATE EGOV_OPINION SET DOC_ID = '\"+ArcXC_UUID_16+\"' WHERE DOMINO_PID ='\"+DOMINO_PID+\"'\");",
}
```

### com.lwjhn.domino2sql.driver.OnActionExtractFile

导出文档配置项执行文档中的所有附件到本地，需配置附件路径SQL存储字段`extended_options`，以及`prepared_sql_driver: com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile`

```js
setting = {
    "prepared_sql_driver": "com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile",   //预处理SQL语句事件，处理扩展附件字段
    "on_action_driver": "com.lwjhn.domino2sql.driver.OnActionExtractFile",    //执行事件，导出当前文档附件
    "after_action_driver": "com.lwjhn.domino2sql.driver.ActionShowLog", //完成事件，打印文档ID
    "extended_options": {
        "sql_field_attachment": {   //导出路径信息字段
            "sql_name": "JSONATT",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        }
    }
}
```

### com.lwjhn.domino2sql.driver.OnActionExtensionDocuments

+ 支持通过`extended_options`字段配置`children`子迁移项，迁移关联子文档。
+ 注意：迁移配置项中`domino_server`、`domino_dbpath`、`domino_query`，此处使用domino formula,关联父文档。
+ 此三个字段，返回多值，则按顺序执行多个数据库查询。

```js
setting = {
    "children": [
        {
            "domino_server": "@If(MSSSERVER=\"\";@ServerName;MSSSERVER)",
            "domino_dbpath": "@If(MSSOpinion=!\"\";MSSOpinion;OpinionlogDatabase!=\"\";OpinionlogDatabase;@ServerName)",
            "domino_query": "\"Form=\\\"Opinion\\\" & PARENTUNID=\\\"\"+@Text(@DocumentUniqueID)+\"\\\"\"",
            "after_action_driver": "com.lwjhn.domino2sql.driver.ActionShowLog"
        }
    ]
}
```

### com.rjsoft.driver.OnActionDriverLocalFile
+ 此接口实例了`com.lwjhn.domino2sql.driver.OnActionExtensionDocuments`驱动
+ 导出文档配置项执行文档中的所有附件到本地，需配置附件路径SQL存储字段`extended_options`，以及`prepared_sql_driver: com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile`
+ 默认关联附件查询选项，其它特殊情况可以自己修改
+ `attachment_server: "@If(MSSSERVER=\"\";@ServerName;MSSSERVER)"`
+ `attachment_dbpath: "MSSDATABASE"`
+ `attachment_query: "\"!@Contains(Form;\\\"DelForm\\\") & (DOCUNID = \\\"\" + @Text(@DocumentUniqueID) + \"\\\"\" + @If(UniAppUnid=\"\";\"\";\" | DOCUNID = \\\"\" + UniAppUnid + \"\\\"\") + @If(MSSUNID=\"\";\"\";\" | @Text(@DocumentUniqueID) = \\\"\" + MSSUNID + \"\\\"\") + \")\""`

```js
setting = {
    "prepared_sql_driver": "com.lwjhn.domino2sql.driver.PreparedSql4ExtractFile",   //预处理SQL语句事件，处理扩展附件字段
    "on_action_driver": "com.rjsoft.driver.OnActionDriverLocalFile",    //执行事件，导出当前文档附件
    "after_action_driver": "com.lwjhn.domino2sql.driver.ActionShowLog", //完成事件，打印文档ID
    "extended_options": {   //导出路径信息字段
        "export_flow": true,    //是否以附件方式导出流程记录
        "export_processing": true,   //是否以附件方式导出办理单
        "export_opinion": true, //是否以附件方式导出意见文件
        "sql_field_attachment": {
            "sql_name": "JSONATT",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        }
    }
}
```

### com.rjsoft.driver.OnActionDriverMongoDb

+ 此接口实例了`com.lwjhn.domino2sql.driver.OnActionExtensionDocuments`驱动
+ 使用实现了`com.rjsoft.driver.OnActionDriverLocalFile`接口，可以附件方式导出意见，办理单，及流程记录
+ 附件上传MongoDb，`extended_options`需配置`mongo_url`、`mongo_db`、`mongo_bucket`
+ `mongo_file_id_formula`及`mongo_map_sql_formula`支持扩展公式`@UUID16`、`@UUID32`（32位随机UUID）、`@FileName`（附件别名）、`@FileSuffix`（扩展名）、`@FileType`（文件类别，如MSS、Attachment、flow、processing、opinion）。
+ `mongo_file_id_formula`上传附件ID，`mongo_map_sql_formula`处理关联附件表语句

```js
setting = {
    "enable": true,
    "version": "1.6.7",
    "ftppath": "/FTP_XC/",
    "domino_server": "OAAPP/SRV/NANPING",
    "domino_queries": [ //未配置，则取外层执行，否则外层作为默认值，循环迁移此项配置的所有数据
        {
            "enable": true,
            "error_continue": true,
            "domino_query": "Form=\"FlowForm\" & MSSDATABASE!=\"\" & @Text(@DocumentUniqueID)=\"256C00C83CBF5064482582A40024C167\"",
            "domino_dbpath": "egov/dispatch.nsf",
            "update_mode_no_insert": false  //配合sql_update_primary_key使用，未找到关联数据，是否插入数据
        }
    ],
    "before_action_driver": "com.lwjhn.domino2sql.driver.BeforeActionDriverFormula",
    "on_action_driver": "com.rjsoft.driver.OnActionDriverMongoDb",
    "after_action_driver": "com.lwjhn.domino2sql.driver.AfterActionDriverFormula",
    "extended_options": {
        "before_sql_formula": "FileId:= ArcXC_UUID_16; @If(FileId=\"\";@Nothing; \"UPDATE EGOV_ATT SET STATUS = '删除' WHERE DOC_ID = '\"+FileId+\"'\");",
        "after_sql_formula": "docId:= ArcXC_UUID_16; DOMINO_PID := @Text(@DocumentUniqueID);\n@if(docId=\"\";@Nothing; \"UPDATE EGOV_OPINION SET DOC_ID = '\"+ArcXC_UUID_16+\"' WHERE DOMINO_PID ='\"+DOMINO_PID+\"'\");",
        "export_flow": true,
        "export_processing": false,
        "export_opinion": false,
        "mongo_url": "mongodb://192.168.*.*:27017",
        "mongo_db": "sftoamongo",
        "mongo_bucket": "fs",
        "mongo_file_id_formula": "@SetField(\"_mongodb_file_id_\";\"@UUID16\");_mongodb_file_id_",
        "mongo_map_sql_formula": "DocId:= ArcXC_UUID_16;\nFileId:= _mongodb_file_id_;\nFileType:=@If(@LowerCase(\"@FileType\")=\"mss\";\"main_doc\";\"attach\");\n\"INSERT INTO EGOV_ATT (ID, MODULE_ID, DOC_ID, EGOV_FILE_ID, FILE_NAME, FILE_SUFFIX, \\\"TYPE\\\", STATUS, SORT_TIME, CREATE_TIME) \n\tVALUES('\"+ FileId +\"', '\"+@Right(@ReplaceSubstring(@Left(@UpperCase(@Subset(@DbName;-1));\".\");\"\\\\\";\"/\");\"/\")+\"', '\" + DocId + \"', '\"+ FileId + \"', '@FileName',  '@FileSuffix', '\"+FileType+\"','正常', 0, NOW);\nINSERT INTO EGOV_FILE (ID, FILE_PATH, CREATE_TIME) VALUES('\"+ FileId +\"', '\"+ FileId +\"', NOW);\"",
        "children": [
            {
                "enable": true,
                "vesion": "1.6.4",
                "ftppath": "/FTP_XC/",
                "domino_server": "@If(MSSSERVER=\"\";@ServerName;MSSSERVER)",
                "domino_dbpath": "@If(MSSOpinion=!\"\";MSSOpinion;OpinionlogDatabase!=\"\";OpinionlogDatabase;@ServerName)",
                "domino_query": "\"Form=\\\"Opinion\\\" & PARENTUNID=\\\"\"+@Text(@DocumentUniqueID)+\"\\\"\"",
                "after_action_driver": "com.lwjhn.domino2sql.driver.ActionShowLog",
                "update_mode_no_insert": false,
                "sql_update_primary_key": {
                    "sql_name": "DOMINO_ID",
                    "domino_formula": "@Text(@DocumentUniqueID)",
                    "jdbc_type": "VARCHAR",
                    "scale_length": 0
                },
                "sql_table": "EGOV_OPINION",
                "sql_field_others": [
                    {
                        "sql_name": "ID",
                        "domino_name": "ArcXC_UUID_16",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "DOMINO_ID",
                        "domino_formula": "@Text(@DocumentUniqueID)",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "DOC_ID",
                        "domino_formula": "@Nothing",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "DOMINO_PID",
                        "domino_name": "PARENTUNID",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_CODE",
                        "domino_name": "OPINIONFIELD",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_CODE_NAME",
                        "domino_name": "OPINIONTYPE",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_CONTENT",
                        "domino_name": "OPINIONBODY",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_USER",
                        "domino_name": "OPINIONUSERTITLE",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_USER_NO",
                        "domino_name": "OPINIONUSER",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_DEPT",
                        "domino_name": "OPINIONDEPTTITLE",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "OPINION_DEPT_NO",
                        "domino_name": "OPINIONDEPT",
                        "jdbc_type": "VARCHAR",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "CREATE_TIME",
                        "domino_formula": "OPINIONTIME",
                        "jdbc_type": "TIMESTAMP",
                        "scale_length": 0
                    },
                    {
                        "sql_name": "LAST_UPDATE_TIME",
                        "domino_formula": "OPINIONTIME",
                        "jdbc_type": "TIMESTAMP",
                        "scale_length": 0
                    }
                ]
            }
        ]
    },
    "sql_table": "EGOV_DISPATCH",
    "sql_update_primary_key": { //找到相关迁移文档，则更新数据
        "sql_name": "DOMINO_ID",
        "domino_formula": "@Text(@DocumentUniqueID)",
        "jdbc_type": "VARCHAR",
        "scale_length": 0
    },
    "sql_field_others": [
        {
            "sql_name": "ID",
            "domino_name": "ArcXC_UUID_16",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        },
        {
            "sql_name": "DOMINO_ID",
            "domino_formula": "@Text(@DocumentUniqueID)",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        },
        {
            "sql_name": "IS_HISTORY_FILE",
            "domino_formula": "\"01\"",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        },
        {
            "sql_name": "SUBJECT",
            "domino_formula": "Subject + @Text(@DocumentUniqueID)",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        },
        {
            "sql_name": "DOC_WORD",
            "domino_name": "DocWord",
            "jdbc_type": "VARCHAR",
            "scale_length": 0
        }
    ]
}
```


## 新创公文交换启用
### 配置
参考配置文件`arc.sql.config.mongodb.ex-doc.json`。第一项配置，迁移主文档，及已签收记录；第二项，迁移各库待收记录
### 迁移后，子表数据处理
执行SQL语句，关联主表与记录表数据，并更新相关字段数据
```sql
UPDATE EGOV_EX_RECEIVE_TRACK AS t
SET (DOC_ID ,SUBJECT, DOC_MARK, URGENCY, SEND_TIME) = (
	SELECT d.ID,  d.SUBJECT,  d.DOC_MARK,  d.URGENCY,  d.SEND_TIME FROM EGOV_EX_DOC AS d
	WHERE t.DOMINO_PID = d.INITUNID
)
WHERE t.DOMINO_ID IS NOT NULL
```