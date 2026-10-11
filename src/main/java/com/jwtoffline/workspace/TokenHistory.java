package com.jwtoffline.workspace;

import com.jwtoffline.core.JwtDecoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Keeps the most recent tokens decoded or created in this session, newest first, no duplicates. */
public class TokenHistory {

    public static final int DEFAULT_LIMIT = 20;

    private final Deque<String> recent = new ArrayDeque<>();
    private final int limit;

    public TokenHistory() {
        this(DEFAULT_LIMIT);
    }

    public TokenHistory(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }
        this.limit = limit;
    }

    public void add(String token) {
        String t = JwtDecoder.normalizeToken(token);
        if (t.isBlank()) {
            return;
        }
        recent.remove(t);
        recent.addFirst(t);
        while (recent.size() > limit) {
            recent.removeLast();
        }
    }

    public List<String> recent() {
        return new ArrayList<>(recent);
    }

}
