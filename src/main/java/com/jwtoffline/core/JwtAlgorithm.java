package com.jwtoffline.core;

/** Signing algorithms supported by JWTOffline, mapped to their Java (JCA) names. */
public enum JwtAlgorithm {
    HS256("HmacSHA256", Family.HMAC),
    HS384("HmacSHA384", Family.HMAC),
    HS512("HmacSHA512", Family.HMAC),
    RS256("SHA256withRSA", Family.RSA),
    RS384("SHA384withRSA", Family.RSA),
    RS512("SHA512withRSA", Family.RSA);

    public enum Family { HMAC, RSA }

    private final String javaName;
    private final Family family;

    JwtAlgorithm(String javaName, Family family) {
        this.javaName = javaName;
        this.family = family;
    }

    public String javaName() {
        return javaName;
    }

    public Family family() {
        return family;
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
