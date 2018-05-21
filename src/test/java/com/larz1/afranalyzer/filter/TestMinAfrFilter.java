package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMinAfrFilter {

    @Test
    public void testMinFilterEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minAfrEnabled = true;
        as.minAfr = 13.0;
        MinAfrFilter MinAfrFilter = new MinAfrFilter(as);
        LogValue logValue = new LogValue(0, 2000, 90.0, 0.0, 14.0);
        assertFalse(MinAfrFilter.filter(logValue));
        as.minAfr = 15.0;
        assertTrue(MinAfrFilter.filter(logValue));
    }

    @Test
    public void testMinFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minAfrEnabled = false;
        MinAfrFilter MinAfrFilter = new MinAfrFilter(as);
        assertFalse(MinAfrFilter.filter(null));
    }
}