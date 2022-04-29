package com.lwjhn.domino2sql;

import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import lotus.domino.DateTime;
import lotus.domino.Item;
import lotus.domino.RichTextItem;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Vector;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.lwjhn.domino2sql
 * @Version: 1.0
 */
public class Domino2SqlHelp {
    public static boolean isReturnAllValues(JDBCType jdbcType) {
        return !(jdbcType == JDBCType.INTEGER || jdbcType == JDBCType.BIGINT
                || jdbcType == JDBCType.TINYINT || jdbcType == JDBCType.SMALLINT
                || jdbcType == JDBCType.NUMERIC || jdbcType == JDBCType.FLOAT
                || jdbcType == JDBCType.DOUBLE || jdbcType == JDBCType.DECIMAL
                || jdbcType == JDBCType.BIT || jdbcType == JDBCType.BOOLEAN
                || jdbcType == JDBCType.TIME || jdbcType == JDBCType.DATE
                || jdbcType == JDBCType.TIMESTAMP || jdbcType == JDBCType.TIME_WITH_TIMEZONE || jdbcType == JDBCType.TIMESTAMP_WITH_TIMEZONE);
    }

    public static Object domino2JdbcType(Vector values, ItemConfig itemConfig) throws Exception {
        if (values == null || values.size() < 1) return null;
        JDBCType jdbcType = itemConfig.getJdbc_type();
        Object parameterObj = values.get(0);
        try {
            if (parameterObj instanceof String || parameterObj instanceof Double) {
                return isReturnAllValues(jdbcType)
                        ? join(values, itemConfig.getJoin_delimiter(), null)
                        : (parameterObj == null || parameterObj.toString().length() == 0 ? null : parameterObj);
            } else if (parameterObj instanceof lotus.domino.DateTime) {
                return parameterObj.toString() == null || parameterObj.toString().length() == 0 ? null
                        : (isReturnAllValues(jdbcType)
                        ? join(values, itemConfig.getJoin_delimiter(), itemConfig.getDate_format())
                        : new Timestamp(((lotus.domino.DateTime) parameterObj).toJavaDate().getTime())
                );
            } else {
                throw new Exception("domino2JdbcType:: this domino item's value is abnormality ! " + parameterObj.getClass().getName());
            }
        } catch (Exception e) {
            throw itemConfig != null && itemConfig.getSql_name() != null
                    ? new Exception("at sql_name of " + itemConfig.getSql_name() + System.lineSeparator() + e.getMessage(), e.getCause())
                    : e;
        }
    }

    public static Object domino2JdbcType(Item item, ItemConfig itemConfig) throws Exception {
        if (item == null) return null;
        switch (item.getType()) {
            case Item.RICHTEXT:
                return ((RichTextItem) item).getUnformattedText();
            default:
                return domino2JdbcType(item.getValues(), itemConfig);
        }
    }

    public static String join(Iterable elements, CharSequence delimiter, SimpleDateFormat dateFormat) throws Exception {
        Objects.requireNonNull(elements);
        /*Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);*/
        StringJoiner joiner = new StringJoiner(delimiter == null ? DefaultConfig.String_Join_Delimiter : delimiter);
        for (Object cs : elements) {
            if (cs == null) {
                joiner.add("");
            } else if (cs instanceof lotus.domino.DateTime) {
                joiner.add(cs.toString() == null || cs.toString().length() == 0
                        ? ""
                        : (dateFormat == null ? DefaultConfig.DateFormat : dateFormat).format(((lotus.domino.DateTime) cs).toJavaDate())
                );
            } else {
                joiner.add(cs.toString());
            }
        }
        return joiner.toString();
    }

}
