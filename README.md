# domino2sql
### Example
```java
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino2sql.Action;
import com.lwjhn.domino2sql.ArcConfigSerialization;
import com.lwjhn.domino2sql.config.ArcConfig;
import lotus.domino.*;
import java.io.File;

public class ArcAgent extends AgentBase {
    final String configpath = "D:/EX_NPXC/arc.sql.config.json";
    final String output = "D:/EX_NPXC/arc.sql.output.json";
    Session session = null;
    ArcConfig config = null;
    Action action = null;
    public void NotesMain() {
        try {
            System.out.println("ArchXC Agent Start . ");
            config = ArcConfigSerialization.parseArcConfig(new File(configpath));
            Action action = new Action(config,session = getSession());
            action.archive();
            action.recycle();
            action=null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(config!=null){
                    ArcConfigSerialization.toJSONFile(config,output);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BaseUtils.recycle(action, session);
            System.out.println("ArchXC Agent ShutDown . ");
        }
    }
}
```
### config document
```json
{
  "sql_url": "jdbc:mysql://192.168.210.153:3399",
  "sql_username": "******",
  "sql_password": "******",
  "sql_driver": "com.mysql.cj.jdbc.Driver",
  "options": [{
    "enable": true,
    "vesion": "2",
    "ftppath": "/FTP_XC/",
    "domino_server": "PUBLIC/SRV/RJSOFT",
    "domino_dbpath": "egov/dispatch.nsf",
    "domino_query": "Form=\"FlowForm\" & DraftDate > [2020-07-20] & DraftDate < [2020-07-25]",
    "domino_before_prepared_driver": "com.lwjhn.test.BeforePrepareDocument",
    "domino_after_prepared_driver": "com.lwjhn.test.AfterPrepareDocument",
    "sql_field_attachment": {
      "sql_name": "JSONATT",
      "jdbc_type": "VARCHAR",
      "scale_length": 0
    },
    "sql_table": "XC_DEMO.EGOV_DISPATCH_HISTORY",
    "sql_field_others": [{
        "sql_name": "ID",
        "domino_name": "ArcXC_UUID_16",
        "jdbc_type": "VARCHAR",
        "scale_length": 0
      }, {
        "sql_name": "DOMINOID",
        "domino_formula": "@Text(@DocumentUniqueID)",
        "jdbc_type": "VARCHAR",
        "scale_length": 0
      }, {
        "sql_name": "Subject",
        "domino_name": "Subject",
        "jdbc_type": "VARCHAR",
        "scale_length": 0
      }, {
        "sql_name": "SignDateTime",
        "domino_name": "SignDateTime",
        "jdbc_type": "TIMESTAMP",
        "scale_length": 0
      }, {
        "sql_name": "DraftDate",
        "domino_name": "DraftDate",
        "jdbc_type": "TIMESTAMP",
        "scale_length": 0
      },
      {
        "sql_name": "PrintNum",
        "jdbc_type": "INTEGER",
        "domino_formula": "1",
        "scale_length": 0
      },
      {
        "sql_name": "OTHER",
        "jdbc_type": "VARCHAR",
        "domino_formula": "\"測試公式\"",
        "scale_length": 0
      }
    ]
  }]
}
```