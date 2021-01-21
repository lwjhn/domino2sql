import com.alibaba.fastjson.JSON;
import com.lwjhn.domino2sql.ArcConfigSerialization;
import com.lwjhn.domino2sql.config.*;
import com.lwjhn.util.FileOperator;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lwjhn
 * @Date: 2020-11-18
 * @Description: PACKAGE_NAME
 * @Version: 1.0
 */
public class testArc {
    @Test
    public void generateConfig() throws Exception{
        ArcConfig config = new ArcConfig(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://192.168.210.153:3399",
                "rjsoft",
                "abcd@1234");

        List<DbConfig> dbcfgs = new ArrayList<>();
        DbConfig dbcfg = new DbConfig("PUBLIC/SRV/RJSOFT"
                ,"egov/dipatch.nsf",
                "Form=\"FlowForm\" & C_UserReader=\"\"");

        dbcfg.setDomino_error_flag_field("ArcXC_Error_FLAG");
        dbcfg.setDomino_succ_flag_field("ArcXC_Succ_FLAG");
        dbcfg.setSql_table("XC_DEMO.EGOV_DISPATCH_HISTORY");
        //dbcfg.setSql_field_attachment(new ItemConfig("JSONATT",JDBCType.VARCHAR,0));

        //设置item
        List<ItemConfig> itemConfigs = new ArrayList<>();
        itemConfigs.add(new ItemConfig("Subject","Subject",null,JDBCType.VARCHAR,512));
        itemConfigs.add(new ItemConfig("DocWord","DocWord",null,JDBCType.VARCHAR,256));
        itemConfigs.add(new ItemConfig("DraftDate","DraftDate",null,JDBCType.TIMESTAMP,0));
        itemConfigs.add(new ItemConfig("PrintNum","PrintNum",null,JDBCType.INTEGER,0));
        dbcfg.setSql_field_others(itemConfigs.toArray(new ItemConfig[itemConfigs.size()]));

        dbcfgs.add(dbcfg);
        config.setOptions(dbcfgs.toArray(new DbConfig[dbcfgs.size()]));

        System.out.println(JSON.toJSON(config));
        FileOperator.newFile("c:/test/arc_xc_3.json",JSON.toJSON(config).toString().getBytes("utf-8"));
    }

    @Test
    public void loadConfig() throws Exception{
        ArcConfig config = ArcConfigSerialization.parseArcConfig(new File("D:\\Workspaces\\IDEA\\domino2sql\\src\\test\\file\\arc.sql.config.rj.json"));
        if(config.getOptions()[0].getExtended_options()!=null) {
            ItemConfig attachment_config = config.getOptions()[0].getExtended_options().getObject("sql_field_attachment", ItemConfig.class);
            if(attachment_config!=null) {
                System.out.println(attachment_config.getSql_name());
                System.out.println(attachment_config.getJdbc_type().getName());
            }
        }
        //System.out.println(ArcConfigSerialization.toJSONString(config));
        ArcConfigSerialization.toJSONFile(config,"c:/test/arc_xc_10.json");
    }

    @Test
    public void test2() throws Exception {
        DriverManager.setLogWriter(new PrintWriter(System.out));
        Connection connection = createConnection("com.oscar.Driver", "jdbc:mysql://192.168.210.134:2003/OSCARDB?connectTimeout=1000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC", "FZSWOA", "FZSWOA");// createConnection("dm.jdbc.driver.DmDriver", "jdbc:dm://192.168.210.134:5236", "EX_NPXC", "EX_NPXCEX_NPXC");
        System.out.println(">>>----> 1111");
    }

    @Test
    public void test1() throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            System.out.println(System.getProperty("user.dir"));
            DriverManager.setLogWriter(new PrintWriter(System.out));
            connection = createConnection("dm.jdbc.driver.DmDriver", "jdbc:dm://192.168.210.134:5236", "EX_NPXC", "EX_NPXCEX_NPXC");

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO EGOV_EX_DOC_HISTORY (ID , SUBJECT , DOC_TYPE , DOC_MARK)"
                        + " VALUES (?, ?, ?, ?)"
            );
            preparedStatement.setObject(1, getUUID16(), JDBCType.VARCHAR.getVendorTypeNumber(), 0);
            preparedStatement.setObject(2, "标题-" + getUUID16(), JDBCType.VARCHAR.getVendorTypeNumber(), 0);
            preparedStatement.setObject(3, "决议", JDBCType.VARCHAR.getVendorTypeNumber(), 0);
            preparedStatement.setObject(4, "闽政通（2020）29号", JDBCType.VARCHAR.getVendorTypeNumber(), 0);

            int result = preparedStatement.executeUpdate();
            System.out.println("result:-->"+result);
            //getVendorTypeNumber
            preparedStatement.close();
            preparedStatement = connection.prepareStatement("SELECT * FROM EGOV_EX_DOC_HISTORY");
            resultSet = preparedStatement.executeQuery();
            int count = resultSet.getMetaData().getColumnCount();
            System.out.println("column:" + count);
            while (resultSet.next()) {
                for (int i = 1; i <= count; i++) {
                    System.out.print(resultSet.getString(i) + " , ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception e) {
            }
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
            }
            try {
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getUUID(int len) {
        int first = new Random(10).nextInt(8) + 1,
                hashCodeV = UUID.randomUUID().toString().hashCode();
        return first + String.format("%0"+len+"d", new Integer[]{Integer.valueOf(hashCodeV < 0 ? -hashCodeV : hashCodeV)});
    }
    @Test
    public void testUUID(){
        final Pattern PATTERN_EXT = Pattern.compile("\\.[0-9a-z]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = PATTERN_EXT.matcher("abcd.txt");
        System.out.println(matcher.find() ? matcher.group() : "");
    }

    public static String getUUID16() {
        int first = new Random(10).nextInt(8) + 1,
                hashCodeV = UUID.randomUUID().toString().hashCode();
        return first + String.format("%015d", new Integer[]{Integer.valueOf(hashCodeV < 0 ? -hashCodeV : hashCodeV)});
    }

    public static Connection createConnection(String driver, String url, String username, String password) {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
}
