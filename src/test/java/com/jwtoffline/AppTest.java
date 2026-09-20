package com.jwtoffline;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void applicationStarts() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
