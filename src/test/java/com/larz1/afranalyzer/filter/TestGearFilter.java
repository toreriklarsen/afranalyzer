package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGearFilter {

    @Test
    public void testGearFilterEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.gearEnabled= true;
        as.gear = 0;
        GearFilter gearFilter = new GearFilter(as);
        LogValue logValue = new LogValue(0, 2000, 90.0, 0.0, 12.0);
        assertFalse(gearFilter.filter(logValue));
        logValue.setGear(1.0);
        assertTrue(gearFilter.filter(logValue));
        logValue.setGear(2.0);
        assertTrue(gearFilter.filter(logValue));
        logValue.setGear(3.0);
        assertTrue(gearFilter.filter(logValue));
        logValue.setGear(4.0);
        assertTrue(gearFilter.filter(logValue));
        logValue.setGear(5.0);
        assertTrue(gearFilter.filter(logValue));
        logValue.setGear(6.0);
        assertTrue(gearFilter.filter(logValue));
    }

    @Test
    public void testGearFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.gearEnabled= false;
        GearFilter gearFilter = new GearFilter(as);
        assertFalse(gearFilter.filter(null));
    }
}