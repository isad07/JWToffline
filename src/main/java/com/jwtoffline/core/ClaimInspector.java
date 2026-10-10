package com.jwtoffline.core;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Turns the standard time claims (exp, iat, nbf) into readable dates and works out whether a token is still valid. */
public class ClaimInspector {

    public enum Status { VALID, EXPIRED, NOT_YET_VALID, NO_EXPIRY }

    public static final String[] TIME_CLAIMS = {"exp", "iat", "nbf"};

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private final Clock clock;

    public ClaimInspector() {
        this(Clock.systemDefaultZone());
    }

    public ClaimInspector(Clock clock) {
        this.clock = clock;
    }

    public Status status(Map<String, Object> payload) {
        long now = clock.instant().getEpochSecond();
        Long nbf = epochSeconds(payload.get("nbf"));
        if (nbf != null && now < nbf) {
            return Status.NOT_YET_VALID;
        }
        Long exp = epochSeconds(payload.get("exp"));
        if (exp == null) {
            return Status.NO_EXPIRY;
        }
        return now >= exp ? Status.EXPIRED : Status.VALID;
    }

    /** Readable version of each time claim that is present, in the order exp, iat, nbf. */
    public Map<String, String> readableTimes(Map<String, Object> payload) {
        Map<String, String> out = new LinkedHashMap<>();
        ZoneId zone = clock.getZone();
        for (String claim : TIME_CLAIMS) {
            Long seconds = epochSeconds(payload.get(claim));
            if (seconds != null) {
                out.put(claim, FORMAT.format(Instant.ofEpochSecond(seconds).atZone(zone)));
            }
        }
        return out;
    }

    /** Short human sentence for the status banner, e.g. "Expired 3 hours ago". */
    public String describe(Map<String, Object> payload) {
        long now = clock.instant().getEpochSecond();
        switch (status(payload)) {
            case EXPIRED:
                return "Expired " + humanDuration(now - epochSeconds(payload.get("exp"))) + " ago";
            case VALID:
                return "Valid, expires in " + humanDuration(epochSeconds(payload.get("exp")) - now);
            case NOT_YET_VALID:
                return "Not valid yet, starts in " + humanDuration(epochSeconds(payload.get("nbf")) - now);
            default:
                return "No expiry set (exp claim missing)";
        }
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

    private static Long epochSeconds(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
