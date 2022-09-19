package com.sunstriker.utils.json;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A very primitive JSON serialize tool class since I'm not sure if Gson/Jackson is allowed.
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

    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<>(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

    private static String convert(Object obj, String indent) throws IllegalAccessException {
        if(obj == null) return "null";
        Class<?> clazz = obj.getClass();
        if(clazz.isPrimitive() || WRAPPER_TYPES.contains(clazz)) return String.valueOf(obj);
        if(clazz.equals(String.class)) return String.format("\"%s\"", obj);
        StringBuilder sb = new StringBuilder();
        // hard code string array process...
        if(clazz.equals(String[].class)){
            sb.append('[');
            for (String s : ((String[]) obj)) {
                sb.append('"').append(s).append('"').append(", ");
            }
            sb.deleteCharAt(sb.length()-1).deleteCharAt(sb.length()-1);
            sb.append(']');
            return sb.toString();
        }
        sb.append('{');
        var fields = Arrays.stream(obj.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(JsonProperty.class)).toArray();
        if (fields.length != 0) {
            sb.append('\n');
            String finalIndent = "  " + indent;
            for (int i = 0; i < fields.length; i++) {
                Field field = (Field) fields[i];
                field.setAccessible(true);
                sb.append(String.format("%s\"%s\": ", finalIndent, field.getName()));
                if(field.getType().isPrimitive() || WRAPPER_TYPES.contains(field.getType())) sb.append(field.get(obj));
                else sb.append(convert(field.get(obj), finalIndent));
                if(i!= fields.length-1) sb.append(',');
                sb.append('\n');
            }
        }
        sb.append(indent).append("}");
        return sb.toString();
    }
}
