package com.sunstriker.utils.json;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A very simple JSON serialize tool class.（array serialization not implemented）
 */
public class JsonUtils {
    /**
     *
     * @param obj Object to convert
     * @return JSON String
     */
    public static String convert(Object obj) {
        try {
            return convert(obj, "");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convert(Object obj, String indent) throws IllegalAccessException {
        if(obj == null) return "null";
        Class<?> clazz = obj.getClass();
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
                if(field.getType().isPrimitive()) sb.append(field.get(obj));
                else sb.append(convert(field.get(obj), finalIndent));
                if(i!= fields.length-1) sb.append(',');
                sb.append('\n');
            }
        }
        sb.append(indent).append("}");
        return sb.toString();
    }
}
