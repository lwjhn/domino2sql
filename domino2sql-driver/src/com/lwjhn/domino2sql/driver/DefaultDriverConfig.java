package com.lwjhn.domino2sql.driver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.function.UniConsumer;
import com.lwjhn.util.BeanFieldsIterator;

public class DefaultDriverConfig {
    public static final String ftppath_formula = "@ReplaceSubstring(@Subset(@DbName;-1); \"/\":\"\\\\\":\".\";\"_\")+\"/\"+@Text(@DocumentUniqueID)";

    public static void putFile(JSONObject response, String form, String name, String alias, String local) {
        JSONArray list = response.getJSONArray(form);
        if (list == null) {
            response.put(form, list = new JSONArray());
        }
        putFile(list, name, alias, local);
    }

    public static void putFile(JSONArray response, String name, String alias, String local) {
        response.add(new JSONObject() {{
            put("name", name);
            put("alias", alias);
            put("local", local);
        }});
    }

    public static <T> T parseExtendedOptions(JSONObject target, Class<T> clazz) {
        return parseExtendedOptions(target, clazz, null);
    }

    public static <T> T parseExtendedOptions(JSONObject target, Class<T> clazz, UniConsumer<String, Object> callback) {
        try {
            T response = clazz.newInstance();
            if (target != null) {
                BeanFieldsIterator.iterator(clazz, field -> {
                    if (target.containsKey(field.getName())) {
                        Object o = target.get(field.getName());
                        if (o instanceof JSONObject) {
                            o = ((JSONObject) o).toJavaObject(field.getType());
                        } else if (o instanceof JSONArray) {
                            o = ((JSONArray) o).toJavaObject(field.getType());
                        }
                        BeanFieldsIterator.setField(field, response, o);
                    }
                    if (callback != null) {
                        callback.accept(field.getName(), BeanFieldsIterator.getFieldValue(field, target));
                    }
                    return false;
                });
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
