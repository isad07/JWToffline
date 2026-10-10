package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonTest {

    @Test
    void parsesNestedValues() {
        Map<String, Object> m = Json.parseObject(
                "{\"a\":1,\"b\":[true,false,null],\"c\":{\"d\":\"x\\ny\"},\"e\":2.5,\"f\":\"\\u00e9\"}");
        assertEquals(1L, m.get("a"));
        assertEquals(List.of(true, false), ((List<?>) m.get("b")).subList(0, 2));
        assertNull(((List<?>) m.get("b")).get(2));
        assertEquals("x\ny", ((Map<?, ?>) m.get("c")).get("d"));
        assertEquals(2.5, m.get("e"));
        assertEquals("\u00e9", m.get("f"));
    }

    @Test
    void keepsKeyOrderWhenPrinting() {
        String compact = Json.compact(Json.parse("{ \"z\": 1, \"a\": [1, 2], \"m\": {} }"));
        assertEquals("{\"z\":1,\"a\":[1,2],\"m\":{}}", compact);
    }

    @Test
    void escapesStrings() {
        assertEquals("\"q\\\"uote\\\\\"", Json.compact("q\"uote\\"));
    }

    @Test
    void rejectsBrokenJson() {
        assertThrows(JwtException.class, () -> Json.parse("{\"a\":}"));
        assertThrows(JwtException.class, () -> Json.parse("{\"a\":1"));
        assertThrows(JwtException.class, () -> Json.parse("{\"a\":1} extra"));
        assertThrows(JwtException.class, () -> Json.parseObject("[1,2]"));
    }
}
