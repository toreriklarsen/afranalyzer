package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCellToleranceFilter {

    @Test
    public void testCellToleranceOneEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.cellToleranceEnabled = true;
        as.cellTolerance = 1.0;
        CellToleranceFilter cellToleranceFilter = new CellToleranceFilter(as);
        LogValue logValue = new LogValue(0, 1000, 90.0, 0.0, 14.0);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(2000);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13000);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13500);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13501);
        assertFalse(cellToleranceFilter.filter(logValue));
    }

    @Test
    public void testCellToleranceWhateverEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.cellToleranceEnabled = true;
        as.cellTolerance = 0.25;
        CellToleranceFilter cellToleranceFilter = new CellToleranceFilter(as);
        LogValue logValue = new LogValue(0, 1000, 90.0, 0.0, 14.0);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2000);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2124);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2200);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2249);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2250);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2251);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2500);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2750);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2751);
        assertTrue(cellToleranceFilter.filter(logValue));
    }

    @Test
    public void testCellToleranceDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.cellToleranceEnabled = false;
        CellToleranceFilter cellToleranceFilter = new CellToleranceFilter(as);
        assertFalse(cellToleranceFilter.filter(null));
    }
}