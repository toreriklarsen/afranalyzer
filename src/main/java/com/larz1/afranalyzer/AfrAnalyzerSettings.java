package com.larz1.afranalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AfrAnalyzerSettings {

    @Value("${afr.file:data/almeria-before.csv}")
    public String afrFile;

    @Value("${targetafr.file:data/zx10r-tel01-Target.csv}")
    public String targetAfrFile;

    @Value("${minafrenabled:false}")
    public Boolean minAfrEnabled;
    @Value("${minafr:10.0}")
    public Double minAfr;

    @Value("${maxafrenabled:false}")
    public Boolean maxAfrEnabled;
    @Value("${maxafr:15.0}")
    public Double maxAfr;

    @Value("${quickshiftenabled:false}")
    public Boolean quickshiftEnabled;

    @Value("${neutralenabled:false}")
    public Boolean neutralEnabled;

    @Value("${minectenabled:false}")
    public Boolean minEctEnabled;
    @Value("${minect:80}")
    public Integer minEct;

    @Value("${lowrpmenabled:false}")
    public Boolean lowRpmEnabled;
    @Value("${lowrpm:500}")
    public Integer lowRpm;

    // used for autotune
    @Value("${maxtunepercentageabled:false}")
    public Boolean maxtunepercentageEnabled;
    @Value("${maxtunepercentage:0.1}")
    Double maxTunePercentage;

    @Value("${celltoleranceenabled:false}")
    public Boolean cellToleranceEnabled;
    @Value("${cellTolerance:0.5}")
    public Double cellTolerance;

    @Value("${tablecellprecision:1}")
    public Integer tableCellPrecision;

    @Value("${egocompensationenabled:false}")
    public Boolean egoCompensationEnabled;
}
