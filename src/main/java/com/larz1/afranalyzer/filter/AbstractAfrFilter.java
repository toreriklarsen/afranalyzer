package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.filter.AfrFilter;

public abstract
class AbstractAfrFilter implements AfrFilter {
    private boolean enabled;

    protected AfrAnalyzerSettings afrAnalyzerSettings;

    public AbstractAfrFilter(AfrAnalyzerSettings afrAnalyzerSettings) {
        this.afrAnalyzerSettings = afrAnalyzerSettings;
    }


}
