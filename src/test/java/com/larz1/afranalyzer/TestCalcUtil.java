package com.larz1.afranalyzer;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestCalcUtil {
    private static double[] rpmArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 12500.0, 13000.0, 13500.0, 14000.0};

    @Test
    void testFindIndexOutSide() {
        assertNull(CalcUtil.findIndex(rpmArray, -0.01));
        assertNull(CalcUtil.findIndex(rpmArray, 14001));
        assertNull(CalcUtil.findIndex(rpmArray, 14000.01));
    }

    @Test
    void testFindIndexInSide() {
        assertEquals(new Integer(0), CalcUtil.findIndex(rpmArray, 0.0d));
    }

    @Test
    void testFindIndexInSideMany() {
        for (double d = rpmArray[0]; d <= rpmArray[rpmArray.length - 1]; d += 0.1D) {
            assertNotNull(CalcUtil.findIndex(rpmArray, d));
        }
    }

    @Test
    void testFindIndexWithinArea25() {
        assertEquals(new Integer(0), CalcUtil.findIndex(rpmArray, 249.0D, 0.25D));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 751.0D, 0.25D));
        assertEquals(new Integer(1), CalcUtil.findIndex(rpmArray, 1249.0D, 0.25D));
        assertEquals(new Integer(2), CalcUtil.findIndex(rpmArray, 1751.0D, 0.25D));

        assertEquals(new Integer(12), CalcUtil.findIndex(rpmArray, 12010.0D, 0.25D));
        assertEquals(new Integer(12), CalcUtil.findIndex(rpmArray, 12125.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 12126.0D, 0.25D));


        assertNull(CalcUtil.findIndex(rpmArray, 14001.0D, 0.25D));
    }

    @Test
    void testFindIndexOutSidenArea25() {
        assertNull(CalcUtil.findIndex(rpmArray, 251.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 749.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 1251.0D, 0.25D));
        assertNull(CalcUtil.findIndex(rpmArray, 7749.0D, 0.25D));
    }

    @Test
    void testCalculateMean() {
        List<AFRValue> ali = new LinkedList<>();
        ali.add(new AFRValue(1.0D, 1000D, 50D, 1D, 2.5D));
        ali.add(new AFRValue(2.0D, 1000D, 50D, 1D, 2.5D));
        ali.add(new AFRValue(3.0D, 1000D, 50D, 1D, 2.5D));

        assertEquals(2.5D, CalcUtil.calculateMean(ali), "error calc mean");
    }

    @Test
    void testCalculateStdDeviation() {
        List<AFRValue> ali = new LinkedList<>();
        ali.add(new AFRValue(1.0D, 1000D, 50D, 1D, 2.5D));
        ali.add(new AFRValue(2.0D, 1000D, 50D, 1D, 2.5D));

        assertEquals(0.0D, CalcUtil.calculateStdDeviation(ali, CalcUtil.calculateMean(ali)), "error calc mean");
    }

    @Test
    void testCalculateAverage() {
        List<AFRValue> ali = new LinkedList<>();
        assertEquals(0.0d, CalcUtil.calculateAverage(ali));
    }

    @Test
    void testCalculateAverageOneValue() {
        List<AFRValue> ali = new LinkedList<>();
        ali.add(new AFRValue(1.0D, 1000D, 50D, 1D, 2.5D));
        assertEquals(2.5d, CalcUtil.calculateAverage(ali));
    }

    @Test
    void testCalculateAverage3Values() {
        List<AFRValue> ali = new LinkedList<>();
        ali.add(new AFRValue(1.0D, 1000D, 50D, 1D, 2.5D));
        ali.add(new AFRValue(2.0D, 1000D, 50D, 1D, 2.5D));
        ali.add(new AFRValue(3.0D, 1000D, 50D, 1D, 2.5D));

        assertEquals(2.5D, CalcUtil.calculateAverage(ali), "error calc mean");
    }

    @Test
    void testCalculateAverageManyValues3Wins() {
        List<AFRValue> ali = new LinkedList<>();
        ali.add(new AFRValue(1.01D, 1000D, 50D, 1D, 1D));
        ali.add(new AFRValue(1.02D, 1000D, 50D, 1D, 2D));
        ali.add(new AFRValue(1.03D, 1000D, 50D, 1D, 3D));
        ali.add(new AFRValue(1.04D, 1000D, 50D, 1D, 4D));
        ali.add(new AFRValue(1.05D, 1000D, 50D, 1D, 5D));

        assertEquals(3D, CalcUtil.calculateMean(ali), "error calc mean");
        assertEquals(3D, CalcUtil.calculateAverage(ali), "error calc mean");

        ali.add(new AFRValue(4.01D, 1000D, 50D, 1D, 1D));
        ali.add(new AFRValue(4.02D, 1000D, 50D, 1D, 2D));
        ali.add(new AFRValue(4.03D, 1000D, 50D, 1D, 3D));
        ali.add(new AFRValue(4.04D, 1000D, 50D, 1D, 4D));
        ali.add(new AFRValue(4.05D, 1000D, 50D, 1D, 5D));

        ali.add(new AFRValue(8.01D, 1000D, 50D, 1D, 1D));
        ali.add(new AFRValue(8.02D, 1000D, 50D, 1D, 2D));
        ali.add(new AFRValue(8.03D, 1000D, 50D, 1D, 3D));
        ali.add(new AFRValue(8.04D, 1000D, 50D, 1D, 4D));
        ali.add(new AFRValue(8.05D, 1000D, 50D, 1D, 5D));

        assertEquals(3D, CalcUtil.calculateAverage(ali), "error calc mean");
        assertEquals(3D, CalcUtil.calculateMean(ali), "error calc mean");
    }

}
