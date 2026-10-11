package com.jwtoffline.core;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/** Reads RSA keys from PKCS#1, PKCS#8 and SPKI PEM blocks. */
public final class KeyParser {
    private KeyParser() {
    }

    public static PublicKey publicKey(String pem) {
        try {
            PublicKey key = new JcaPEMKeyConverter().getPublicKey((SubjectPublicKeyInfo) read(pem));
            if (key instanceof RSAPublicKey) {
                return key;
            }
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Could not read the RSA public key. Paste a PUBLIC KEY or RSA PUBLIC KEY PEM block", ex);
        }
        throw new JwtException("The public key must be an RSA key");
    }

    public static PrivateKey privateKey(String pem) {
        try {
            Object parsed = read(pem);
            PrivateKeyInfo info = parsed instanceof PEMKeyPair pair
                    ? pair.getPrivateKeyInfo() : (PrivateKeyInfo) parsed;
            PrivateKey key = new JcaPEMKeyConverter().getPrivateKey(info);
            if (key instanceof RSAPrivateKey) {
                return key;
            }
        } catch (JwtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtException("Could not read the RSA private key. Paste an unencrypted PRIVATE KEY or RSA PRIVATE KEY PEM block", ex);
        }
        throw new JwtException("The private key must be an RSA key");
    }

    private static Object read(String pem) throws Exception {
        if (pem == null || pem.isBlank()) {
            throw new JwtException("Key is empty");
        }
        try (PEMParser parser = new PEMParser(new StringReader(pem))) {
            Object key = parser.readObject();
            if (key == null) {
                throw new JwtException("No PEM key found");
            }
            return key;
        }
    }
}
