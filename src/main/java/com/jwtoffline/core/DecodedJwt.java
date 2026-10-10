package com.jwtoffline.core;

import java.util.Collections;
import java.util.Map;

/** Result of splitting and decoding a token. Immutable. */
public final class DecodedJwt {

    private final String rawToken;
    private final String headerSegment;
    private final String payloadSegment;
    private final String signatureSegment;
    private final Map<String, Object> header;
    private final Map<String, Object> payload;

    public DecodedJwt(String rawToken, String headerSegment, String payloadSegment, String signatureSegment,
                      Map<String, Object> header, Map<String, Object> payload) {
        this.rawToken = rawToken;
        this.headerSegment = headerSegment;
        this.payloadSegment = payloadSegment;
        this.signatureSegment = signatureSegment;
        this.header = Collections.unmodifiableMap(header);
        this.payload = Collections.unmodifiableMap(payload);
    }

    public String rawToken() { return rawToken; }
    public String headerSegment() { return headerSegment; }
    public String payloadSegment() { return payloadSegment; }
    public String signatureSegment() { return signatureSegment; }
    public Map<String, Object> header() { return header; }
    public Map<String, Object> payload() { return payload; }

    /** The exact bytes that were signed: base64url(header) + "." + base64url(payload). */
    public String signingInput() {
        return headerSegment + "." + payloadSegment;
    }

    public String algorithmName() {
        Object alg = header.get("alg");
        return alg == null ? "none" : alg.toString();
    }

    public String prettyHeader() { return Json.pretty(header); }
    public String prettyPayload() { return Json.pretty(payload); }
}
