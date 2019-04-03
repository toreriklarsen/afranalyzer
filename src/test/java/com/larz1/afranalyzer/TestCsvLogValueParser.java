package com.larz1.afranalyzer;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.*;

public class TestCsvLogValueParser {

    @Test
    public void testInstaniate() throws IOException {
        File f = new File("src/test/resources/afrdata01.csv");
        assertTrue(f.exists());
        CsvLogValueParser csvLogValueParser = new CsvLogValueParser(f);
        assertNotNull(csvLogValueParser);
        assertTrue(csvLogValueParser.parse());
        assertEquals(9, csvLogValueParser.getLogValues().size());
    }

    @Test
    public void testHeadersHappyDay() throws IOException {
        String csv = "time,rpm,tps, afr\n"
                + "1,2, 3,4\n"
                + "2,3, 4,5\n"
                + "3, 4, 5, 6\n";

        CsvLogValueParser csvLogValueParser = new CsvLogValueParser(csv);
        assertNotNull(csvLogValueParser);
        assertTrue(csvLogValueParser.parse());
        List<LogValue> lvs = csvLogValueParser.getLogValues();
        assertEquals(3, lvs.size());
        assertEquals(1, lvs.get(0).getTime());
        assertEquals(2, lvs.get(0).getRpm());
        assertEquals(3.0, lvs.get(0).getTps());
        assertEquals(4.0, lvs.get(0).getAfr());

        assertEquals(2, lvs.get(1).getTime());
        assertEquals(3, lvs.get(1).getRpm());
        assertEquals(4.0, lvs.get(1).getTps());
        assertEquals(5.0, lvs.get(1).getAfr());

        assertEquals(3, lvs.get(2).getTime());
        assertEquals(4, lvs.get(2).getRpm());
        assertEquals(5.0, lvs.get(2).getTps());
        assertEquals(6.0, lvs.get(2).getAfr());
    }


    @Test
    public void testParseTimeBug() throws IOException {
        String csv = "time,rpm,tps, afr, lonacc\n"
                + "0.000,2, 3,4, 2.2\n"
                + "0.100,3, 4,5, 3.3\n"
                + "0.200, 4, 5, 6, 4.4\n";

        CsvLogValueParser csvLogValueParser = new CsvLogValueParser(csv);
        assertNotNull(csvLogValueParser);
        assertTrue(csvLogValueParser.parse());
        List<LogValue> lvs = csvLogValueParser.getLogValues();
        assertEquals(3, lvs.size());

        assertEquals(0, lvs.get(0).getTime());
        assertEquals(2, lvs.get(0).getRpm());
        assertEquals(3.0, lvs.get(0).getTps());
        assertEquals(4.0, lvs.get(0).getAfr());
        assertEquals(2.2, lvs.get(0).getLonacc());

        assertEquals(100, lvs.get(1).getTime());
        assertEquals(3, lvs.get(1).getRpm());
        assertEquals(4.0, lvs.get(1).getTps());
        assertEquals(5.0, lvs.get(1).getAfr());

        assertEquals(200, lvs.get(2).getTime());
        assertEquals(4, lvs.get(2).getRpm());
        assertEquals(5.0, lvs.get(2).getTps());
        assertEquals(6.0, lvs.get(2).getAfr());
    }

    @Test
    public void testParseTimeBugLonAcc() throws IOException {
        String csv = "\"Time\",\"ZX_RPM\",\"ZX_TPS\",\"ZX_ECT\",\"ZX_GEAR\",\"LCC_AFR\",\"GPS_LonAcc\"\n"
                + "0.000,3976.800049,9.300000,92.000000,1.000000,14.176001,0.163295\n"
                + "0.020,4000.899902,9.300000,92.000000,1.000000,14.200001,0.152715\n";

        CsvLogValueParser csvLogValueParser = new CsvLogValueParser(csv);
        assertNotNull(csvLogValueParser);
        assertTrue(csvLogValueParser.parse());
        List<LogValue> lvs = csvLogValueParser.getLogValues();
        assertEquals(2, lvs.size());

        assertEquals(0, lvs.get(0).getTime());
        assertEquals(3976, lvs.get(0).getRpm());
        assertEquals(9.3, lvs.get(0).getTps());
        assertEquals(92.0, lvs.get(0).getEct());
        assertEquals(1.0, lvs.get(0).getGear());
        assertEquals(14.176001, lvs.get(0).getAfr());
        assertEquals(0.163295, lvs.get(0).getLonacc());

        assertEquals(20, lvs.get(1).getTime());
        assertEquals(4000, lvs.get(1).getRpm());
        assertEquals(9.3, lvs.get(1).getTps());
        assertEquals(92.0, lvs.get(1).getEct());
        assertEquals(1.0, lvs.get(1).getGear());
        assertEquals(14.200001, lvs.get(1).getAfr());
        assertEquals(0.152715, lvs.get(1).getLonacc());
    }

    @Test
    public void testParseErrorMissingTime() throws IOException {
        String csv = "\"ZX_RPM\",\"ZX_TPS\",\"ZX_ECT\",\"ZX_GEAR\",\"LCC_AFR\",\"GPS_LonAcc\"\n"
                + "3976.800049,9.300000,92.000000,1.000000,14.176001,0.163295\n"
                + "4000.899902,9.300000,92.000000,1.000000,14.200001,0.152715\n";

        CsvLogValueParser csvLogValueParser = new CsvLogValueParser(csv);
        assertFalse(csvLogValueParser.parse());
    }
}
