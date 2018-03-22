package com.larz1.afranalyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class TestAutoTuneService {
    @Test
    void testApplyEgo() {
        AutoTuneService as = new AutoTuneService();
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        as.dumpAfrValues(lvs);
        as.applyEgo(lvs);
        as.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertFalse(lvs.get(1).isEgoOffsetApplied());
        assertFalse(lvs.get(2).isEgoOffsetApplied());
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertTrue(lvs.get(5).isEgoOffsetApplied());
    }


    @Test
    void testApplyEgoFixed() {
        AutoTuneService as = new AutoTuneService();
        List<LogValue> lvs = new ArrayList<>();
        lvs.add(new LogValue(0, 1000, 2, 12));
        lvs.add(new LogValue(100, 1100, 4, 13));
        lvs.add(new LogValue(200, 1200, 6, 14));
        lvs.add(new LogValue(300, 1300, 8, 15));
        lvs.add(new LogValue(400, 1400, 10, 16));
        lvs.add(new LogValue(500, 1600, 12, 17));

        as.dumpAfrValues(lvs);
        as.applyEgo(lvs, 50);
        as.dumpAfrValues(lvs);

        assertFalse(lvs.get(0).isEgoOffsetApplied());
        assertTrue(lvs.get(1).isEgoOffsetApplied());
        assertTrue(lvs.get(2).isEgoOffsetApplied());
        assertTrue(lvs.get(3).isEgoOffsetApplied());
        assertTrue(lvs.get(4).isEgoOffsetApplied());
        assertTrue(lvs.get(5).isEgoOffsetApplied());
    }

}
