package com.kirayim.jark.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class BeanUtils {

    public static Field getField(Object obj, String name) {
        for (Class<?> clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException | SecurityException e) {
                // Ignore...
            }

            if (field != null) {
                return field;
            }
        }

        return null;
    }


    //===================================================================

    /**
     * See if an annotation is present on a field.
     * @param obj
     * @param fieldName
     * @param annotation
     * @return
     */
    public static boolean isAnnotationPresent(Object obj, String fieldName, Class<? extends Annotation> annotation) {
        Field field = getField(obj, fieldName);

        if (field != null) {
            return field.isAnnotationPresent(annotation);
        }

        return false;
    }

    public static boolean isAnnotationPresent(Class obj, String fieldName, Class<? extends Annotation> annotation) {
        Field field = getField(obj, fieldName);

        if (field != null) {
            return field.isAnnotationPresent(annotation);
        }

        return false;
    }

}
