import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.util.AutoCloseableBase;
import com.lwjhn.util.UtilXML;
import com.rjsoft.archive.FormatFlow;
import com.rjsoft.archive.RJUitilDSXml;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * @Author: lwjhn
 * @Date: 2021-4-14
 * @Description: PACKAGE_NAME
 * @Version: 1.0
 */
public class testFormatFlow {
    @Test
    public void test3() throws Exception {
        UtilXML.transformer(new File("c:/temp/test/政府发文稿纸.xsl"),new File("c:/temp/test/发文数据源.xml"),new File("c:/temp/test/out.html"));
    }

    @Test
    public void test1() throws Exception{
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("./file/flowC6FD3EB05411B800482583A90003B7F8.json"));    //("./file/flowF87111FC5E74057B482583D900095629.json");
            JSONArray res = JSONArray.parseObject(inputStream, JSONArray.class);
            JSONObject unit;
            System.out.println(com.rjsoft.archive.FormatFlow.formatFlowJson(res));

            int i = 0;
            for (Object o : com.rjsoft.archive.FormatFlow.formatFlowJson(res)) {
                unit = (JSONObject) o;
                System.out.println(String.format("%d: %s（环节） %s %s至 %s（环节）；接收人员：%s ; 开始时间：%s; 办理时间：%s ;",
                        ++i,
                        FormatFlow.getItemFirstValue(unit, "unitname"),
                        FormatFlow.getItemFirstValue(unit, "unituser"),
                        FormatFlow.getItemFirstValue(unit, "unitaction"),
                        FormatFlow.getItemFirstValue(unit, "unitnameto"),
                        FormatFlow.getItemFirstValue(unit, "unituserto"),
                        DefaultConfig.DateFormat.format(DefaultConfig.DateFormat.parse(FormatFlow.getItemFirstValue(unit, "unittime"))),
                        FormatFlow.getItemFirstValue(unit,"unitstarttime")
                ));
                //System.out.println(unit.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AutoCloseableBase.close(inputStream);
        }
    }

    @Test
    public void test2(){
        int i=0;
        while (i<15){
            try {
                if(i==4){
                    continue;
                }
                System.out.print(i + ", next: ");
            }catch (Exception e){

            }finally {
                System.out.println(++i);
            }
        }
        //List<String> lists = Arrays.asList("A", "B", "C", "D");
        Vector<String> lists= new Vector<>(Arrays.asList("A", "B", "C", "D", "B"));

        lists=new Vector<>(lists.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return true;
            }
        }).collect(Collectors.toCollection(new Supplier<Collection<String>>() {
            @Override
            public Collection<String> get() {
                return new Vector();
            }
        })));
        System.out.println(lists.indexOf("B"));
        Iterator<String> iterator = lists.stream().iterator();
        String temp;
        while (iterator.hasNext()) {
            System.out.println(temp=iterator.next());
        }
    }
}
