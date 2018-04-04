package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMinRpmFilter {

    @Test
    public void testLowRpmEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.lowRpmEnabled = true;
        as.lowRpm = 500;
        MinRpmFilter minRpmFilter = new MinRpmFilter(as);
        LogValue logValue = new LogValue(0, 499.0, 90.0, 0.0, 14.0);
        assertTrue(minRpmFilter.filter(logValue));
        as.lowRpm = 1000;
        assertTrue(minRpmFilter.filter(logValue));
    }

    @Test
    public void testLowRpmDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.lowRpmEnabled = false;
        MinRpmFilter minRpmFilter = new MinRpmFilter(as);
        assertFalse(minRpmFilter.filter(null));
    }
}