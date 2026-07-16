package com.cognizant.junit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AssertionsTest {

    @Test
    public void testAssertions() {
        // Assert equals
        assertEquals(5, 2 + 3, "2 + 3 should be 5");
        
        // Assert true
        assertTrue(5 > 3, "5 should be greater than 3");
        
        // Assert false
        assertFalse(5 < 3, "5 should not be less than 3");
        
        // Assert null
        assertNull(null, "Value should be null");
        
        // Assert not null
        assertNotNull(new Object(), "Value should not be null");
    }
}
