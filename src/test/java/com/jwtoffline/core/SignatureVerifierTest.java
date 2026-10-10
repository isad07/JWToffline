package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import org.junit.jupiter.api.Test;

/** US-3.1 to US-3.3 / FR-11 to FR-14 */
class SignatureVerifierTest {

    private final SignatureVerifier verifier = new SignatureVerifier();
    private final JwtEncoder encoder = new JwtEncoder();

    @Test
    void acceptsCorrectHmacSecret() {
        VerificationResult r = verifier.verify(JwtDecoderTest.JWT_IO, "your-256-bit-secret");
        assertTrue(r.isValid());
        assertEquals("Signature verified (HS256)", r.message());
    }

    @Test
    void rejectsWrongHmacSecret() {
        VerificationResult r = verifier.verify(JwtDecoderTest.JWT_IO, "wrong-secret");
        assertFalse(r.isValid());
        assertTrue(r.message().startsWith("Signature does not match"));
    }

    @Test
    void detectsTamperedPayload() {
        String[] parts = JwtDecoderTest.JWT_IO.split("\\.");
        String tampered = parts[0] + "." + Base64Url.encode("{\"sub\":\"1234567890\",\"name\":\"Admin\",\"iat\":1516239022}") + "." + parts[2];
        assertFalse(verifier.verify(tampered, "your-256-bit-secret").isValid());
    }

    @Test
    void roundTripsAllHmacAlgorithms() {
        for (JwtAlgorithm alg : new JwtAlgorithm[]{JwtAlgorithm.HS256, JwtAlgorithm.HS384, JwtAlgorithm.HS512}) {
            String token = encoder.encode("{}", "{\"n\":1}", alg, "key-" + alg);
            assertTrue(verifier.verify(token, "key-" + alg).isValid(), alg.name());
        }
    }

    @Test
    void roundTripsRsaWithPublicKey() throws Exception {
        KeyPair pair = JwtEncoderTest.rsaPair();
        String priv = KeyParser.toPem(pair.getPrivate().getEncoded(), "PRIVATE KEY");
        String pub = KeyParser.toPem(pair.getPublic().getEncoded(), "PUBLIC KEY");
        for (JwtAlgorithm alg : new JwtAlgorithm[]{JwtAlgorithm.RS256, JwtAlgorithm.RS384, JwtAlgorithm.RS512}) {
            String token = encoder.encode("{}", "{\"n\":1}", alg, priv);
            assertTrue(verifier.verify(token, pub).isValid(), alg.name());
        }
    }

    @Test
    void rejectsRsaTokenWithOtherPublicKey() throws Exception {
        String priv = KeyParser.toPem(JwtEncoderTest.rsaPair().getPrivate().getEncoded(), "PRIVATE KEY");
        String otherPub = KeyParser.toPem(JwtEncoderTest.rsaPair().getPublic().getEncoded(), "PUBLIC KEY");
        String token = encoder.encode("{}", "{}", JwtAlgorithm.RS256, priv);
        assertFalse(verifier.verify(token, otherPub).isValid());
    }

    @Test
    void refusesAlgNone() {
        String unsigned = Base64Url.encode("{\"alg\":\"none\"}") + "." + Base64Url.encode("{}") + ".";
        VerificationResult r = verifier.verify(unsigned, "anything");
        assertFalse(r.isValid());
        assertTrue(r.message().contains("none"));
    }

    @Test
    void explainsMissingKey() {
        VerificationResult r = verifier.verify(JwtDecoderTest.JWT_IO, "");
        assertFalse(r.isValid());
        assertEquals("Enter the secret that was used to sign this token", r.message());
    }

    @Test
    void reportsMalformedTokenInsteadOfThrowing() {
        VerificationResult r = verifier.verify("not-a-token", "k");
        assertFalse(r.isValid());
    }

    @Test
    void reportsUnsupportedAlgorithm() {
        String es = Base64Url.encode("{\"alg\":\"ES256\"}") + "." + Base64Url.encode("{}") + ".abc";
        assertEquals("Unsupported algorithm: ES256", verifier.verify(es, "k").message());
    }
}
