package com.jwtoffline.core;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Reads RSA keys from PEM text (PKCS#8 private keys and X.509 / SPKI public keys). */
public final class KeyParser {

    private KeyParser() {
    }

    public static PublicKey publicKey(String pem) {
        try {
            byte[] der = stripPem(pem, "PUBLIC KEY");
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Could not read the RSA public key. Paste a PEM block that starts with -----BEGIN PUBLIC KEY-----", ex);
        }
    }

    public static PrivateKey privateKey(String pem) {
        try {
            byte[] der = stripPem(pem, "PRIVATE KEY");
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Could not read the RSA private key. Paste a PKCS#8 PEM block that starts with -----BEGIN PRIVATE KEY-----", ex);
        }
    }

    public static String toPem(byte[] der, String type) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(der);
        return "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----\n";
    }

    private static byte[] stripPem(String pem, String type) {
        if (pem == null || pem.isBlank()) {
            throw new JwtException("Key is empty");
        }
        if (pem.contains("RSA " + type)) {
            throw new JwtException("PKCS#1 keys (BEGIN RSA " + type + ") are not supported yet, convert to PKCS#8 / SPKI first");
        }
        String body = pem.replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(body);
    }
}
