package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.AutoTuneService;
import com.larz1.afranalyzer.LogValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.larz1.afranalyzer.CalcUtil.findIndex;

@Component
public class CellToleranceFilter extends AbstractAfrFilter {

    @Autowired
    public CellToleranceFilter(AfrAnalyzerSettings afrAnalyzerSettings) {
        super(afrAnalyzerSettings);
    }

    @Override
    public boolean filter(LogValue logValue, Object... args) {
        if (afrAnalyzerSettings.cellToleranceEnabled) {
            return findIndex(AutoTuneService.rpmArray, logValue.getRpm(), afrAnalyzerSettings.cellTolerance) == null;
        }

        return false;
    }
}
