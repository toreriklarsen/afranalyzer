package com.larz1.afranalyzer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CalcUtil {
    public final static double calculateAverage(List<AFRValue> afrs) {
        double totalAfr = 0.0d;
        double totalCount = 0.0d;
        double avgAfr = 0.0d;
        double stdDev = 0.0d;
        double mean = 0.0d;
        int groupNumber = 1;
        //
        double AutoTuneCellStdDev = 1.5d;
        int AutoTuneTimeWindow = 500;

        if (afrs.size() == 1) {
            return afrs.get(0).LLC_AFR;
        }

        mean = calculateMean(afrs);
        stdDev = calculateStdDeviation(afrs, mean);

        List<AFRValue> timeWindowValues = new LinkedList<>();
        double timeWindowAvg = 0.0d;

        for (AFRValue afrVal : afrs) {
            if ((afrVal.LLC_AFR >= mean - stdDev * AutoTuneCellStdDev) && (afrVal.LLC_AFR <= mean + stdDev * AutoTuneCellStdDev)) {
                if (timeWindowValues.size() == 0) {
                    timeWindowValues.add(afrVal);
                }

                int timeDifference = (int) (afrVal.Time - timeWindowValues.get(0).Time) * 1000;
                if ((timeDifference < AutoTuneTimeWindow) && (timeDifference >= 0)) {
                    if (!timeWindowValues.contains(afrVal)) {
                        timeWindowValues.add(afrVal);
                    }
                } else {
                    timeWindowAvg = calculateMean(timeWindowValues);
                    timeWindowValues.clear();
                    timeWindowValues.add(afrVal);

                    totalAfr += timeWindowAvg;
                    totalCount++;
                }
            }
        }

        if (timeWindowValues.size() > 0) {
            timeWindowAvg = calculateMean(timeWindowValues);
            totalAfr += timeWindowAvg;
            totalCount++;
        }

        if (totalCount > 0) {
            avgAfr = totalAfr / totalCount;
        }


        return avgAfr;
    }


    public static double calculateStdDeviation(List<AFRValue> afrs, double mean) {
        double deviation = 0d;

        if ((afrs == null) || (afrs.size() == 0)) {
            return deviation;
        }

        for (AFRValue val : afrs) {
            deviation += Math.pow(mean - val.LLC_AFR, 2);
        }
        return Math.sqrt(deviation / (afrs.size() - 1));
    }

    public static double calculateMean(List<AFRValue> afrs) {
        double sum = 0;
        for (AFRValue val : afrs) {
            sum += val.LLC_AFR;
        }
        return sum / afrs.size();
    }

    public static Integer findIndex(final double[] array, final double value) {
        return findIndex(array, value, 1.0D);
    }


    public static Integer findIndex(final double[] array, final double value, final double factor) {
        Integer i = null;
        int idx = Arrays.binarySearch(array, value);
        if (idx < 0) {
            idx = -idx - 1;
            if (idx == 0 || idx >= array.length) {
                // Do nothing. This point is outside the array bounds
            } else {
                // Find nearest point
                double d0 = Math.abs(array[idx - 1] - value);
                double d1 = Math.abs(array[idx] - value);
                //i = (d0 <= d1) ? idx - 1 : idx;
                double length = array[idx] - array[idx - 1];
                if (value <= array[idx] - (length * (1.0D - factor))) {
                    i = idx -1;
                } else if (value > array[idx] -(length * factor)) {
                    i = idx;
                }
            }
        } else {
            i = idx;
        }

        return i;
    }
}
