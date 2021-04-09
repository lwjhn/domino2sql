import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.DominoQuery;
import org.junit.Test;

import java.io.FileReader;
import java.lang.reflect.Field;

/**
 * @Author: lwjhn
 * @Date: 2021-1-28
 * @Description: PACKAGE_NAME
 * @Version: 1.0
 */
public class Demo1 {
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
}
