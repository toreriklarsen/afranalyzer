package com.larz1.afranalyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AdjAFRValue {
    private double average;
    private int count = 0;
    private List<LogValue> afrList = new ArrayList<>();

    public AdjAFRValue() {
    }

    public AdjAFRValue(double average) {
        this.average = average;
        count = 1;
    }

    private void incAverage(double newVal) {
        average += (newVal - average) / (double) ++count;
    }

    public double getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }

    public void addAFRValue(LogValue value) {
        afrList.add(value);
        incAverage(value.getAfr());
    }

    public void clear() {
        this.count = 0;
        this.afrList.clear();
    }

    public void calculateAverage() {
        this.average = CalcUtil.calculateAverage(afrList);
        this.count = afrList.size();
    }

    @Override
    public String toString() {
        return "AdjAFRValue{" +
                "average=" + average +
                ", count=" + count +
                //", afrList=" + afrList +
                '}';
    }
}
