package com.larz1.afranalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestAutoTuneService {
    @Test
    void myFirstTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    void findIndex() {
        double[] tpsArray = {0.0, 0.8, 2.3, 4.7, 7.8, 10.2, 14.8, 20.3, 29.7, 39.8, 50.0, 75.0, 100.0};
        assertEquals(java.util.Optional.of(12), java.util.Optional.of(CalcUtil.findIndex(tpsArray, 99)));
        assertEquals(java.util.Optional.of(12), java.util.Optional.of(CalcUtil.findIndex(tpsArray, 90)));
        assertEquals(java.util.Optional.of(12), java.util.Optional.of(CalcUtil.findIndex(tpsArray, 88)));
        assertEquals(java.util.Optional.of(11), java.util.Optional.of(CalcUtil.findIndex(tpsArray, 87)));
        assertNull(CalcUtil.findIndex(tpsArray, 101));
        assertNull(CalcUtil.findIndex(tpsArray, -1));
    }
}
