package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxAfrFilterTest {

    @Test
    void testMaxFilterEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.maxAfrEnabled = true;
        as.maxAfr = 13.0;
        MaxAfrFilter maxAfrFilter = new MaxAfrFilter(as);
        LogValue logValue = new LogValue(0, 2000.0, 90.0, 0.0, 14.0);
        assertTrue(maxAfrFilter.filter(logValue));
        as.maxAfr = 15.0;
        assertFalse(maxAfrFilter.filter(logValue));
    }

    @Test
    void testMaxFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.maxAfrEnabled = false;
        MaxAfrFilter maxAfrFilter = new MaxAfrFilter(as);
        assertFalse(maxAfrFilter.filter(null));
    }
}