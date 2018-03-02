package com.larz1.afranalyzer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import static com.larz1.afranalyzer.CalcUtil.findIndex;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
@Service
public class AutoTuneService {
    private static final Logger logger = LoggerFactory.getLogger(AutoTuneService.class);

    public enum PRINT {
        AFR,
        COUNT
    }

    static double[]  tpsArray = {0.0, 0.8, 2.3, 4.7, 7.8, 10.2, 14.8, 20.3, 29.7, 39.8, 50.0, 75.0, 100.0};
    //double[] tpsArray = {0.0, 2.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.9, 45.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0};
    static double[] rpmArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 12500.0, 13000.0, 13500.0, 14000.0};

    @Value("${afr.file:data/almeria-before.csv}")
    private String afrFile;

    /*
    @Value("${targetafr.file}")
    private String targetAfrFile;
    */

    @Value("${printafr:true}")
    private String printAfr;

    @Value("${filterMinMax:true}")
    private String filterMinMax;

    @Value("${minafr:10.0}")
    private Double minAfr;

    @Value("${maxafr:17.1}")
    private Double maxAfr;

    @Value("${filterMinCoolantTemp:true}")
    private String filterMinCoolantTemp;

    @Value("${MinCoolantTemp:80}")
    private Integer minCoolantTemp;


    public AutoTuneService() {
    }

    public void print(PRINT print, AdjAFRValue[][] mArr) {
        Boolean[][] bArr = new Boolean[tpsArray.length][rpmArray.length];
        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                bArr[i][j] = new Boolean(false);
            }
        }
        for (int i = 5; i < 8; i++) {
            bArr[1][i] = new Boolean(true);
        }
        for (int i = 5; i < 8; i++) {
            bArr[2][i] = new Boolean(true);
        }
        for (int i = 5; i < 9; i++) {
            bArr[3][i] = new Boolean(true);
        }
        for (int i = 5; i < 9; i++) {
            bArr[4][i] = new Boolean(true);
        }
        for (int i = 5; i < 10; i++) {
            bArr[5][i] = new Boolean(true);
        }
        for (int i = 5; i < 10; i++) {
            bArr[6][i] = new Boolean(true);
        }
        for (int i = 5; i < 10; i++) {
            bArr[7][i] = new Boolean(true);
        }
        for (int i = 5; i < 11; i++) {
            bArr[8][i] = new Boolean(true);
        }
        for (int i = 5; i < 12; i++) {
            bArr[9][i] = new Boolean(true);
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[10][i] = new Boolean(true);
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[11][i] = new Boolean(true);
        }
        for (int i = 5; i < rpmArray.length; i++) {
            bArr[12][i] = new Boolean(true);
        }

        // print header
        /*
        System.out.print("      ");
        for (int j = 2; j < rpmArray.length - 1; j++) {
            System.out.format("% 7d", j);
        }
        System.out.println();
        System.out.print("      ");
*/
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
    public List<AFRValue> readAfrFile() throws IOException {
        return readAfrFile("data/almeria-before.csv");
    }

    public List<AFRValue> readAfrFile(String file) throws IOException {
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
        ObjectReader oReader = mapper.readerFor(AFRValue.class).with(schema);

        // read from file
        LinkedList<AFRValue> afrValues = new LinkedList<AFRValue>();
        Reader reader = new FileReader(new File(file));
        MappingIterator<AFRValue> mi = oReader.readValues(reader);
        while (mi.hasNext()) {
            afrValues.add(mi.next());
        }

        return afrValues;
    }

    public void calculateTarget() {

    }

    List<AFRValue> filter(List<AFRValue> dataList) {
        final int[] prevGear = {6};
        final int[] gear = {0};
        final int[] i = {0};
        dataList.forEach(data -> {
            gear[0] = (int) data.ZX_GEAR;
            if (gear[0] > prevGear[0]) {
                //println "upshift from " + prevGear + " to " + gear
                // filter out to prev values
                dataList.get(i[0] - 2).skip = true;
                dataList.get(i[0] - 1).skip = true;
                //println "Filtering out: " + dataList.get(i - 2).lcc_afr + " " + dataList.get(i - 1).lcc_afr
                logger.trace("filtering out afr:{} afr:{} time:{} due to upshift", dataList.get(i[0] - 2).LLC_AFR, dataList.get(i[0] - 1).LLC_AFR, data.Time);
            } else if ((data.LLC_AFR < minAfr) || (data.LLC_AFR > maxAfr)) {
                data.skip = true;
                logger.trace("filtering out afr {} value out of range, tps:{} time:{}", data.LLC_AFR, data.ZX_TPS, data.Time);
            } else if (data.ZX_RPM < 500) {
                data.skip = true;
                logger.trace("filtering out afr {} value,RPM lower than 500", data.LLC_AFR);
            } else if (data.ZX_GEAR == 0.0) {
                data.skip = true;
                logger.trace("filtering out afr {} value, gear is 0(neutral)", data.LLC_AFR);
            } else if ((data.ZX_GEAR != 1.0) && (data.ZX_GEAR != 2.0) && (data.ZX_GEAR != 3.0) && (data.ZX_GEAR != 4.0) && (data.ZX_GEAR != 5.0) && (data.ZX_GEAR != 6.0)) {
                data.skip = true;
                logger.trace("filtering out afr {} value, gear {} is rubbish, time: {}", data.LLC_AFR, data.ZX_GEAR, data.Time);
            } else if (findIndex(rpmArray, data.ZX_RPM, 0.25D) == null) {
                data.skip = true;
                logger.trace("filtering out afr {} value, rpm {} is outside tolerance {}, time: {}", data.LLC_AFR, data.ZX_RPM, 0.25D, data.Time);
            }

            prevGear[0] = gear[0];
            i[0]++;
        });

        return dataList;
    }


    AdjAFRValue[][] convert2Map(List<AFRValue> afrValues) {
        AdjAFRValue[][] mArr = new AdjAFRValue[tpsArray.length][rpmArray.length];
        for (int i = 0; i < tpsArray.length; i++) {
            for (int j = 0; j < rpmArray.length; j++) {
                mArr[i][j] = new AdjAFRValue();
            }
        }

        afrValues.forEach(value -> {
            if (!value.skip) {
                // find tps index
                Integer tpsInd = findIndex(tpsArray, value.getZX_TPS());
                Integer rpmInd = findIndex(rpmArray, value.getZX_RPM());
                if (mArr[tpsInd][rpmInd] == null) {
                    mArr[tpsInd][rpmInd] = new AdjAFRValue();
                }
                mArr[tpsInd][rpmInd].incAverage(value.getLLC_AFR());
                mArr[tpsInd][rpmInd].addAFRValue(value);
            }
        });

        return mArr;
    }

    public void dumpAfrValues(List<AFRValue> afrValues) {
        afrValues.forEach(value -> {
            //time rpm afr gear
            //System.out.println(value.Time + " " + value.ZX_RPM + " " + value.getLLC_AFR() + value.ZX_GEAR);
            if (value.ZX_RPM >= 13500.0) {
                System.out.print(ConsoleColors.RED_BACKGROUND);
            }

            System.out.format("% 6.1f", value.Time);
            System.out.format("% 7.0f", value.ZX_RPM);
            System.out.format("% 5.1f", value.ZX_TPS);
            System.out.format("% 5.1f", value.getLLC_AFR());
            System.out.format("% 3.1f", value.ZX_GEAR);
            System.out.println();

            if (value.ZX_RPM >= 13500.0) {
                System.out.print(ConsoleColors.RESET);
            }


        });

    }

    public void process() throws IOException {
        List<AFRValue> rawAfrValues = null;
        List<AFRValue> targetAfrValues = null;

        if (!afrFile.isEmpty()) {
            rawAfrValues = readAfrFile(afrFile);
        }

        //dumpAfrValues(rawAfrValues);

        /*
        if (!targetAfrFile.isEmpty()) {
            targetAfrValues = readAfrFile(targetAfrFile);
        }
        */
        AdjAFRValue[][] origMapArray = convert2Map(rawAfrValues);

        // filter out irellevant data
        List<AFRValue> filteredAFRValues = filter(rawAfrValues);

        // convert to maparray
        AdjAFRValue[][] filteredMapArray = convert2Map(filteredAFRValues);

        // calculate target values
        calculateTarget();

        if ("true".equals(printAfr)) {
            //printAfr(origMapArray);
            print(PRINT.AFR, origMapArray);
            print(PRINT.AFR, filteredMapArray);
            print(PRINT.COUNT, filteredMapArray);
        }
    }
}
