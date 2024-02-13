package io.mrohan.henrymeds.domain;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdGeneratorTest {

    @Test
    @RepeatedTest(100)
    public void testIdGeneration() {
        String id = IdGenerator.generate();
        System.out.println(id);
        assertEquals(27, id.length());
    }
}
