package com.lwjhn.domino2sql;

import com.alibaba.fastjson.JSON;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino2sql.config.ArcConfig;
import com.lwjhn.json.Loader;
import com.lwjhn.util.CloseableBase;
import com.lwjhn.util.FileOperator;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.priv.CORBA.iiop.Profile;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Executor {
    public static void run(String host, String user, String password, String path, String output, String[] args) {
        System.out.printf("java -DDominoHost=\"%s\" -DDominoUser=\"%s\" -DDominoPassword=\"%s\" -DDominoPath=\"%s\" -DDominoOutput=\"%s\" -jar ./domino2sql-app.jar\n",
                host, user, password, path, output);
        Session session = null;
        ArcConfig config = null;
        Action action = null;
        try {
            System.out.println("ArchXC Agent Start . ");
            System.out.println("config file path : " + new File(path).getCanonicalPath());
            Profile.PROXY_HOST = host.replaceAll(":\\d*$", "");
            String port = host.replaceAll(".*:", "").trim();
            Profile.PROXY_PORT = "".equals(port) ? 63148 : Integer.parseInt(port);
            System.out.printf("connect to host(%s) , user(%s), password(%s) \n", host, user, password);

            session = NotesFactory.createSession(host, args, user, password);
            System.out.println("connected ....");

            config = Loader.load(new File(path), ArcConfig.class);    //(new File(this.getClass().getResource("./arc.sql.config.json").getFile()));
            action = new Action(config, session, 1000);
            System.out.println("Authority : " + Domino2SqlHelp.join(session.evaluate("@UserName"), "; ", null));
            action.archive();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (config != null) {
                    FileOperator.newFile(output, JSON.toJSONString(config).getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CloseableBase.close(action);
            BaseUtils.recycle(session);
            System.out.println("ArchXC Agent ShutDown . ");
        }
    }
}
