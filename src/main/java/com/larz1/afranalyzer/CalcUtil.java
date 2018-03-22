package com.larz1.afranalyzer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CalcUtil {
    public final static double calculateAverage(List<LogValue> logValues) {
        double totalAfr = 0.0;
        double totalCount = 0;
        double avgAfr = 0.0;
        double stdDev;
        double mean;
        int groupNumber = 1;
        //
        // todo move into settings
        double AutoTuneCellStdDev = 1.5d;
        int AutoTuneTimeWindow = 500;

        if (logValues.size() == 1) {
            return logValues.get(0).getAfr();
        }

        mean = calculateMean(logValues);
        stdDev = calculateStdDeviation(logValues, mean);

        List<LogValue> timeWindowValues = new LinkedList<>();
        double timeWindowAvg;

        for (LogValue afrVal : logValues) {
            if ((afrVal.getAfr() >= mean - stdDev * AutoTuneCellStdDev) && (afrVal.getAfr() <= mean + stdDev * AutoTuneCellStdDev)) {
                if (timeWindowValues.size() == 0) {
                    timeWindowValues.add(afrVal);
                }

                int timeDifference = afrVal.getTime() - timeWindowValues.get(0).getTime();
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


    public static double calculateStdDeviation(List<LogValue> afrs, double mean) {
        double deviation = 0d;

        if ((afrs == null) || (afrs.size() == 0)) {
            return deviation;
        }

        for (LogValue val : afrs) {
            deviation += Math.pow(mean - val.getAfr(), 2);
        }
        return Math.sqrt(deviation / (afrs.size() - 1));
    }

    public static double calculateMean(List<LogValue> afrs) {
        double sum = 0;
        for (LogValue val : afrs) {
            sum += val.getAfr();
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
                    i = idx - 1;
                } else if (value > array[idx] - (length * factor)) {
                    i = idx;
                }
            }
        } else {
            i = idx;
        }

        return i;
    }

    /**
     * @param engineVolume in ccm
     * @param maxRpm
     * @return
     */
    public static double maxFlux(double engineVolume, int maxRpm) {
        return (engineVolume / 2) * (maxRpm / 60);
    }

    // Er det en feil her, arealet diameter TODO NNBNBNB
    //' cc = cm3 =  (pipe cross section 10 cm2) * 4 * (pipe length 100 cm) = 4000
    //Dim gasvolume As Double = Math.PI * ((My.Settings.AutoTuneExhaustGasOffsetHeaderPipeDiameter - 2) / 2 / 10) ^ 2 * 4 * My.Settings.AutoTuneExhaustGasOffsetHeaderPipeLength / 10

    /**
     * @param pipeDiameter - cm
     * @param pipeLength   - cm
     * @param ncylinders
     * @return
     */
    public static double pipeVolume(double pipeDiameter, double pipeLength, int ncylinders) {
        return Math.PI * Math.pow(pipeDiameter / 2, 2) * ncylinders * pipeLength;
        //return pipeCrossSection * ncylinders * pipeLength;
    }

    /**
     * @param pipeVolume in ccm
     * @param maxEgo
     * @return
     */
    public static double minFlux(double pipeVolume, int maxEgo) {
        return (pipeVolume * 1000.0) / maxEgo;
    }

    /**
     * Calculate the exhaust gas offset (ego)
     *
     * @param maxEgo     - in ms
     * @param maxFlux
     * @param minFlux
     * @param pipeVolume - in cc
     * @param rpm        - current rpm
     * @param tps        - current tps 0 - 1
     * @return Exhaust gas offset in ms
     */
    public static int ego(int maxEgo, double maxFlux, double minFlux, double pipeVolume, double rpm, double tps) {
        if ((rpm < 500) || (tps < 0.1)) return 0;
        double partialFlux = maxFlux * (rpm / maxEgo) * (tps / 100.0);

        return (int)(pipeVolume * 1000.0 / (partialFlux + minFlux));
    }

    /**
     * Calculate new afr between t1 and t2
     *
     * @param afrT1 - afr
     * @param afrT2 - afr
     * @param t1    - in ms
     * @param t2    - in ms
     * @param ego   - exhaust gas offset in ms
     * @return
     */
    public static double afrBetweenT1andT2(double afrT1, double afrT2, int t1, int t2, int ego) {
        double fx = (afrT2 - afrT1) / (t2 - t1);
        return (afrT1 + (fx * (t2 - t1 - ego)));
    }
}
