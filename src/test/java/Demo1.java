import com.lwjhn.domino2sql.config.DefaultConfig;
import org.junit.Test;

import java.io.FileReader;

/**
 * @Author: lwjhn
 * @Date: 2021-1-28
 * @Description: PACKAGE_NAME
 * @Version: 1.0
 */
public class Demo1 {
    @Test
    public void test1() throws Exception{
        System.out.println(!DefaultConfig.PATTERN_NAME.matcher("PRINT_NUM").matches());
    }
}
