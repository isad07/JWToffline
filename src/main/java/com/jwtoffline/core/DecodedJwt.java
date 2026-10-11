package com.jwtoffline.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Result of splitting and decoding a token. Immutable. */
public record DecodedJwt(String rawToken, String signatureSegment,
                         Map<String, Object> header, Map<String, Object> payload) {

    public DecodedJwt {
        header = immutableMap(header);
        payload = immutableMap(payload);
    }

    public String algorithmName() {
        Object alg = header.get("alg");
        return alg == null ? "(missing)" : alg.toString();
    }

    private static Map<String, Object> immutableMap(Map<?, ?> values) {
        Map<String, Object> copy = new LinkedHashMap<>();
        values.forEach((key, value) -> copy.put((String) key, immutableValue(value)));
        return Collections.unmodifiableMap(copy);
    }

    private static Object immutableValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            return immutableMap(map);
        }
        if (value instanceof List<?> list) {
            return list.stream().map(DecodedJwt::immutableValue).toList();
        }
        return value;
    }

    public String prettyHeader() { return Json.pretty(header); }
    public String prettyPayload() { return Json.pretty(payload); }
}
