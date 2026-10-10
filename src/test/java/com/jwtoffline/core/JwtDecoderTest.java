package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** US-1.1, US-1.2, US-1.4 / FR-1 to FR-4 */
class JwtDecoderTest {

    // Well known example token from the JWT spec docs, secret "your-256-bit-secret"
    static final String JWT_IO =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    private final JwtDecoder decoder = new JwtDecoder();

    @Test
    void decodesHeaderAndPayload() {
        DecodedJwt jwt = decoder.decode(JWT_IO);
        assertEquals("HS256", jwt.header().get("alg"));
        assertEquals("JWT", jwt.header().get("typ"));
        assertEquals("1234567890", jwt.payload().get("sub"));
        assertEquals("John Doe", jwt.payload().get("name"));
        assertEquals(1516239022L, jwt.payload().get("iat"));
        assertEquals("SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c", jwt.signatureSegment());
    }

    @Test
    void prettyPrintsJson() {
        DecodedJwt jwt = decoder.decode(JWT_IO);
        assertEquals("{\n  \"alg\": \"HS256\",\n  \"typ\": \"JWT\"\n}", jwt.prettyHeader());
        assertTrue(jwt.prettyPayload().contains("\"name\": \"John Doe\""));
    }

    @Test
    void acceptsBearerPrefixAndWhitespace() {
        DecodedJwt jwt = decoder.decode("  Bearer " + JWT_IO + "\n");
        assertEquals(JWT_IO, jwt.rawToken());
    }

    @Test
    void rejectsEmptyInput() {
        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("   "));
        assertEquals("Paste a token to decode", ex.getMessage());
    }

    @Test
    void rejectsWrongNumberOfParts() {
        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("abc.def"));
        assertTrue(ex.getMessage().contains("3 parts"));
    }

    @Test
    void rejectsBadBase64() {
        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("@@@.e30.sig"));
        assertTrue(ex.getMessage().contains("header is not valid Base64URL"));
    }

    @Test
    void rejectsPayloadThatIsNotJson() {
        String notJson = Base64Url.encode("hello");
        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("e30." + notJson + ".sig"));
        assertTrue(ex.getMessage().contains("payload is not valid JSON"));
    }

    @Test
    void allowsUnsignedTokenWithEmptySignature() {
        String unsigned = Base64Url.encode("{\"alg\":\"none\"}") + "." + Base64Url.encode("{\"a\":1}") + ".";
        DecodedJwt jwt = decoder.decode(unsigned);
        assertEquals("none", jwt.algorithmName());
        assertEquals("", jwt.signatureSegment());
    }
}
