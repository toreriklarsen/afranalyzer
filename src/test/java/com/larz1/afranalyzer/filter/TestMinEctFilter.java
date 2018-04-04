package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMinEctFilter {

    @Test
    public void testMinEctEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minEctEnabled = true;
        as.minEct = 80;
        MinEctFilter minEctFilter = new MinEctFilter(as);
        LogValue logValue = new LogValue(0, 499.0, 90.0, 0.0, 14.0);
        logValue.setEct(79.0);
        assertTrue(minEctFilter.filter(logValue));
        as.minEct = 78;
        assertFalse(minEctFilter.filter(logValue));
    }

    @Test
    public void testMinEctDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minEctEnabled = false;
        MinEctFilter minEctFilter = new MinEctFilter(as);
        assertFalse(minEctFilter.filter(null));
    }
}