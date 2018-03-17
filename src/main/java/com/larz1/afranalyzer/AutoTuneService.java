package com.larz1.afranalyzer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.larz1.afranalyzer.filter.MaxFilter;
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
import java.util.LinkedList;
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
    private MaxFilter maxFilter;

    public enum PRINT {
        AFR,
        COUNT
    }

    static double[] tpsArray = {0.0, 0.8, 2.3, 4.7, 7.8, 10.2, 14.8, 20.3, 29.7, 39.8, 50.0, 75.0, 100.0};
    //double[] tpsArray = {0.0, 2.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.9, 45.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0};
    static double[] rpmArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 12500.0, 13000.0, 13500.0, 14000.0};


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
        for (int j = 0; j < rpmArray.length; j++) {
            System.out.format("% 7d", (int) rpmArray[j]);
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
        List<LogValue> logValues = new LinkedList<>();
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

    List<LogValue> filter(List<LogValue> dataList) {
        final int[] prevGear = {6};
        final int[] gear = {0};
        final int[] i = {0};
        List<LogValue> retList = new ArrayList<>(dataList.size());


        dataList.forEach(data -> {
            gear[0] = (int) data.getZX_GEAR();
            LogValue logValue = new LogValue(data);
            data.setSkip(false);

            if (maxFilter.filter(data)) {
                logger.trace("filtering out afr value, {} > max {}, tps:{} time:{}", data.getLLC_AFR(), afrAnalyzerSettings.maxAfr, data.getZX_TPS(), data.getTime());
            } else if ((afrAnalyzerSettings.minAfrEnabled) && (data.getLLC_AFR() < afrAnalyzerSettings.minAfr)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr value, {} < max {}, tps:{} time:{}", data.getLLC_AFR(), afrAnalyzerSettings.minAfr, data.getZX_TPS(), data.getTime());
            } else if ((afrAnalyzerSettings.lowRpmEnabled) && (data.getZX_RPM() < afrAnalyzerSettings.lowRpm)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value,RPM {} < {}", data.getLLC_AFR(), data.getZX_RPM(), afrAnalyzerSettings.lowRpm);
            } else if ((afrAnalyzerSettings.neutralEnabled) && (data.getZX_GEAR() == 0.0)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value, gear is 0(neutral)", data.getLLC_AFR());
            } else if ((afrAnalyzerSettings.cellToleranceEnabled) && (findIndex(rpmArray, data.getZX_RPM(), 0.25D) == null)) {
                //logValue.setSkip(true);
                logger.trace("filtering out afr {} value, rpm {} is outside tolerance {}, time: {}", data.getLLC_AFR(), data.getZX_RPM(), 0.25D, data.getTime());
            } else if ((afrAnalyzerSettings.quickshiftEnabled) && (gear[0] > prevGear[0])) {
                // filter out to prev values
                dataList.get(i[0] - 2).setSkip(true);
                dataList.get(i[0] - 1).setSkip(true);
                //logValue.setSkip(true);
                logger.trace("filtering out afr:{} afr:{} time:{} due to upshift", dataList.get(i[0] - 2).getLLC_AFR(), dataList.get(i[0] - 1).getLLC_AFR(), data.getTime());
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
                Integer tpsInd = findIndex(tpsArray, value.getZX_TPS());
                Integer rpmInd = findIndex(rpmArray, value.getZX_RPM());
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

        // todo set in settings
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

    public void applyEgo(List<LogValue> lv) {
        // for hver verdi
        for (int i = 0; i < lv.size(); i++) {
            logger.trace(" epplyEgo logValue: {}", lv.get(i));
            // let tilbake
            for (int j = 1; j < 10 && i - j > 0; j++) {
                LogValue tmpLv = lv.get(i - j);
            }

            i++;
        }
        // let tilbake til tid < t2 - offset
        // hvis interval brytes ikke gjøre noe

    }

    public void dumpAfrValues(List<LogValue> logValues) {
        logValues.forEach(value -> {
            //time rpm afr gear
            if (value.getZX_RPM() >= 13500.0) {
                System.out.print(ConsoleColors.RED_BACKGROUND);
            }

            System.out.format("% 6.1f", value.getTime());
            System.out.format("% 7.0f", value.getZX_RPM());
            System.out.format("% 5.1f", value.getZX_TPS());
            System.out.format("% 5.1f", value.getLLC_AFR());
            System.out.format("% 3.1f", value.getZX_GEAR());
            System.out.println();

            if (value.getZX_RPM() >= 13500.0) {
                System.out.print(ConsoleColors.RESET);
            }
        });
    }
}
