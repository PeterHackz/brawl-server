package com.brawl.logic.utils;

public class LogicReflectionUtils {

    public static void set(Object object, String fieldName, String value, String type) throws Exception {
        fieldName = fieldName.trim();
        try {
            switch (type.toLowerCase()) {
                case "string" ->
                    object.getClass().getDeclaredMethod("set" + fieldName, String.class).invoke(object, value);
                case "integer", "int" -> object.getClass().getDeclaredMethod("set" + fieldName, int.class)
                        .invoke(object, value.isEmpty() ? 0 : Integer.parseInt(value));
                case "boolean", "bool" -> object.getClass().getDeclaredMethod("set" + fieldName, Boolean.class)
                        .invoke(object, !value.isEmpty() && Boolean.parseBoolean(value.toLowerCase()));
            }
        } catch (NoSuchMethodException ignored) {
        }
    }

}
