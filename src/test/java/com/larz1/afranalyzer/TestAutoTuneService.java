package com.larz1.afranalyzer;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestAutoTuneService {
    private double d = 0.001;

    @Test
    public void testTull() {
        int i = 243;
        int resten = i % 50;
        System.out.println("resten:" + resten);
        System.out.println("helt:" + i/50);
    }


    @Test
    public void testApplyEgo50Hz() {
        AutoTuneService as = new AutoTuneService();
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
        lvs = as.applyEgo(lvs);
        as.dumpAfrValues(lvs);

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
        AutoTuneService as = new AutoTuneService();
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = as.applyEgo(lvs);
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
        AutoTuneService as = new AutoTuneService();
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = as.applyEgo(lvs, 50);
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
        AutoTuneService as = new AutoTuneService();
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        //as.dumpAfrValues(lvs);
        lvs = as.applyEgo(lvs, 150);
        as.dumpAfrValues(lvs);

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
        AutoTuneService as = new AutoTuneService();
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

        as.dumpAfrValues(lvs);
        lvs = as.applyEgo(lvs);
        as.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertFalse(lvs.get(2).isEgoOffsetApplied());
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertTrue(lvs.get(5).isEgoOffsetApplied());
    }

}
