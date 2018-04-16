package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.AfrAnalyzerSettings;
import com.larz1.afranalyzer.LogValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NeutralFilter extends AbstractAfrFilter {

    @Autowired
    public NeutralFilter(AfrAnalyzerSettings afrAnalyzerSettings) {
        super(afrAnalyzerSettings);
    }

    @Override
    public boolean filter(LogValue logValue, Object... args) {
        if (afrAnalyzerSettings.neutralEnabled) {
            return logValue.getGear() == 0.0
                    ;
        }

        return false;
    }
}
