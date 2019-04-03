package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class LogModel extends AbstractTableModel {
    private static final Logger logger = LoggerFactory
            .getLogger(LogModel.class);

    private final static int MAX_COL = 6;


    private List<LogValue> logValues;

    public LogModel() {
        this.logValues = new ArrayList<LogValue>(0);
    }

    public LogModel(List<LogValue> logValues) {
        this.logValues = logValues;
    }

    public Class<?> getColumnClass(int col) {
        if ((col == 0) || (col == 2)) return Integer.class;
        if ((col > 0) && (col < MAX_COL)) return Double.class;

        throw new AssertionError("invalid column:" + col);
    }

    public int getRowCount() {
        return logValues.size();
    }

    public int getColumnCount() {
        return MAX_COL;
    }

    //LogValue{time=65200, rpm=10512, tps=64.0, gear=3.0, afr=14.691260494949494, ect=0.0, lonacc=0.65685, skip=false, egoOffsetApplied=true, egoOffset=110, unadjustedAfr=14.724721, lineNr=653}
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Time";
            case 1:
                return "Afr";
            case 2:
                return "Rpm";
            case 3:
                return "Tps";
            case 4:
                return "Gear";
            case 5:
                return "LonAcc";
        }

        throw new AssertionError("invalid column:" + col);
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return logValues.get(row).getTime();
            case 1:
                return logValues.get(row).getAfr();
            case 2:
                return logValues.get(row).getRpm();
            case 3:
                return logValues.get(row).getTps();
            case 4:
                return logValues.get(row).getGear();
            case 5:
                return logValues.get(row).getLonacc();
        }

        throw new AssertionError("invalid column:" + col);
    }

    public void setLogValues(List<LogValue> logValues) {
        this.logValues = logValues;
        fireTableDataChanged();
    }
}