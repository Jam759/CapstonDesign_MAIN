package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class DebugLogSanitizer {

    private static final int MAX_OBJECT_FIELD_COUNT = 8;

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password",
            "passwd",
            "secret",
            "token",
            "authorization",
            "cookie",
            "signature",
            "state",
            "credential",
            "key"
    );

    private final int maxStringLength;
    private final int maxCollectionSize;

    DebugLogSanitizer(LoggingProperties.DebugAspect properties) {
        this.maxStringLength = Math.max(16, properties.getMaxStringLength());
        this.maxCollectionSize = Math.max(1, properties.getMaxCollectionSize());
    }

    Map<String, Object> sanitizeArgs(String[] parameterNames, Object[] args) {
        Map<String, Object> sanitized = new LinkedHashMap<>();

        for (int i = 0; i < args.length; i++) {
            String parameterName = parameterNames != null && parameterNames.length > i
                    ? parameterNames[i]
                    : "arg" + i;

            sanitized.put(parameterName, sanitizeNamedValue(parameterName, args[i], 0));
        }

        return sanitized;
    }

    Object sanitizeReturnValue(Object value) {
        return sanitizeValue(value, 0);
    }

    String sanitizeMessage(String message) {
        return abbreviate(message);
    }

    private Object sanitizeNamedValue(String fieldName, Object value, int depth) {
        if (isSensitiveKey(fieldName)) {
            return "<redacted>";
        }
        return sanitizeValue(value, depth);
    }

    private Object sanitizeValue(Object value, int depth) {
        if (value == null) {
            return null;
        }

        if (value instanceof String stringValue) {
            return abbreviate(stringValue);
        }

        if (value instanceof Number
                || value instanceof Boolean
                || value instanceof UUID
                || value instanceof Enum<?>
                || value instanceof TemporalAccessor) {
            return value;
        }

        if (value instanceof Optional<?> optional) {
            return optional.map(v -> sanitizeValue(v, depth + 1)).orElse(null);
        }

        if (value instanceof byte[] bytes) {
            return "<byte[" + bytes.length + "]>";
        }

        Class<?> type = value.getClass();

        if (type.isArray()) {
            return sanitizeArray(value, depth + 1);
        }

        if (value instanceof Collection<?> collection) {
            return sanitizeCollection(collection, depth + 1);
        }

        if (value instanceof Map<?, ?> map) {
            return sanitizeMap(map, depth + 1);
        }

        if (depth >= 2) {
            return "<" + type.getSimpleName() + ">";
        }

        Package typePackage = type.getPackage();
        if (typePackage != null && typePackage.getName().startsWith("com.Hoseo.CapstoneDesign")) {
            return sanitizeObjectFields(value, type, depth + 1);
        }

        return abbreviate(String.valueOf(value));
    }

    private List<Object> sanitizeArray(Object value, int depth) {
        int length = Array.getLength(value);
        List<Object> sanitized = new ArrayList<>();
        int limit = Math.min(length, maxCollectionSize);

        for (int i = 0; i < limit; i++) {
            sanitized.add(sanitizeValue(Array.get(value, i), depth));
        }

        if (length > maxCollectionSize) {
            sanitized.add("<+" + (length - maxCollectionSize) + " more>");
        }

        return sanitized;
    }

    private List<Object> sanitizeCollection(Collection<?> collection, int depth) {
        List<Object> sanitized = new ArrayList<>();
        int index = 0;

        for (Object element : collection) {
            if (index >= maxCollectionSize) {
                sanitized.add("<+" + (collection.size() - maxCollectionSize) + " more>");
                break;
            }

            sanitized.add(sanitizeValue(element, depth));
            index++;
        }

        return sanitized;
    }

    private Map<String, Object> sanitizeMap(Map<?, ?> map, int depth) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        int index = 0;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (index >= maxCollectionSize) {
                sanitized.put("_truncated", "<+" + (map.size() - maxCollectionSize) + " more>");
                break;
            }

            String key = String.valueOf(entry.getKey());
            sanitized.put(key, sanitizeNamedValue(key, entry.getValue(), depth));
            index++;
        }

        return sanitized;
    }

    private Map<String, Object> sanitizeObjectFields(Object value, Class<?> type, int depth) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        sanitized.put("_type", type.getSimpleName());

        int fieldCount = 0;
        Class<?> current = type;

        while (current != null && current != Object.class && fieldCount < MAX_OBJECT_FIELD_COUNT) {
            for (Field field : current.getDeclaredFields()) {
                if (fieldCount >= MAX_OBJECT_FIELD_COUNT) {
                    break;
                }

                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                if (!field.trySetAccessible()) {
                    sanitized.put(field.getName(), "<inaccessible>");
                    fieldCount++;
                    continue;
                }

                try {
                    sanitized.put(field.getName(), sanitizeNamedValue(field.getName(), field.get(value), depth));
                } catch (IllegalAccessException ignored) {
                    sanitized.put(field.getName(), "<inaccessible>");
                }

                fieldCount++;
            }

            current = current.getSuperclass();
        }

        if (fieldCount >= MAX_OBJECT_FIELD_COUNT) {
            sanitized.put("_truncatedFields", "<max " + MAX_OBJECT_FIELD_COUNT + " fields>");
        }

        return sanitized;
    }

    private boolean isSensitiveKey(String fieldName) {
        String normalized = fieldName == null ? "" : fieldName.toLowerCase(Locale.ROOT);
        return SENSITIVE_KEYS.stream().anyMatch(normalized::contains);
    }

    private String abbreviate(String value) {
        if (value == null || value.length() <= maxStringLength) {
            return value;
        }
        return value.substring(0, maxStringLength) + "...(len=" + value.length() + ")";
    }
}
