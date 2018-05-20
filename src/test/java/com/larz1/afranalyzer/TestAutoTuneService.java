package com.larz1.afranalyzer;


import com.larz1.afranalyzer.filter.*;
import com.larz1.afranalyzer.ui.StatusBar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class TestAutoTuneService {
    @TestConfiguration
    static class AutotuneServiceTestContextConfiguration {

        @Bean
        public AutoTuneService autoTuneService() {
            return new AutoTuneService();
        }

        @Bean
        public AfrAnalyzerSettings afrAnalyzerSettings() {
            return new AfrAnalyzerSettings();
        }

        @Bean
        public MaxAfrFilter maxAfrFilter() {
            return new MaxAfrFilter(afrAnalyzerSettings());
        }

        @Bean
        public MinAfrFilter minAfrFilter() {
            return new MinAfrFilter(afrAnalyzerSettings());
        }

        @Bean
        public MinRpmFilter minRpmFilter() {
            return new MinRpmFilter(afrAnalyzerSettings());
        }

        @Bean
        public NeutralFilter neutralFilter() {
            return new NeutralFilter(afrAnalyzerSettings());
        }

        @Bean
        public MinEctFilter minEctFilter() {
            return new MinEctFilter(afrAnalyzerSettings());
        }

        @Bean
        public CellToleranceFilter  cellToleranceFilter() {
            return new CellToleranceFilter(afrAnalyzerSettings());
        }

        @MockBean
        private Status status;
    }

    @Autowired
    private AutoTuneService autoTuneService;

    @Autowired
    private AfrAnalyzerSettings afrAnalyzerSettings;

    private double d = 0.00001;

    @Test
    public void testApplyEgo50Hz() {
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(1020, 1000, 2, 12));
        lvs.add(new LogValue(1040, 1100, 4, 13));
        lvs.add(new LogValue(1060, 1200, 6, 14));
        lvs.add(new LogValue(1080, 1300, 8, 15));
        lvs.add(new LogValue(1100, 1400, 10, 16));
        lvs.add(new LogValue(1120, 1600, 12, 17));
        lvs.add(new LogValue(1140, 2000, 14.8, 18));
        lvs.add(new LogValue(1160, 13500, 100.0, 19));

        //as.dumpAfrValues(lvs);
        lvs = autoTuneService.applyEgo(lvs);
        //autoTuneService.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertFalse(lvs.get(2).isEgoOffsetApplied());
        assertFalse(lvs.get(3).isEgoOffsetApplied());
        assertFalse(lvs.get(4).isEgoOffsetApplied());
        // nb
        assertFalse(lvs.get(5).isEgoOffsetApplied());
        assertFalse(lvs.get(6).isEgoOffsetApplied());
        assertTrue(lvs.get(7).isEgoOffsetApplied());
    }

    @Test
    public void testApplyEgo() {
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = autoTuneService.applyEgo(lvs);
        //as.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertFalse(lvs.get(2).isEgoOffsetApplied());
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertTrue(lvs.get(5).isEgoOffsetApplied());
    }


    @Test
    public void testApplyEgoFixedEgo50() {
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = autoTuneService.applyEgo(lvs, 50);
        //as.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertTrue(lvs.get(1).isEgoOffsetApplied());
        assertEquals(12.5, lvs.get(1).getAfr(), d);
        assertTrue(lvs.get(2).isEgoOffsetApplied());
        assertEquals(13.5, lvs.get(2).getAfr(), d);
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertEquals(14.5, lvs.get(3).getAfr(), d);
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertEquals(15.5, lvs.get(4).getAfr(), d);
        assertTrue(lvs.get(5).isEgoOffsetApplied());
        assertEquals(16.5, lvs.get(5).getAfr(), d);
    }


    @Test
    public void testApplyEgoFixedEgo150() {
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = autoTuneService.applyEgo(lvs, 150);
        //autoTuneService.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertTrue(lvs.get(2).isEgoOffsetApplied());
        assertEquals(12.5, lvs.get(2).getAfr(), d);
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertEquals(13.5, lvs.get(3).getAfr(), d);
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertEquals(14.5, lvs.get(4).getAfr(), d);
        assertTrue(lvs.get(5).isEgoOffsetApplied());
        assertEquals(15.5, lvs.get(5).getAfr(), d);
    }

    @Test
    public void testApplyEgoMoreLaps() {
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //autoTuneService.dumpAfrValues(lvs);
        lvs = autoTuneService.applyEgo(lvs);
        //autoTuneService.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertFalse(lvs.get(2).isEgoOffsetApplied());
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertTrue(lvs.get(5).isEgoOffsetApplied());
    }
    @Test
    public void testReadAfrFile() throws IOException {
        File f = new File("src/test/resources/afrdata01.csv");
        List<LogValue> lv = autoTuneService.readAfrFile(f);
        assertEquals(0, lv.get(0).getTime());
        assertEquals(10341.040039, lv.get(0).getRpm(), d);

        assertEquals(100, lv.get(1).getTime());
        assertEquals(200, lv.get(2).getTime());
        assertEquals(300, lv.get(3).getTime());
    }


}
