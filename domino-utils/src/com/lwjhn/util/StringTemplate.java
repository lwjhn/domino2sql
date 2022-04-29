package com.lwjhn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lwjhn
 * @Date: 2020-12-3
 * @Description: com.lwjhn.mht
 * @Version: 1.0
 */
public class StringTemplate {
    private static final Pattern pattern = Pattern.compile("\\$\\{\\w+}");

    public static StringBuffer process(CharSequence input, Replicator replicator) throws Exception {
        StringBuffer res = new StringBuffer();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(res, Matcher.quoteReplacement(replicator.replace(matcher.group().replaceAll("^\\$\\{|}$", ""))));
        }
        matcher.appendTail(res);
        return res;
    }
}
