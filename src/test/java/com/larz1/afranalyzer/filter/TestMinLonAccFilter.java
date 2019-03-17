package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMinLonAccFilter {

    @Test
    public void testMinLonAccFilterEnabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minLonAccEnabled= true;
        as.minLonAcc = 0.5;
        MinLonAccFilter minLonAccFilter = new MinLonAccFilter(as);
        LogValue logValue = new LogValue(0, 2000, 90.0, 0.0, 14.0, 0.6);
        assertFalse(minLonAccFilter.filter(logValue));
        as.minLonAcc = 0.7;
        assertTrue(minLonAccFilter.filter(logValue));
    }

    @Test
    public void testMinLonAccFilterDisabled() {
        AfrAnalyzerSettings as = new AfrAnalyzerSettings();
        as.minLonAccEnabled = false;
        MinLonAccFilter minLonAccFilter = new MinLonAccFilter(as);
        assertFalse(minLonAccFilter.filter(null));
    }
}