package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.List;
import org.junit.jupiter.api.Test;

/** US-1.3 / FR-5, FR-6 */
class ClaimInspectorTest {

    private static final long NOW = 1_800_000_000L;
    private final ClaimInspector inspector =
            new ClaimInspector(Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC));

    @Test
    void validWhenExpInFuture() {
        Map<String, Object> p = Map.of("exp", NOW + 7200);
        assertEquals(ClaimInspector.Status.VALID, inspector.inspect(p).status());
        assertEquals("Expires in 2 hours", inspector.inspect(p).message());
    }

    @Test
    void expiredWhenExpInPast() {
        Map<String, Object> p = Map.of("exp", NOW - 90);
        assertEquals(ClaimInspector.Status.EXPIRED, inspector.inspect(p).status());
        assertEquals("Expired 1 minute ago", inspector.inspect(p).message());
    }

    @Test
    void expiredExactlyAtExp() {
        assertEquals(ClaimInspector.Status.EXPIRED, inspector.inspect(Map.of("exp", NOW)).status());
    }

    @Test
    void notYetValidBeforeNbf() {
        Map<String, Object> p = Map.of("nbf", NOW + 30, "exp", NOW + 3600);
        assertEquals(ClaimInspector.Status.NOT_YET_VALID, inspector.inspect(p).status());
    }

    @Test
    void noExpiryWhenExpMissing() {
        assertEquals(ClaimInspector.Status.NO_EXPIRY, inspector.inspect(Map.of("sub", "x")).status());
    }

    @Test
    void reportsNonNumericExpAsInvalid() {
        Map<String, Object> payload = Map.of("exp", "tomorrow");
        assertEquals(ClaimInspector.Status.INVALID_TIME_CLAIM, inspector.inspect(payload).status());
        assertTrue(inspector.inspect(payload).message().startsWith("Invalid exp:"));
        assertEquals("Invalid timestamp", inspector.inspect(payload).readableTimes().get("exp"));
    }

    @Test
    void formatsTimeClaimsInOrder() {
        Map<String, String> times = inspector.inspect(Map.of("iat", 1516239022L, "exp", 1516242622L)).readableTimes();
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

    @Test
    void rejectsOutOfRangeAndNonFiniteTimestampsWithoutThrowing() {
        for (Number value : List.of(Long.MAX_VALUE, Long.MIN_VALUE, Double.MAX_VALUE,
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
            Map<String, Object> payload = Map.of("exp", value);
            assertEquals(ClaimInspector.Status.INVALID_TIME_CLAIM, inspector.inspect(payload).status(), value.toString());
            assertTrue(inspector.inspect(payload).message().startsWith("Invalid exp:"));
            assertEquals("Invalid timestamp", inspector.inspect(payload).readableTimes().get("exp"));
        }
    }

    @Test
    void distinguishesNullExpiryFromMissingExpiry() {
        assertEquals(ClaimInspector.Status.INVALID_TIME_CLAIM, inspector.inspect(Json.parseObject("{\"exp\":null}")).status());
        assertEquals("No expiry set (exp claim missing)", inspector.inspect(Map.of()).message());
    }

    @Test
    void checksAllPresentTimeClaims() {
        for (String claim : List.of("exp", "iat", "nbf")) {
            Map<String, Object> payload = new java.util.HashMap<>(Map.of("exp", NOW + 3600));
            payload.put(claim, "bad date");
            assertEquals(ClaimInspector.Status.INVALID_TIME_CLAIM, inspector.inspect(payload).status());
            assertTrue(inspector.inspect(payload).message().startsWith("Invalid " + claim + ":"));
        }
    }

    @Test
    void handlesDatesOutsideTheDisplayableRange() {
        Map<String, Object> payload = Map.of("iat", Instant.MAX.getEpochSecond());
        assertEquals(ClaimInspector.Status.INVALID_TIME_CLAIM, inspector.inspect(payload).status());
        assertEquals("Invalid timestamp", inspector.inspect(payload).readableTimes().get("iat"));
    }

    @Test
    void formatsFractionalSecondsOnEitherSideOfTheEpoch() {
        assertEquals("1970-01-01 00:00:00 Z", inspector.inspect(Map.of("iat", 0.5)).readableTimes().get("iat"));
        assertEquals("1969-12-31 23:59:59 Z", inspector.inspect(Map.of("iat", -0.5)).readableTimes().get("iat"));
    }
}
