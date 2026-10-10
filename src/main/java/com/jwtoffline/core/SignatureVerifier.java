package com.jwtoffline.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;

/** Checks a token signature with an HMAC secret or an RSA public key. */
public class SignatureVerifier {

    private final JwtDecoder decoder = new JwtDecoder();

    public VerificationResult verify(String token, String key) {
        DecodedJwt jwt;
        try {
            jwt = decoder.decode(token);
        } catch (JwtException ex) {
            return VerificationResult.invalid(ex.getMessage());
        }
        if ("none".equalsIgnoreCase(jwt.algorithmName())) {
            return VerificationResult.invalid("Token uses alg \"none\" and has no signature to check");
        }
        JwtAlgorithm alg;
        try {
            alg = JwtAlgorithm.fromHeader(jwt.header().get("alg"));
        } catch (JwtException ex) {
            return VerificationResult.invalid(ex.getMessage());
        }
        if (key == null || key.isEmpty()) {
            return VerificationResult.invalid(alg.family() == JwtAlgorithm.Family.HMAC
                    ? "Enter the secret that was used to sign this token" : "Paste the RSA public key for this token");
        }
        try {
            byte[] provided = Base64Url.decode(jwt.signatureSegment());
            boolean ok;
            if (alg.family() == JwtAlgorithm.Family.HMAC) {
                byte[] expected = JwtEncoder.sign(jwt.signingInput(), alg, key);
                ok = MessageDigest.isEqual(expected, provided); // constant time compare
            } else {
                Signature sig = Signature.getInstance(alg.javaName());
                sig.initVerify(KeyParser.publicKey(key));
                sig.update(jwt.signingInput().getBytes(StandardCharsets.US_ASCII));
                ok = sig.verify(provided);
            }
            return ok ? VerificationResult.valid(alg)
                      : VerificationResult.invalid("Signature does not match. The key is wrong or the token was changed");
        } catch (JwtException ex) {
            return VerificationResult.invalid(ex.getMessage());
        } catch (Exception ex) {
            return VerificationResult.invalid("Could not verify: " + ex.getMessage());
        }
    }
}
