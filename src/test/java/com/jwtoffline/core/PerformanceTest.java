package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** NFR-2: decoding or verifying a token up to 8 KB finishes in under 100 ms. */
class PerformanceTest {

    private static final long LIMIT_MS = 100;
    private static final String SECRET = "performance-test-secret";

    private static String hsToken;
    private static String rsToken;
    private static String rsPublicPem;

    private final JwtDecoder decoder = new JwtDecoder();
    private final SignatureVerifier verifier = new SignatureVerifier();

    @BeforeAll
    static void buildLargeTokens() throws Exception {
        // About 5,900 characters of claims, which encodes to a token of roughly 8 KB.
        String payload = "{\"sub\":\"perf-user\",\"iat\":1767225600,\"exp\":1924992000,\"blob\":\"" + "x".repeat(5900) + "\"}";
        JwtEncoder encoder = new JwtEncoder();
        hsToken = encoder.encode("{}", payload, JwtAlgorithm.HS256, SECRET);
        KeyPair pair = TestKeys.rsaPair();
        rsToken = encoder.encode("{}", payload, JwtAlgorithm.RS256,
                TestKeys.toPem(pair.getPrivate().getEncoded(), "PRIVATE KEY"));
        rsPublicPem = TestKeys.toPem(pair.getPublic().getEncoded(), "PUBLIC KEY");
        assertTrue(hsToken.length() >= 7800 && hsToken.length() <= 8192, "token is about 8 KB: " + hsToken.length());
    }

    /**
     * Runs the action a few times to warm up the JVM, then times 21 runs and returns the median in ms.
     * The median keeps one garbage collection pause on a busy CI machine from failing the build.
     */
    private static long medianRunMs(Supplier<?> action) {
        for (int i = 0; i < 5; i++) {
            action.get();
        }
        long[] runs = new long[21];
        for (int i = 0; i < runs.length; i++) {
            long start = System.nanoTime();
            action.get();
            runs[i] = (System.nanoTime() - start) / 1_000_000;
        }
        java.util.Arrays.sort(runs);
        return runs[runs.length / 2];
    }

    @Test
    void decodes8kbTokenUnder100ms() {
        long ms = medianRunMs(() -> decoder.decode(hsToken));
        assertTrue(ms < LIMIT_MS, "median decode took " + ms + " ms");
    }

    @Test
    void verifiesHmac8kbTokenUnder100ms() {
        long ms = medianRunMs(() -> {
            assertTrue(verifier.verify(hsToken, SECRET).isValid());
            return null;
        });
        assertTrue(ms < LIMIT_MS, "median HS256 verify took " + ms + " ms");
    }

    @Test
    void verifiesRsa8kbTokenUnder100ms() {
        long ms = medianRunMs(() -> {
            assertTrue(verifier.verify(rsToken, rsPublicPem).isValid());
            return null;
        });
        assertTrue(ms < LIMIT_MS, "median RS256 verify took " + ms + " ms");
    }
}
