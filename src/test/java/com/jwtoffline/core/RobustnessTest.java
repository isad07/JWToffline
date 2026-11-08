package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * NFR-5 and FR-4: nothing the user types can crash the app. The screens only catch JwtException,
 * so every bad input must come back as a JwtException (decoder, encoder) or an invalid result (verifier).
 */
class RobustnessTest {

    private static final String CHARS = "ab.-_=+/{}[]\":,0123456789eE \t\né中";

    private final JwtDecoder decoder = new JwtDecoder();
    private final JwtEncoder encoder = new JwtEncoder();
    private final SignatureVerifier verifier = new SignatureVerifier();

    private static String randomText(Random rnd) {
        StringBuilder sb = new StringBuilder();
        int len = rnd.nextInt(120);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /** A three part token whose header and payload are random text that is valid Base64URL. */
    private static String randomToken(Random rnd) {
        return Base64Url.encode(randomText(rnd)) + "." + Base64Url.encode(randomText(rnd)) + "." + randomText(rnd);
    }

    @Test
    void decoderOnlyThrowsJwtExceptionForRandomInput() {
        Random rnd = new Random(2107);
        for (int i = 0; i < 2000; i++) {
            String input = i % 2 == 0 ? randomText(rnd) : randomToken(rnd);
            try {
                decoder.decode(input);
            } catch (JwtException expected) {
                // fine: shown to the user as a message
            } catch (RuntimeException ex) {
                fail("decode crashed on input #" + i + " [" + input + "]: " + ex);
            }
        }
    }

    @Test
    void encoderOnlyThrowsJwtExceptionForRandomJson() {
        Random rnd = new Random(42);
        for (int i = 0; i < 1000; i++) {
            String header = randomText(rnd);
            String payload = randomText(rnd);
            try {
                encoder.encode(header, payload, JwtAlgorithm.HS256, "secret");
            } catch (JwtException expected) {
                // fine
            } catch (RuntimeException ex) {
                fail("encode crashed on header [" + header + "] payload [" + payload + "]: " + ex);
            }
        }
    }

    @Test
    void verifierNeverThrowsAndNeverPassesGarbage() {
        Random rnd = new Random(7);
        for (int i = 0; i < 1000; i++) {
            String token = randomToken(rnd);
            assertFalse(verifier.verify(token, randomText(rnd)).isValid(), token);
        }
    }

    /** Defect D-01: a number with a huge exponent made BigDecimal throw NumberFormatException past the UI. */
    @Test
    void hugeExponentIsReportedAsInvalidJson() {
        JwtException ex = assertThrows(JwtException.class, () -> Json.parse("{\"exp\":1e9999999999}"));
        assertFalse(ex.getMessage().isBlank());

        String token = Base64Url.encode("{\"alg\":\"HS256\"}") + "." + Base64Url.encode("{\"exp\":1e9999999999}") + ".sig";
        assertThrows(JwtException.class, () -> decoder.decode(token));
        assertFalse(verifier.verify(token, "secret").isValid());
    }
}
