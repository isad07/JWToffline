package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.junit.jupiter.api.Test;

/** US-2.1 to US-2.3 / FR-7 to FR-10 */
class JwtEncoderTest {

    private final JwtEncoder encoder = new JwtEncoder();
    private final JwtDecoder decoder = new JwtDecoder();

    @Test
    void reproducesKnownHs256Token() {
        String token = encoder.encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}",
                "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"iat\":1516239022}",
                JwtAlgorithm.HS256, "your-256-bit-secret");
        assertEquals(JwtDecoderTest.JWT_IO, token);
    }

    @Test
    void setsAlgAndTypAutomatically() {
        String token = encoder.encode("", "{\"sub\":\"a\"}", JwtAlgorithm.HS384, "s3cret");
        DecodedJwt jwt = decoder.decode(token);
        assertEquals("HS384", jwt.header().get("alg"));
        assertEquals("JWT", jwt.header().get("typ"));
    }

    @Test
    void headerAlgIsOverriddenBySelectedAlgorithm() {
        String token = encoder.encode("{\"alg\":\"none\",\"kid\":\"k1\"}", "{}", JwtAlgorithm.HS512, "s3cret");
        DecodedJwt jwt = decoder.decode(token);
        assertEquals("HS512", jwt.header().get("alg"));
        assertEquals("k1", jwt.header().get("kid"));
    }

    @Test
    void signsWithRsaPrivateKey() throws Exception {
        KeyPair pair = rsaPair();
        String privatePem = KeyParser.toPem(pair.getPrivate().getEncoded(), "PRIVATE KEY");
        String token = encoder.encode("{}", "{\"role\":\"admin\"}", JwtAlgorithm.RS256, privatePem);
        assertEquals("RS256", decoder.decode(token).header().get("alg"));
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void rejectsInvalidPayloadJson() {
        JwtException ex = assertThrows(JwtException.class,
                () -> encoder.encode("{}", "{\"sub\": }", JwtAlgorithm.HS256, "k"));
        assertTrue(ex.getMessage().startsWith("Invalid JSON"));
    }

    @Test
    void rejectsMissingKey() {
        JwtException ex = assertThrows(JwtException.class,
                () -> encoder.encode("{}", "{}", JwtAlgorithm.HS256, ""));
        assertEquals("Enter a secret to sign with", ex.getMessage());
    }

    @Test
    void rejectsGarbageRsaKey() {
        assertThrows(JwtException.class,
                () -> encoder.encode("{}", "{}", JwtAlgorithm.RS256, "not a key"));
    }

    static KeyPair rsaPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }
}
