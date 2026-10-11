package com.jwtoffline.core;

import com.auth0.jwt.algorithms.Algorithm;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/** Signing algorithms supported by JWTOffline, backed by Auth0 java-jwt. */
public enum JwtAlgorithm {
    HS256(Family.HMAC),
    HS384(Family.HMAC),
    HS512(Family.HMAC),
    RS256(Family.RSA),
    RS384(Family.RSA),
    RS512(Family.RSA);

    public enum Family { HMAC, RSA }

    private final Family family;

    JwtAlgorithm(Family family) {
        this.family = family;
    }

    public Family family() {
        return family;
    }

    Algorithm forSigning(String key) {
        return create(key, null, family == Family.RSA ? (RSAPrivateKey) KeyParser.privateKey(key) : null);
    }

    Algorithm forVerification(String key) {
        return create(key, family == Family.RSA ? (RSAPublicKey) KeyParser.publicKey(key) : null, null);
    }

    private Algorithm create(String key, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        return switch (this) {
            case HS256 -> Algorithm.HMAC256(key);
            case HS384 -> Algorithm.HMAC384(key);
            case HS512 -> Algorithm.HMAC512(key);
            case RS256 -> Algorithm.RSA256(publicKey, privateKey);
            case RS384 -> Algorithm.RSA384(publicKey, privateKey);
            case RS512 -> Algorithm.RSA512(publicKey, privateKey);
        };
    }

    public static JwtAlgorithm fromHeader(Object alg) {
        if (alg == null) {
            throw new JwtException("Header has no \"alg\" value");
        }
        for (JwtAlgorithm a : values()) {
            if (a.name().equals(alg.toString())) {
                return a;
            }
        }
        throw new JwtException("Unsupported algorithm: " + alg);
    }
}
