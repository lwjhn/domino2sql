package com.lwjhn.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public interface BeanFieldsIterator {
    /**
     * @return Whether or not to break : false-continue, true-break
     */
    boolean resolve(Field field);

    static void iterator(Class<?> clazz, BeanFieldsIterator iterator) {
        do {
            for (Field field : clazz.getDeclaredFields())
                if (iterator.resolve(field))
                    return;
        } while ((clazz = clazz.getSuperclass()) != null);
    }

    static void iteratorThis(Class<?> clazz, BeanFieldsIterator iterator) {
        for (Field field : clazz.getDeclaredFields())
            if (iterator.resolve(field))
                return;
    }

    @SuppressWarnings("unused")
    static void iteratorUnique(Class<?> clazz, BeanFieldsIterator iterator) {
        Set<String> exists = new HashSet<>();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (exists.add(field.getName()) && iterator.resolve(field))
                    return;
            }
        } while ((clazz = clazz.getSuperclass()) != null);
    }

    @SuppressWarnings("unused")
    static Field getField(String key, Class<?> clazz) {
        Field field = null;
        do {
            try {
                field = clazz.getDeclaredField(key);
                break;
            } catch (NoSuchFieldException ignored) {

            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return field;
    }

    static Object getFieldValue(String key, Object target) {
        if(target==null)
            return null;
        return getFieldValue(getField(key, target.getClass()), target);
    }

    static Object getFieldValue(Field field, Object target) {
        try {
            if(field==null){
                throw new RuntimeException("java.lang.NoSuchFieldException ! ");
            }
            field.setAccessible(true);
            return field.get(target);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static void setField(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static void setField(String property, Object target, Object value) {
        setField(getField(property, target.getClass()), target, value);
    }

    /**
     * @return subclass is sub class of superclass
     * try {
     * subclass.asSubclass(superclass);
     * return true;
     * } catch (ClassCastException e) {
     * return false;
     * }
     */
    static boolean isSubclass(Class<?> subclass, Class<?> superclass) {
        return subclass != null && superclass.isAssignableFrom(subclass);
    }
}