package com.jwtoffline.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Inspects the standard time claims (exp, iat, nbf) without checking the signature. */
public class ClaimInspector {

    public enum Status { VALID, EXPIRED, NOT_YET_VALID, NO_EXPIRY, INVALID_TIME_CLAIM }

    private static final List<String> TIME_CLAIMS = List.of("exp", "iat", "nbf");
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private final Clock clock;

    public ClaimInspector() {
        this(Clock.systemDefaultZone());
    }

    public ClaimInspector(Clock clock) {
        this.clock = clock;
    }

    public record Inspection(Status status, String message, Map<String, String> readableTimes) {
        public Inspection {
            readableTimes = Collections.unmodifiableMap(new LinkedHashMap<>(readableTimes));
        }
    }

    public Inspection inspect(Map<String, Object> payload) {
        Map<String, Long> seconds = new LinkedHashMap<>();
        Map<String, String> times = new LinkedHashMap<>();
        String invalidClaim = null;
        for (String claim : TIME_CLAIMS) {
            if (!payload.containsKey(claim)) {
                continue;
            }
            ZonedDateTime date = timestamp(payload.get(claim), clock.getZone());
            if (date == null) {
                if (invalidClaim == null) invalidClaim = claim;
                times.put(claim, "Invalid timestamp");
            } else {
                seconds.put(claim, date.toEpochSecond());
                times.put(claim, FORMAT.format(date));
            }
        }
        long now = clock.instant().getEpochSecond();
        Long exp = seconds.get("exp");
        Long nbf = seconds.get("nbf");
        Status status;
        if (invalidClaim != null) status = Status.INVALID_TIME_CLAIM;
        else if (nbf != null && now < nbf) status = Status.NOT_YET_VALID;
        else if (exp == null) status = Status.NO_EXPIRY;
        else status = now >= exp ? Status.EXPIRED : Status.VALID;

        String message = switch (status) {
            case EXPIRED -> "Expired " + humanDuration(now - exp) + " ago";
            case VALID -> "Expires in " + humanDuration(exp - now);
            case NOT_YET_VALID -> "Starts in " + humanDuration(nbf - now);
            case INVALID_TIME_CLAIM -> "Invalid " + invalidClaim
                    + ": expected numeric seconds within the supported date range";
            case NO_EXPIRY -> "No expiry set (exp claim missing)";
        };
        return new Inspection(status, message, times);
    }

    static String humanDuration(long seconds) {
        seconds = Math.abs(seconds);
        if (seconds < 60) return seconds + (seconds == 1 ? " second" : " seconds");
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + (minutes == 1 ? " minute" : " minutes");
        long hours = minutes / 60;
        if (hours < 48) return hours + (hours == 1 ? " hour" : " hours");
        long days = hours / 24;
        return days + " days";
    }

    private static ZonedDateTime timestamp(Object value, ZoneId zone) {
        if (!(value instanceof Number number)) {
            return null;
        }
        try {
            long seconds = new BigDecimal(number.toString()).setScale(0, RoundingMode.FLOOR).longValueExact();
            return Instant.ofEpochSecond(seconds).atZone(zone);
        } catch (DateTimeException | ArithmeticException | NumberFormatException ex) {
            return null;
        }
    }
}
