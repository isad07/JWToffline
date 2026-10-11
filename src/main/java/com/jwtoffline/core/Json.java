package com.jwtoffline.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import java.math.BigDecimal;
import java.util.Map;

/** JSON parsing and formatting without rounding numeric claims. */
public final class Json {
    private static final Gson COMPACT = builder().create();
    private static final Gson PRETTY = builder().setPrettyPrinting().create();

    private Json() {
    }

    private static GsonBuilder builder() {
        return new GsonBuilder().setStrictness(Strictness.STRICT)
                .setObjectToNumberStrategy(reader -> {
                    String value = reader.nextString();
                    try {
                        return Long.valueOf(value);
                    } catch (NumberFormatException ex) {
                        return new BigDecimal(value);
                    }
                })
                .serializeNulls().disableHtmlEscaping();
    }

    public static Object parse(String text) {
        if (text == null || text.isBlank()) {
            throw new JwtException("JSON input is empty");
        }
        try {
            return COMPACT.fromJson(text, Object.class);
        } catch (JsonParseException ex) {
            throw new JwtException("Invalid JSON: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String text) {
        if (!(parse(text) instanceof Map<?, ?> value)) {
            throw new JwtException("Expected a JSON object");
        }
        return (Map<String, Object>) value;
    }

    public static String pretty(Object value) {
        return PRETTY.toJson(value);
    }

    public static String compact(Object value) {
        return COMPACT.toJson(value);
    }
}
