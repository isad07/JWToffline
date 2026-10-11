package com.jwtoffline.core;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Builds and signs a new compact JWT from header and payload JSON. */
public class JwtEncoder {

    /**
     * @param headerJson  header JSON typed by the user (alg and typ are filled in automatically)
     * @param payloadJson payload JSON typed by the user
     * @param algorithm   signing algorithm
     * @param key         HMAC secret, or an unencrypted PKCS#1 / PKCS#8 PEM private key for RSA
     */
    public String encode(String headerJson, String payloadJson, JwtAlgorithm algorithm, String key) {
        Map<String, Object> header = headerJson == null || headerJson.isBlank()
                ? new LinkedHashMap<>() : new LinkedHashMap<>(Json.parseObject(headerJson));
        Map<String, Object> payload = Json.parseObject(payloadJson);
        header.put("alg", algorithm.name());
        header.putIfAbsent("typ", "JWT");

        String signingInput = Base64Url.encode(Json.compact(header)) + "." + Base64Url.encode(Json.compact(payload));
        byte[] signature = sign(signingInput, algorithm, key);
        return signingInput + "." + Base64Url.encode(signature);
    }

    static byte[] sign(String signingInput, JwtAlgorithm algorithm, String key) {
        if (key == null || key.isEmpty()) {
            throw new JwtException(algorithm.family() == JwtAlgorithm.Family.HMAC
                    ? "Enter a secret to sign with" : "Paste an RSA private key to sign with");
        }
        byte[] data = signingInput.getBytes(StandardCharsets.US_ASCII);
        try {
            return algorithm.forSigning(key).sign(data);
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Signing failed: " + ex.getMessage(), ex);
        }
    }
}
