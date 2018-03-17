package com.larz1.afranalyzer.filter;

import com.larz1.afranalyzer.LogValue;
import org.springframework.stereotype.Component;

@Component
public interface AfrFilter {
    /**
     *
     * @param logValue
     * @param args
     * @return true if filtered out
     */
    boolean filter(LogValue logValue, Object ... args);
}
