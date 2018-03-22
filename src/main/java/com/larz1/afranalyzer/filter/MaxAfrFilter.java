package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxAfrFilter extends AbstractAfrFilter {

    @Autowired
    public MaxAfrFilter(AfrAnalyzerSettings afrAnalyzerSettings) {
        super(afrAnalyzerSettings);
    }

    @Override
    public boolean filter(LogValue logValue, Object... args) {
        if (afrAnalyzerSettings.maxAfrEnabled) {
            return logValue.getAfr() > afrAnalyzerSettings.maxAfr;
        }

        return false;
    }
}