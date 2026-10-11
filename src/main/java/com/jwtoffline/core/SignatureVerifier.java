package com.jwtoffline.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;

/** Checks a token signature with an HMAC secret or an RSA public key. */
public class SignatureVerifier {

    private final JwtDecoder decoder = new JwtDecoder();

    public VerificationResult verify(String token, String key) {
        try {
            DecodedJwt jwt = decoder.decode(token);
            if ("none".equalsIgnoreCase(jwt.algorithmName())) {
                return VerificationResult.invalid("Token uses alg \"none\" and has no signature to check");
            }
            JwtAlgorithm alg = JwtAlgorithm.fromHeader(jwt.header().get("alg"));
            if (key == null || key.isEmpty()) {
                return VerificationResult.invalid(alg.family() == JwtAlgorithm.Family.HMAC
                        ? "Enter the secret that was used to sign this token" : "Paste the RSA public key for this token");
            }
            // Check only the signature; time claims are inspected separately.
            alg.forVerification(key).verify(JWT.decode(jwt.rawToken()));
            return VerificationResult.valid(alg);
        } catch (SignatureVerificationException ex) {
            return VerificationResult.invalid("Signature does not match. The key is wrong or the token was changed");
        } catch (JwtException ex) {
            return VerificationResult.invalid(ex.getMessage());
        } catch (Exception ex) {
            return VerificationResult.invalid("Could not verify: " + ex.getMessage());
        }
    }
}
