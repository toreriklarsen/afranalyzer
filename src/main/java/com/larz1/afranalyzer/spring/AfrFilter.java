package com.larz1.afranalyzer.spring;

import com.larz1.afranalyzer.LogValue;

public interface AfrFilter {
    void filter(LogValue logValue);
}
