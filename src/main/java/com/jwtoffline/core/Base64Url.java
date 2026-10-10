package com.jwtoffline.core;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Base64URL helpers (RFC 4648 section 5, no padding) used by every JWT part. */
public final class Base64Url {

    private Base64Url() {
    }

    public static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String encode(String text) {
        return encode(text.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decode(String value) {
        try {
            return Base64.getUrlDecoder().decode(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new JwtException("Segment is not valid Base64URL", ex);
        }
    }

    public static String decodeToString(String value) {
        return new String(decode(value), StandardCharsets.UTF_8);
    }
}
