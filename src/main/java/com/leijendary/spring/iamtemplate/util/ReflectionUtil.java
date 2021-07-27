package com.leijendary.spring.iamtemplate.util;

public class ReflectionUtil {

    public static Object getFieldValue(final Object target, final String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        final var field = target.getClass().getField(fieldName);
        field.setAccessible(true);

        return field.get(target);
    }
}
