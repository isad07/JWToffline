package com.jwtoffline.core;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Builds and signs a new compact JWT from header and payload JSON. Everything runs locally. */
public class JwtEncoder {

    /**
     * @param headerJson  header JSON typed by the user (alg and typ are filled in automatically)
     * @param payloadJson payload JSON typed by the user
     * @param algorithm   signing algorithm
     * @param key         HMAC secret, or a PKCS#8 PEM private key for RSA
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
            if (algorithm.family() == JwtAlgorithm.Family.HMAC) {
                Mac mac = Mac.getInstance(algorithm.javaName());
                mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm.javaName()));
                return mac.doFinal(data);
            }
            Signature sig = Signature.getInstance(algorithm.javaName());
            sig.initSign(KeyParser.privateKey(key));
            sig.update(data);
            return sig.sign();
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Signing failed: " + ex.getMessage(), ex);
        }
    }
}
