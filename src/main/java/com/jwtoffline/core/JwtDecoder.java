package com.jwtoffline.core;

import java.util.Map;

/** Splits a compact JWT into its three parts and decodes the header and payload. No network, no verification. */
public class JwtDecoder {

    public DecodedJwt decode(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Paste a token to decode");
        }
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            trimmed = trimmed.substring(7).trim();
        }
        String[] parts = trimmed.split("\\.", -1);
        if (parts.length != 3) {
            throw new JwtException("A JWT has 3 parts separated by dots, this one has " + parts.length);
        }
        Map<String, Object> header = parsePart(parts[0], "header");
        Map<String, Object> payload = parsePart(parts[1], "payload");
        return new DecodedJwt(trimmed, parts[0], parts[1], parts[2], header, payload);
    }

    private Map<String, Object> parsePart(String segment, String name) {
        if (segment.isEmpty()) {
            throw new JwtException("The " + name + " segment is empty");
        }
        String json;
        try {
            json = Base64Url.decodeToString(segment);
        } catch (JwtException ex) {
            throw new JwtException("The " + name + " is not valid Base64URL", ex);
        }
        try {
            return Json.parseObject(json);
        } catch (JwtException ex) {
            throw new JwtException("The " + name + " is not valid JSON", ex);
        }
    }
}
