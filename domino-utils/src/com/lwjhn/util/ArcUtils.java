package com.lwjhn.util;

import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lwjhn
 * @Date: 2020-11-23
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class ArcUtils {
    public static String UUID32() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String UUID8() {
        return String.format("%08X", UUID.randomUUID().hashCode());
    }

    public static String UUID16() {
        return UUID8() + UUID8();
    }

    public static final Pattern PATTERN_UUID = Pattern.compile("@UUID\\d+\\b", Pattern.CASE_INSENSITIVE);

    public static String formula(String expression) {
        return formula(expression, PATTERN_UUID, s -> ArcUtils.UUID(Integer.parseInt(s.substring(5))));
    }

    public static String formula(String expression, Pattern pattern, Function<String, String> replace) {
        StringBuffer res = new StringBuffer();
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find())
            matcher.appendReplacement(res,
                    Matcher.quoteReplacement(replace.apply(matcher.group()))
            );
        matcher.appendTail(res);
        return res.toString();
    }

    public static String UUID(int type) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < Math.floorDiv(Math.abs(type), 8); i++) {
            res.append(UUID8());
        }
        return res.toString();
    }
}
