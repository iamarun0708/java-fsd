package com.cognizant.junit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ArrangeActAssertTest {

    private List<String> list;

    @BeforeEach
    public void setUp() {
        // Arrange phase (Fixture setup)
        list = new ArrayList<>();
        list.add("InitialItem");
    }

    @AfterEach
    public void tearDown() {
        // Teardown phase
        list.clear();
        list = null;
    }

    @Test
    public void testAddElement() {
        // Act phase
        list.add("NewItem");

        // Assert phase
        assertEquals(2, list.size(), "Size of list should be 2 after adding NewItem");
        assertTrue(list.contains("NewItem"), "List should contain NewItem");
    }
}
