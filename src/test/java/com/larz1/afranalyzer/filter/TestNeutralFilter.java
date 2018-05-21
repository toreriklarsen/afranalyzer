package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestNeutralFilter {

    @Test
    public void testNeutralFilterEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.neutralEnabled = true;
        NeutralFilter neutralFilter = new NeutralFilter(as);
        LogValue logValue = new LogValue(0, 2000, 90.0, 0.0, 12.0);
        assertTrue(neutralFilter.filter(logValue));
        logValue.setGear(1.0);
        assertFalse(neutralFilter.filter(logValue));
    }

    @Test
    public void testNeutralFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.neutralEnabled = false;
        NeutralFilter neutralFilter = new NeutralFilter(as);
        assertFalse(neutralFilter.filter(null));
    }
}