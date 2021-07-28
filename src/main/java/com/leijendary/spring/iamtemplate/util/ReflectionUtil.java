package com.leijendary.spring.iamtemplate.util;

public class ReflectionUtil {

    public static Object getFieldValue(final Object target, final String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        final var cls = target.getClass();
        final var field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(target);
    }
}
