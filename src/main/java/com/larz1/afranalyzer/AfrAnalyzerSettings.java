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

    @Value("${maxafrenabled:true}")
    public Boolean maxAfrEnabled;
    @Value("${maxafr:17.0}")
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

    @Value("${egocompensationenabled:true}")
    public Boolean egoCompensationEnabled;

    // used for autotune
    @Value("${maxtunepercentagenabled:false}")
    public Boolean maxtunepercentageEnabled;
    @Value("${maxtunepercentage:0.1}")
    Double maxTunePercentage;

    @Value("${tunestrengthabled:true}")
    public Boolean tuneStrengthEnabled;
    @Value("${tuneStrength:1.0}")
    Double tuneStrength;

    @Value("${celltoleranceenabled:false}")
    public Boolean cellToleranceEnabled;
    @Value("${cellTolerance:1.0}")
    public Double cellTolerance;

    @Value("${minvaluesincellenabled:false}")
    public Boolean minValuesInCellEnabled;
    @Value("${minvaluesincell:2}")
    public Integer minValuesInCell;

    @Value("${tablecellprecision:1}")
    public Integer tableCellPrecision;

    // engine settings
    @Value("${enginevolume:998}")
    public  Integer engineVolume;

    @Value("${maxrpm:13550}")
    public  Integer maxRpm;

    @Value("${pipediameter:45}")
    public  Integer pipeDiameter;

    @Value("${pipelength:950}")
    public  Integer pipeLength;

    @Value("${ncylinders:4}")
    public  Integer nCylinders;
}
