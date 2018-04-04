package com.larz1.afranalyzer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.larz1.afranalyzer.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.larz1.afranalyzer.CalcUtil.findIndex;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
@Service
public class AutoTuneService {
    private static final Logger logger = LoggerFactory.getLogger(AutoTuneService.class);

    @Autowired
    private AfrAnalyzerSettings afrAnalyzerSettings;

    @Autowired
    private MaxAfrFilter maxAfrFilter;

    @Autowired
    private MinAfrFilter minAfrFilter;

    @Autowired
    private MinRpmFilter minRpmFilter;

    @Autowired
    private NeutralFilter neutralFilter;

    @Autowired
    private CellToleranceFilter cellToleranceFilter;

    @Autowired
    private MinEctFilter minEctFilter;

    public enum PRINT {
        AFR,
        COUNT
    }

    public static double[] tpsArray = {0.0, 0.8, 2.3, 4.7, 7.8, 10.2, 14.8, 20.3, 29.7, 39.8, 50.0, 75.0, 100.0};
    //double[] tpsArray = {0.0, 2.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.9, 45.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0};
    public static double[] rpmArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 12500.0, 13000.0, 13500.0, 14000.0};


    @Value("${printafr:true}")
    private String printAfr;

    public AutoTuneService() {
    }

    public void print(PRINT print, AdjAFRValue[][] mArr, String header) {
        Boolean[][] bArr = new Boolean[tpsArray.length][rpmArray.length];
        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                bArr[i][j] = Boolean.FALSE;
            }
        }
        for (int i = 5; i < 8; i++) {
            bArr[1][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 8; i++) {
            bArr[2][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 9; i++) {
            bArr[3][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 9; i++) {
            bArr[4][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 10; i++) {
            bArr[5][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 10; i++) {
            bArr[6][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 10; i++) {
            bArr[7][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 11; i++) {
            bArr[8][i] = Boolean.TRUE;
        }
        for (int i = 5; i < 12; i++) {
            bArr[9][i] = Boolean.TRUE;
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[10][i] = Boolean.TRUE;
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[11][i] = Boolean.TRUE;
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[12][i] = Boolean.TRUE;
        }

        System.out.println("********** " + header + " **********");
        System.out.print("      ");
        System.out.print(ConsoleColors.GREEN);
        for (double aRpmArray : rpmArray) {
            System.out.format("% 7d", (int) aRpmArray);
        }

        System.out.print(ConsoleColors.RESET);
        System.out.println();
        for (int i = 0; i < tpsArray.length; i++) {
            //System.out.format("% 2d", i);
            System.out.print(ConsoleColors.GREEN);
            System.out.format("%- 6.1f", tpsArray[i]);
            System.out.print(ConsoleColors.RESET);
            for (int j = 0; j < rpmArray.length; j++) {
                if (bArr[i][j]) System.out.print(ConsoleColors.GREEN_BACKGROUND_BRIGHT);
                if (mArr[i][j] != null) {
                    //System.out.format("% 7.1f", (mArr[i][j].getCount() > 1) ? mArr[i][j].getAverage() : 1.0);
                    if (print.equals(PRINT.AFR)) {
                        System.out.format("% 7.1f", mArr[i][j].getAverage());
                    } else {
                        System.out.format("% 7d", mArr[i][j].getCount());
                    }
                    //System.out.print("(" + i + "," + j + ")");
                } else {
                    //print "0 "
                    if (print.equals(PRINT.AFR)) {
                        System.out.format("% 7.1f", 0.0);
                    } else {
                        System.out.format("% 7d", 0);
                    }
                }
                if (bArr[i][j]) System.out.print(ConsoleColors.RESET);
            }
            System.out.println();
        }
    }

    // todo tmp
    public List<LogValue> readAfrFile() throws IOException {
        return readAfrFile(afrAnalyzerSettings.afrFile);
    }

    private List<LogValue> readAfrFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readAfrFile(file);
    }

    public List<LogValue> readAfrFile(File file) throws IOException {
        logger.trace("readAfrFile {}", file);

        CsvSchema schema = CsvSchema.builder()
                .setSkipFirstDataRow(true)
                .setColumnSeparator(',')
                .addColumn("Time", CsvSchema.ColumnType.NUMBER)
                .addColumn("ZX_RPM", CsvSchema.ColumnType.NUMBER)
                .addColumn("ZX_TPS", CsvSchema.ColumnType.NUMBER)
                .addColumn("ZX_GEAR", CsvSchema.ColumnType.NUMBER)
                .addColumn("LLC_AFR", CsvSchema.ColumnType.NUMBER)
                .build();

        CsvMapper mapper = new CsvMapper();
        ObjectReader oReader = mapper.readerFor(LogValue.class).with(schema);

        // read from file
        List<LogValue> logValues = new ArrayList<>();
        Reader reader = new FileReader(file);
        MappingIterator<LogValue> mi = oReader.readValues(reader);
        while (mi.hasNext()) {
            logValues.add(mi.next());
        }

        return logValues;
    }

    public AdjAFRValue[][] readTargetAfrFile() throws IOException {
        return readTargetAfrFile(afrAnalyzerSettings.targetAfrFile);
    }

    private AdjAFRValue[][] readTargetAfrFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readTargetAfrFile(file);
    }

    public AdjAFRValue[][] readTargetAfrFile(File csvFile) throws IOException {
        logger.trace("readTargetAfrFile {}", csvFile);

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        String[][] rows = mapper.readValue(csvFile, String[][].class);

        AdjAFRValue[][] mArr = new AdjAFRValue[tpsArray.length][rpmArray.length];
        for (int i = 1; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                mArr[i - 1][j] = new AdjAFRValue(new Double(rows[i][j + 1]));
            }
        }
        for (int j = 0; j < rpmArray.length; j++) {
            mArr[12][j] = new AdjAFRValue(new Double(rows[13][j + 1]));
        }

        return mArr;
    }

    /**
     * Filter out values
     *
     * @param dataList
     * @return
     */
    List<LogValue> filter(List<LogValue> dataList) {
        final int[] prevGear = {6};
        final int[] gear = {0};
        final int[] i = {0};
        List<LogValue> retList = new ArrayList<>(dataList.size());

        dataList.forEach(data -> {
            gear[0] = (int) data.getGear();
            LogValue logValue = new LogValue(data);
            data.setSkip(false);

            if (maxAfrFilter.filter(data)) {
                logger.trace("filtering out afr value, {} > max {}, tps:{} time:{}", data.getAfr(), afrAnalyzerSettings.maxAfr, data.getTps(), data.getTime());
            } else if (minAfrFilter.filter(data)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr value, {} < max {}, tps:{} time:{}", data.getAfr(), afrAnalyzerSettings.minAfr, data.getTps(), data.getTime());
            } else if (minEctFilter.filter(data)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value,ECT {} < {}", data.getAfr(), data.getEct(), afrAnalyzerSettings.minEct);
            } else if (minRpmFilter.filter(data)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value,RPM {} < {}", data.getAfr(), data.getRpm(), afrAnalyzerSettings.lowRpm);
            } else if (neutralFilter.filter(data)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value, gear is 0(neutral)", data.getAfr());
            } else if (cellToleranceFilter.filter(data, afrAnalyzerSettings.cellTolerance)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value, rpm {} is outside tolerance {}, time: {}", data.getAfr(), data.getRpm(), 0.25D, data.getTime());
            } else if ((afrAnalyzerSettings.quickshiftEnabled) && (gear[0] > prevGear[0])) {
                // filter out to prev values
                dataList.get(i[0] - 2).setSkip(true);
                dataList.get(i[0] - 1).setSkip(true);
                //logValue.setSkip(true);
                logger.trace("filtering out afr:{} afr:{} time:{} due to upshift", dataList.get(i[0] - 2).getAfr(), dataList.get(i[0] - 1).getAfr(), data.getTime());
            } else {
                retList.add(logValue);
                //logValue.setSkip(false);
            }

            prevGear[0] = gear[0];
            i[0]++;
        });

        return retList;
    }


    AdjAFRValue[][] convert2Map(List<LogValue> logValues) {
        AdjAFRValue[][] mArr = new AdjAFRValue[tpsArray.length][rpmArray.length];
        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                mArr[i][j] = new AdjAFRValue();
            }
        }

        logValues.forEach(value -> {
            if (!value.isSkip()) {
                // find tps index
                Integer tpsInd = findIndex(tpsArray, value.getTps());
                Integer rpmInd = findIndex(rpmArray, value.getRpm());
                if ((tpsInd == null) || (rpmInd == null)) {
                    logger.debug("got taken out");
                }
                if (mArr[tpsInd][rpmInd] == null) {
                    mArr[tpsInd][rpmInd] = new AdjAFRValue();
                }
                mArr[tpsInd][rpmInd].addAFRValue(value);
            }
        });

        return mArr;
    }

    // todo check algo
    AdjAFRValue[][] calculateCompensation(AdjAFRValue[][] filteredMap, AdjAFRValue[][] targetMap) {
        AdjAFRValue[][] mArr = new AdjAFRValue[tpsArray.length][rpmArray.length];

        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                if ((filteredMap[i][j].getAverage() == 0.0) || (targetMap[i][j].getAverage() == 0.0)) {
                    mArr[i][j] = new AdjAFRValue(0.0);
                } else {
                    Double diff = (filteredMap[i][j].getAverage() / targetMap[i][j].getAverage()) - 1;
                    if (diff == -1) {
                        mArr[i][j] = new AdjAFRValue(0.0);
                    } else {
                        // nb rounding
                        mArr[i][j] = new AdjAFRValue(diff * 100);
                    }
                }
            }
        }

        return mArr;
    }

    AdjAFRValue[][] calculateEgo() {
        AdjAFRValue[][] mArr = new AdjAFRValue[tpsArray.length][rpmArray.length];

        // todo set this in settings
        double maxFLux = CalcUtil.maxFlux(998, 13550);
        double pipeVolume = CalcUtil.pipeVolume(45.0, 95.0, 4);
        double minFLux = CalcUtil.minFlux(pipeVolume, 250);


        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                mArr[i][j] = new AdjAFRValue(CalcUtil.ego(250, maxFLux, minFLux, pipeVolume, rpmArray[j], tpsArray[i]));
            }
        }

        return mArr;
    }

    /**
     * Apply exhaust gas offset.
     * At the moment we are logging at 10 Hz, to be more precise, we are applying interpolation to get a new value
     *
     * @param logValues
     * @param args
     */
    public void applyEgoNew(List<LogValue> logValues, Object... args) {
        int egoCorrectionCount = 0;
        boolean fixedEgoFLag = false;
        int fixedEgo = 0;
        int ego;
        if (args.length == 1) {
            fixedEgoFLag = true;
            fixedEgo = (int) args[0];
        }
        int engineVolume = 998;
        int maxRpm = 13550;
        int pipeDiameter = 45;
        int pipeLength = 950; //
        int nCylinders = 4;

        int sampleInterval = CalcUtil.decideInterval(logValues);

        double maxFLux = CalcUtil.maxFlux(engineVolume, maxRpm);
        double pipeVolume = CalcUtil.pipeVolume(pipeDiameter, pipeLength, nCylinders);
        double minFLux = CalcUtil.minFlux(pipeVolume, 250);


        for (int i = 0; i < logValues.size(); i++) {

            LogValue currentLv = logValues.get(i);
            if (!fixedEgoFLag) {
                ego = CalcUtil.ego(250, maxFLux, minFLux, pipeVolume, currentLv.getRpm(), currentLv.getTps());
            } else {
                ego = fixedEgo;
            }

            logger.trace("at index:{} ego:{} time:{} rpm:{} tps:{} afr:{}", i, ego, currentLv.getTime(), currentLv.getRpm(), currentLv.getTps(), currentLv.getAfr());

            int idxt2 = i - (ego / sampleInterval);
            if (idxt2 < 1) {
                continue;
            }
            int idxt1 = idxt2 - 1;
            logger.trace("\tadjust ego:{} time:{} rpm:{} tps:{} afr:{} i:{}", ego, currentLv.getTime(), currentLv.getRpm(), currentLv.getTps(), currentLv.getAfr(), i);
            logger.trace("idxt1:{} idxt2:{}", idxt1, idxt2);
            int recalcEgo = currentLv.getTime() - ego - logValues.get(idxt1).getTime();
            logger.trace("recalc ego:{}", recalcEgo);
            double calculatedAfr = CalcUtil.afrBetweenT1andT2(logValues.get(idxt1).getUnadjustedAfr(), logValues.get(idxt2).getUnadjustedAfr(), (logValues.get(idxt1).getTime()),
                    logValues.get(idxt2).getTime(), recalcEgo);
            logger.trace("calc afr:{}", calculatedAfr);
            logValues.get(i).setEgoOffsetApplied(true);
            logValues.get(i).setAfr(calculatedAfr);
            egoCorrectionCount++;
        }


        System.out.println("#LogValues: " + logValues.size());
        System.out.println("#Egocorrection: " + egoCorrectionCount);

    }

    public List<LogValue> applyEgo(List<LogValue> logValues, Object... args) {
        int egoCorrectionCount = 0;
        boolean fixedEgoFLag = false;
        int fixedEgo = 0;
        int ego;
        if (args.length == 1) {
            fixedEgoFLag = true;
            fixedEgo = (int) args[0];
        }
        int engineVolume = 998;
        int maxRpm = 13550;
        int pipeDiameter = 45;
        int pipeLength = 950; //
        int nCylinders = 4;

        int sampleInterval = CalcUtil.decideInterval(logValues);

        double maxFLux = CalcUtil.maxFlux(engineVolume, maxRpm);
        double pipeVolume = CalcUtil.pipeVolume(pipeDiameter, pipeLength, nCylinders);
        double minFLux = CalcUtil.minFlux(pipeVolume, 250);

        List<LogValue> values = new ArrayList<>(logValues.size());
        for (LogValue l : logValues) {
            values.add(new LogValue(l));
        }

        for (int i = 0; i < values.size(); i++) {
            LogValue currentLv = values.get(i);
            if (!fixedEgoFLag) {
                ego = CalcUtil.ego(250, maxFLux, minFLux, pipeVolume, currentLv.getRpm(), currentLv.getTps());
            } else {
                ego = fixedEgo;
            }

            logger.trace("at index:{} ego:{} time:{} rpm:{} tps:{} afr:{}", i, ego, currentLv.getTime(), currentLv.getRpm(), currentLv.getTps(), currentLv.getAfr());
            // go backwards half a second
            for (int j = 1; j < 10 && i - j >= 0; j++) {
                LogValue prevLv = null;
                if (ego <= sampleInterval) {
                    prevLv = values.get(i - j);
                } else {
                    prevLv = values.get(i - j + 1);
                }
                int timeDiff = currentLv.getTime() - prevLv.getTime();
                if (timeDiff < 0) {
                    break;
                }
                if ((timeDiff * j) < ego) {
                    continue;
                }
                int idxt2 = i - j + 1;
                int idxt1 = idxt2 - 1;
                logger.trace("\tadjust ego:" + ego + " time:" + currentLv.getTime() + " rpm:" + currentLv.getRpm() + " tps:" + currentLv.getTps() + " afr:" + currentLv.getAfr() + " i:" + i + " j:" + j);
                logger.trace("\tidxt1: " + idxt1 + " idxt2: " + idxt2);
                int recalcEgo = currentLv.getTime() - ego - values.get(idxt1).getTime();
                logger.trace("recalc ego: {}", recalcEgo);
                double calculatedAfr = CalcUtil.afrBetweenT1andT2(values.get(idxt1).getUnadjustedAfr(), values.get(idxt2).getUnadjustedAfr(), (values.get(idxt1).getTime()),
                        values.get(idxt2).getTime(), recalcEgo);
                logger.trace("calc afr: {}", calculatedAfr);
                values.get(i).setEgoOffsetApplied(true);
                values.get(i).setAfr(calculatedAfr);
                egoCorrectionCount++;
                break;
            }
        }

        //System.out.println("#LogValues: " + values.size());
        //System.out.println("#Egocorrection: " + egoCorrectionCount);

        return values;
    }

    public void dumpAfrValues(List<LogValue> logValues) {
        logValues.forEach(value -> {
            //time rpm afr gear
            if (value.getRpm() >= 13500.0) {
                System.out.print(ConsoleColors.RED_BACKGROUND);
            }

            System.out.format("% 8d", value.getTime());
            System.out.format("% 7.0f", value.getRpm());
            System.out.format("% 5.1f", value.getTps());
            System.out.format("% 5.2f", value.getAfr());
            System.out.format("% 3.1f", value.getGear());
            System.out.print(" " + value.isEgoOffsetApplied());
            System.out.println();

            if (value.getRpm() >= 13500.0) {
                System.out.print(ConsoleColors.RESET);
            }
        });
    }
}
