package com.lwjhn.domino2sql;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        JarLoader.load(System.getProperty("user.dir") + "/lib");

        //java -Dsun.jnu.encoding=UTF-8 -DDominoHost="192.168.211.53:63148" -DDominoUser="Admin" -DDominoPassword="12345678" -DDominoPath="./arc.sql.config.sft.json" -DDominoOutput="./arc.sql.config.output.json" -jar ./domino2sql-app.jar
        String host, user, password, path, output;
        if ((host = System.getProperty("DominoHost")) == null || "".equals(host.trim())) {
            throw new RuntimeException("domino host is null !");
        }
        if ((user = System.getProperty("DominoUser")) == null) {
            throw new RuntimeException("domino user is null !");
        }
        if ((password = System.getProperty("DominoPassword")) == null) {
            throw new RuntimeException("domino password is null !");
        }
        if ((path = System.getProperty("DominoPath")) == null || "".equals(path.trim())) {
            path = "./arc.sql.config.json";
        }
        if ((output = System.getProperty("DominoOutput")) == null || "".equals(output.trim())) {
            output = "./arc.sql.config.output.json";
        }
        Class.forName("com.lwjhn.domino2sql.Executor")
                .getMethod("run", String.class, String.class, String.class, String.class, String.class, String[].class)
                .invoke(null, host, user, password, path, output, args);
        Scanner scan = new Scanner(System.in);
        System.out.println("press any character to shut down .");
        boolean flag = scan.hasNext();
        scan.close();
    }
}
