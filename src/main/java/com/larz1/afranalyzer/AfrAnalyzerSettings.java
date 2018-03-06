package com.larz1.afranalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AfrAnalyzerSettings {

    @Value("${afr.file:data/almeria-before.csv}")
    String afrFile;

    @Value("${targetafr.file:data/zx10r-tel01-Target.csv}")
    String targetAfrFile;

    @Value("${minafrenabled:false}")
    Boolean minAfrEnabled;
    @Value("${minafr:10.0}")
    Double minAfr;

    @Value("${maxafrenabled:false}")
    Boolean maxAfrEnabled;
    @Value("${maxafr:15.0}")
    Double maxAfr;

    @Value("${quickshiftenabled:false}")
    Boolean quickshiftEnabled;

    @Value("${neutralenabled:false}")
    Boolean neutralEnabled;


    // todo no gui
    @Value("${maxtunepercentageabled:false}")
    Boolean maxtunepercentageEnabled;
    @Value("${maxtunepercentage:0.1}")
    Double maxTunePercentage;

    @Value("${minectenabled:false}")
    Boolean minEctEnabled;
    @Value("${minect:80}")
    Integer minEct;

    @Value("${lowrpmenabled:false}")
    Boolean lowRpmEnabled;
    @Value("${lowrpm:500}")
    Integer lowRpm;


    // used for autotune
    @Value("${celltoleranceenabled:false}")
    Boolean cellToleranceEnabled;
    @Value("${cellTolerance:0.25}")
    Double cellTolerance;





}
