package com.jwtoffline.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/** US-4.5 / FR-20 */
class TokenHistoryTest {

    @Test
    void newestFirstWithoutDuplicates() {
        TokenHistory h = new TokenHistory();
        h.add("a");
        h.add("b");
        h.add("a");
        assertEquals(List.of("a", "b"), h.recent());
    }

    @Test
    void deduplicatesBearerAndRawTokens() {
        TokenHistory history = new TokenHistory();
        history.add("  Bearer a.b.c\n");
        history.add("a.b.c");
        history.add("  bEaReR   a.b.c  ");
        assertEquals(List.of("a.b.c"), history.recent());
    }

    @Test
    void dropsOldestPastLimit() {
        TokenHistory h = new TokenHistory(2);
        h.add("1");
        h.add("2");
        h.add("3");
        assertEquals(List.of("3", "2"), h.recent());
    }

    @Test
    void ignoresBlankTokens() {
        TokenHistory h = new TokenHistory();
        h.add("  ");
        h.add(null);
        assertEquals(List.of(), h.recent());
    }

    @Test
    void limitMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new TokenHistory(0));
    }
}
