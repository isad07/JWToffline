package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** US-1.3 / FR-5, FR-6 */
class ClaimInspectorTest {

    private static final long NOW = 1_800_000_000L;
    private final ClaimInspector inspector =
            new ClaimInspector(Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC));

    @Test
    void validWhenExpInFuture() {
        Map<String, Object> p = Map.of("exp", NOW + 7200);
        assertEquals(ClaimInspector.Status.VALID, inspector.status(p));
        assertEquals("Valid, expires in 2 hours", inspector.describe(p));
    }

    @Test
    void expiredWhenExpInPast() {
        Map<String, Object> p = Map.of("exp", NOW - 90);
        assertEquals(ClaimInspector.Status.EXPIRED, inspector.status(p));
        assertEquals("Expired 1 minute ago", inspector.describe(p));
    }

    @Test
    void expiredExactlyAtExp() {
        assertEquals(ClaimInspector.Status.EXPIRED, inspector.status(Map.of("exp", NOW)));
    }

    @Test
    void notYetValidBeforeNbf() {
        Map<String, Object> p = Map.of("nbf", NOW + 30, "exp", NOW + 3600);
        assertEquals(ClaimInspector.Status.NOT_YET_VALID, inspector.status(p));
    }

    @Test
    void noExpiryWhenExpMissing() {
        assertEquals(ClaimInspector.Status.NO_EXPIRY, inspector.status(Map.of("sub", "x")));
    }

    @Test
    void ignoresNonNumericExp() {
        assertEquals(ClaimInspector.Status.NO_EXPIRY, inspector.status(Map.of("exp", "tomorrow")));
    }

    @Test
    void formatsTimeClaimsInOrder() {
        Map<String, String> times = inspector.readableTimes(Map.of("iat", 1516239022L, "exp", 1516242622L));
        assertEquals("[exp, iat]", times.keySet().toString());
        assertEquals("2018-01-18 01:30:22 Z", times.get("iat"));
    }

    @Test
    void humanDurations() {
        assertEquals("1 second", ClaimInspector.humanDuration(1));
        assertEquals("45 minutes", ClaimInspector.humanDuration(45 * 60));
        assertEquals("3 days", ClaimInspector.humanDuration(3 * 86400));
        assertTrue(ClaimInspector.humanDuration(-120).startsWith("2 minutes"));
    }
}
