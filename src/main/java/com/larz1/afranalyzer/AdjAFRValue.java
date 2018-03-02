package com.larz1.afranalyzer;

import java.util.LinkedList;
import java.util.List;

public class AdjAFRValue {
    private double average;
    private int count = 0;
    List<AFRValue> afrList = new LinkedList<>();

    void incAverage(double newVal) {
        average += (newVal - average) / (double) ++count;
    }

    public double getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }

    public void addAFRValue(AFRValue value) {
        afrList.add(value);
    }
}
