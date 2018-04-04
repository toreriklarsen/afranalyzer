package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMaxAfrFilter {

    @Test
    public void testMaxFilterEnabled() {
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
    public void testMaxFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.maxAfrEnabled = false;
        MaxAfrFilter maxAfrFilter = new MaxAfrFilter(as);
        assertFalse(maxAfrFilter.filter(null));
    }
}