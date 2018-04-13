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
        LogValue logValue = new LogValue(0, 1000.0, 90.0, 0.0, 14.0);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(2000.0);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13000.0);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13500.0);
        assertFalse(cellToleranceFilter.filter(logValue));
        logValue.setRpm(13501.0);
        assertFalse(cellToleranceFilter.filter(logValue));
    }

    @Test
    public void testCellToleranceWhateverEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.cellToleranceEnabled = true;
        as.cellTolerance = 0.25;
        CellToleranceFilter cellToleranceFilter = new CellToleranceFilter(as);
        LogValue logValue = new LogValue(0, 1000.0, 90.0, 0.0, 14.0);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2000.0);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2124.0);
        assertFalse(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2200.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2249.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2250.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2251.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2500.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2750.0);
        assertTrue(cellToleranceFilter.filter(logValue));

        logValue.setRpm(2751.0);
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