package com.sunstriker.utils.json;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A very simple JSON serialize tool class.（array serialization not implemented）
 */
public class Json {
    /**
     *
     * @param obj Object to convert
     * @return JSON String
     */
    public static String Convert(Object obj) {
        try {
            return Convert(obj, "");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String Convert(Object obj, String indent) throws IllegalAccessException {
        if(obj == null) return "null";
        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive()) return String.valueOf(obj);
        if(clazz.equals(String.class)) return String.format("\"%s\"", obj);
        StringBuilder sb = new StringBuilder();
        if(clazz.isArray()) throw new UnsupportedOperationException();
        sb.append('{');
        var fields = Arrays.stream(obj.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(JsonProperty.class)).toArray();
        if (fields.length != 0) {
            sb.append('\n');
            String finalIndent = "  " + indent;
            for (int i = 0; i < fields.length; i++) {
                Field field = (Field) fields[i];
                field.setAccessible(true);
                sb.append(String.format("%s\"%s\": ", finalIndent, field.getName()));
                sb.append(Convert(field.get(obj), finalIndent));
                if(i!= fields.length-1) sb.append(',');
                sb.append('\n');
            }
        }
        sb.append(indent).append("}");
        return sb.toString();
    }
}
