package com.larz1.afranalyzer;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class TestCalcUtil {
    private static double[] rpmArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 12500.0, 13000.0, 13500.0, 14000.0};
    private static double[] tpsArray = {0.0, 0.8, 2.3, 4.7, 7.8, 10.2, 14.8, 20.3, 29.7, 39.8, 50.0, 75.0, 100.0};

    private double d = 0.001;


    @Test
    public void testFindIndexSpotOn() {
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 1000.0));
        assertEquals(new Integer(15), CalcUtil.findIndex(rpmArray, 13500.0));
        assertEquals(new Integer(16), CalcUtil.findIndex(rpmArray, 13750.0));
        assertEquals(new Integer(5), CalcUtil.findIndex(tpsArray, 10.2));
        assertEquals(new Integer(11), CalcUtil.findIndex(tpsArray, 75.0));
    }

    @Test
    public void testFindIndexDouble() {
        assertEquals(new Integer(0), CalcUtil.findIndex(tpsArray, 0.1));
        assertEquals(new Integer(1), CalcUtil.findIndex(tpsArray, 0.8));
        assertEquals(new Integer(2), CalcUtil.findIndex(tpsArray, 2.3));
        assertEquals(new Integer(10), CalcUtil.findIndex(tpsArray, 50.0));
    }

    @Test
    public void testFindIndexDoubleBetween() {
        //assertEquals(new Integer(1), CalcUtil.findIndex(tpsArray, 0.8));
        assertEquals(new Integer(1), CalcUtil.findIndex(tpsArray, 0.81));
        assertEquals(new Integer(1), CalcUtil.findIndex(tpsArray, 1.5));
        assertEquals(new Integer(2), CalcUtil.findIndex(tpsArray, 1.6));
    }

    @Test
    public void testFindIndexOutSide() {
        assertNull(CalcUtil.findIndex(rpmArray, -0.01));
        assertNull(CalcUtil.findIndex(rpmArray, 14001));
        assertNull(CalcUtil.findIndex(rpmArray, 14000.01));
    }

    @Test
    public void testFindIndexInSide() {
        assertEquals(new Integer(0), CalcUtil.findIndex(rpmArray, 0.0d));
    }

    @Test
    public void testFindIndexInSideMany() {
        for (double d = rpmArray[0]; d <= rpmArray[rpmArray.length - 1]; d += 0.1D) {
            assertNotNull(CalcUtil.findIndex(rpmArray, d));
        }
    }

    @Test
    public void testFindIndexWithinArea50() {
        assertEquals(new Integer(0), CalcUtil.findIndex(rpmArray, 249.0, .5));
        assertNull(CalcUtil.findIndex(rpmArray, 250.0, .5));
        assertNull(CalcUtil.findIndex(rpmArray, 251.0, .5));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 751.0, .5));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 999, .5));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 1000, .5));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 1249, .5));
        assertNull(CalcUtil.findIndex(rpmArray, 1250.0, .5));
    }

    @Test
    public void testFindIndexWithinArea25() {
        assertEquals(new Integer(0), CalcUtil.findIndex(rpmArray, 124, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 125.01, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 251.0, .5));
        assertNull(CalcUtil.findIndex(rpmArray, 14001.0D, 0.25D));
    }

    @Test
    public void testFindIndexOutSidenArea25() {
        assertNull(CalcUtil.findIndex(rpmArray, 251.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 749.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 1251.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 7749.0D, 0.25D));
    }

    @Test
    public void testCalculateMean() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(100, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(200, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(300, 1000, 50D, 1D, 2.5D));

        assertEquals(2.5D, CalcUtil.calculateMean(ali), d);
    }

    @Test
    public void testCalculateStdDeviation() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(100, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(200, 1000, 50D, 1D, 2.5D));

        assertEquals(0.0D, CalcUtil.calculateStdDeviation(ali, CalcUtil.calculateMean(ali)), d);
    }

    @Test
    public void testCalculateAverage() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(1000, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(2000, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(3000, 1000, 50D, 1D, 2.5D));

        assertEquals(2.5d, CalcUtil.calculateAverage(ali), d);
    }

    @Test
    public void testCalculateAverageOneValue() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(1000, 1000, 50D, 1D, 2.5D));
        assertEquals(2.5d, CalcUtil.calculateAverage(ali), d);
    }

    @Test
    public void testCalculateAverage3Values() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(1000, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(2000, 1000, 50D, 1D, 2.5D));
        ali.add(new LogValue(3000, 1000, 50D, 1D, 2.5D));

        assertEquals(2.5D, CalcUtil.calculateAverage(ali), d);
    }

    @Test
    public void testCalculateAverageManyValues3Wins() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(1010, 1000, 50D, 1D, 1D));
        ali.add(new LogValue(1020, 1000, 50D, 1D, 2D));
        ali.add(new LogValue(1030, 1000, 50D, 1D, 3D));
        ali.add(new LogValue(1040, 1000, 50D, 1D, 4D));
        ali.add(new LogValue(1050, 1000, 50D, 1D, 5D));

        assertEquals(3D, CalcUtil.calculateMean(ali), d);
        assertEquals(3D, CalcUtil.calculateAverage(ali), d);

        ali.add(new LogValue(4010, 1000, 50D, 1D, 1D));
        ali.add(new LogValue(4020, 1000, 50D, 1D, 2D));
        ali.add(new LogValue(4030, 1000, 50D, 1D, 3D));
        ali.add(new LogValue(4040, 1000, 50D, 1D, 4D));
        ali.add(new LogValue(4050, 1000, 50D, 1D, 5D));

        ali.add(new LogValue(8010, 1000, 50D, 1D, 1D));
        ali.add(new LogValue(8020, 1000, 50D, 1D, 2D));
        ali.add(new LogValue(8030, 1000, 50D, 1D, 3D));
        ali.add(new LogValue(8040, 1000, 50D, 1D, 4D));
        ali.add(new LogValue(8050, 1000, 50D, 1D, 5D));

        assertEquals(3D, CalcUtil.calculateAverage(ali), d);

        assertEquals(3D, CalcUtil.calculateMean(ali), d);
    }

    @Test
    public void testMaxFluxSimple() {
        assertEquals(50000.0, CalcUtil.maxFlux(1000, 6000), d);
        assertEquals(100000.0, CalcUtil.maxFlux(1000, 12000), d);
    }

    @Test
    public void testAfrBetweenT1andT2() {
        assertEquals(12, CalcUtil.afrBetweenT1andT2(12, 12, 100, 200, 50), d);
        assertEquals(12.5, CalcUtil.afrBetweenT1andT2(12, 13, 100, 200, 50), d);
        assertEquals(13, CalcUtil.afrBetweenT1andT2(12, 14, 100, 200, 50), d);
        assertEquals(14, CalcUtil.afrBetweenT1andT2(12, 14, 100, 200, 0), d);
        assertEquals(12, CalcUtil.afrBetweenT1andT2(12, 14, 100, 200, 100), d);
    }


    @Test
    public void testDecideInterval() {
        List<LogValue> ali = new ArrayList<>();
        ali.add(new LogValue(1010, 1000, 50D, 1D, 1D));
        ali.add(new LogValue(1020, 1000, 50D, 1D, 2D));
        ali.add(new LogValue(1030, 1000, 50D, 1D, 3D));
        ali.add(new LogValue(1040, 1000, 50D, 1D, 4D));
        ali.add(new LogValue(1050, 1000, 50D, 1D, 5D));

        assertEquals(10, CalcUtil.decideInterval(ali));
    }

}
