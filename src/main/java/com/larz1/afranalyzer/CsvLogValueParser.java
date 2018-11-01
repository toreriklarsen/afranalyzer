package com.larz1.afranalyzer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvLogValueParser {
    private static final Logger logger = LoggerFactory
            .getLogger(CsvLogValueParser.class);

    private File csvFile;
    private String csv;
    private String timeColName;
    private String rpmColName;
    private String tpsColName;
    private String afrColName;
    private String ectColName;
    private String gearColName;
    private String lonAccColName;
    List<LogValue> lvs = new ArrayList<>();

    public CsvLogValueParser(String csv) {
        this.csv = csv;
    }

    public CsvLogValueParser(File csvFile) {
        this.csvFile = csvFile;
    }

    public boolean parse() throws IOException {
        boolean parseOK = false;
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> it;
        if (csv == null) {
            it = mapper.readerFor(Map.class)
                    .with(schema)
                    .readValues(csvFile);
        } else {
            it = mapper.readerFor(Map.class)
                    .with(schema)
                    .readValues(csv);
        }

        boolean foundheaders = false;
        int lineNr = 1;
        while (it.hasNext()) {
            Map<String, String> rowAsMap = it.next();
            if (!foundheaders) {
                if (!findheadings(rowAsMap)) {
                    logger.info("error in header");
                    break;
                } else {
                    foundheaders = true;
                    LogValue lv = parseLogvalue(rowAsMap);
                    lv.setLineNr(lineNr++);
                    lvs.add(lv);
                }
            } else {
                LogValue lv = parseLogvalue(rowAsMap);
                lv.setLineNr(lineNr++);
                lvs.add(lv);
                parseOK = true;
            }
        }

        return parseOK;
    }

    private LogValue parseLogvalue(Map<String, String> rowAsMap) {
        LogValue lv = new LogValue();
        String timeString = rowAsMap.get(timeColName);
        int timeval;
        if (timeString.contains(".")) {
            timeval = (int)(1000 * Double.parseDouble(timeString));
        } else {
            timeval = Integer.parseInt(timeString);
        }
        lv.setTime(timeval);
        lv.setRpm((int)Double.parseDouble(rowAsMap.get(rpmColName)));
        lv.setTps(Double.parseDouble(rowAsMap.get(tpsColName)));
        lv.setAfr(Double.parseDouble(rowAsMap.get(afrColName)));
        if (gearColName != null) {
            lv.setGear(Double.parseDouble(rowAsMap.get(gearColName)));
        }
        if (ectColName != null) {
            lv.setEct(Double.parseDouble(rowAsMap.get(ectColName)));
        }
        if (lonAccColName!= null) {
            lv.setLonacc(Double.parseDouble(rowAsMap.get(lonAccColName)));
        }

        return lv;
    }

    public List<LogValue> getLogValues() {
        return lvs;
    }

    private boolean findheadings(Map<String, String> rowAsMap) {
        for (String colName : rowAsMap.keySet()) {
            if (colName.toLowerCase().contains("time")) {
                timeColName = colName;
            } else if (colName.toLowerCase().contains("rpm")) {
                rpmColName = colName;
            } else if (colName.toLowerCase().contains("tps")) {
                tpsColName = colName;
            } else if (colName.toLowerCase().contains("afr")) {
                afrColName = colName;
            } else if (colName.toLowerCase().contains("ect")) {
                ectColName = colName;
            } else if (colName.toLowerCase().contains("gear")) {
                gearColName = colName;
            } else if (colName.toLowerCase().contains("lonacc")) {
                lonAccColName = colName;
            } else {
                logger.warn("found unknown column {}, skipping" + colName);
            }
        }

        return timeColName != null && rpmColName != null && tpsColName != null && afrColName != null;
    }
}
