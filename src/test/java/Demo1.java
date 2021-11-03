import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.DominoQuery;
import com.lwjhn.util.Replicator;
import com.lwjhn.util.StringTemplate;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: lwjhn
 * @Date: 2021-1-28
 * @Description: PACKAGE_NAME
 * @Version: 1.0
 */
public class Demo1 {
    @Test
    public void test12(){
        System.out.println("CN=OAAPP/OU=SRV/O=NANPING".replaceAll("(/[^/]*)|([^/]*=)",""));
    }

    @Test
    public void test1() throws Exception{
        DominoQuery dominoQuery = (DominoQuery) new DbConfig();
        DominoQuery dominoQuery3 = new DominoQuery();
        System.out.println(dominoQuery3.getVesion()==dominoQuery.getVesion());
        Field field = DominoQuery.class.getDeclaredField("vesion");
        field.setAccessible(true);
        System.out.println(field.get(dominoQuery));

        printObject(dominoQuery);
        System.out.println("\n\n---->\n");
        DominoQuery dominoQuery1 = (DominoQuery) dominoQuery.clone();
        //dominoQuery1.setVesion("2.1.1");
        field.set(dominoQuery1,"2.1.1");
        printObject(dominoQuery1);
        System.out.println("\n\n---->\n");
        printObject(dominoQuery);

        System.out.println(!DefaultConfig.PATTERN_NAME.matcher("PRINT_NUM").matches());
    }

    public void printObject(Object object) throws IllegalAccessException {
        Object val = null;
        Class c = object.getClass();
        while (c!=null){
            for(Field field : c.getDeclaredFields()){
                field.setAccessible(true);
                val=field.get(object);
                System.out.println(field.getName() + "--->" + field.getType() + "--->" + (val != null ? val : null));
            }
            c=c.getSuperclass();
        }
    }

    @Test
    public void testXML2HTML() throws Exception{
        System.out.println("你好".getBytes());

        System.out.println(StringTemplate.process("/EX_NPXC/arc.sql.output.${suffix}.json", new Replicator() {
            @Override
            public String replace(String key) throws Exception {
                return new SimpleDateFormat("yyyyMMddHHmmssSSSZ").format(new Date());
            }
        }));
        //System.out.println(new String(java.util.Base64.getDecoder().decode("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48ZG9jdW1lbnQ+PGV4Y2hhbmdlLz48Zm9ybT48aXRlbSBuYW1lPSJzdWJqZWN0IiB0eXBlPSJzdHJpbmciPjwhW0NEQVRBW+a1i+ivleW+geaxguaEj+ingeWKn+iDvTIwMjAwNzI4LUFdXT48L2l0ZW0+PGl0ZW0gbmFtZT0iY2F0ZWdvcnkiIHR5cGU9InN0cmluZyI+PCFbQ0RBVEFb5oSP6KeBXV0+PC9pdGVtPjxpdGVtIG5hbWU9InNlY3JldGxldmVsIiB0eXBlPSJzdHJpbmciPjwhW0NEQVRBW+aZrumAml1dPjwvaXRlbT48aXRlbSBuYW1lPSJ1cmdlbmN5bGV2ZWwiIHR5cGU9InN0cmluZyI+PCFbQ0RBVEFb5LiA6IisXV0+PC9pdGVtPjxpdGVtIG5hbWU9ImRpc3BhdGNodW5pdCIgdHlwZT0ic3RyaW5nIj48IVtDREFUQVvmtZnmsZ/ph5HmjqddXT48L2l0ZW0+PGl0ZW0gbmFtZT0icHVibGlzaGVyIiB0eXBlPSJzdHJpbmciPjwhW0NEQVRBW+WKnuWFrOWupOaWh+enmF1dPjwvaXRlbT48aXRlbSBuYW1lPSJwdWJsaXNodGltZSIgdHlwZT0iZGF0ZSI+PCFbQ0RBVEFbMjAyMC03LTI4IDE2OjE5OjQ3XV0+PC9pdGVtPjwvZm9ybT48Ym9keT48aXRlbSBuYW1lPSJhdHRhY2hmaWxlIiB0eXBlPSJzdHJpbmciPjwhW0NEQVRBWzk0Y2E3ZjBkNDBmMWY0ZjBkNjRlYmI1NzMwZWI4ZTE4LmRvY11dPjwvaXRlbT48L2JvZHk+PG9waW5pb25zLz48L2RvY3VtZW50Pg==")));
        //UtilXML.transformer(new File("D:/【工程管理文件字】/工程管理数据源.xml"),new File("D:/【工程管理文件字】/备案合同办理单.xsl"),new File("D:/【工程管理文件字】/test1.html"));
    }
}
