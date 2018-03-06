package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.table.AbstractTableModel;

//@Component
//@Scope("prototype")
class AfrModel extends AbstractTableModel {
    private static final Logger logger = LoggerFactory
            .getLogger(AfrModel.class);

    final static int MAX_COL = 18;
    final static int MAX_ROW = 13;
    AdjAFRValue[][] mapArray;

    public AfrModel() {
    }

    public void setMapArray(AdjAFRValue[][] mapArray) {
        this.mapArray = mapArray;
        fireTableDataChanged();

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class<?> getColumnClass(int col) {
        if (col == 0) return Integer.class;
        if ((col > 0) && (col < MAX_COL)) return Double.class;

        throw new AssertionError("invalid column:" + col);
    }

    public int getRowCount() {
        return MAX_ROW;
    }

    public int getColumnCount() {
        return MAX_COL;
    }

    public String getColumnName(int col) {
        if (col == 0) {
            return "TPS/RPM";
        }

        if ((col > 0) && (col <= 13)) {
            return "" + (col - 1) * 1000;
        }

        switch (col) {
            case 14:
                return "12500";
            case 15:
                return "13000";
            case 16:
                return "13500";
            case 17:
                return "14000";

        }

        throw new AssertionError("invalid column:" + col);
    }

    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return AutoTuneService.tpsArray[row];
        }

        if (mapArray == null) return new Integer(0);
        logger.trace("getValueAt() row {} col {}", row, col);
        logger.trace("getValueAt() value {}", mapArray[row][col - 1].getAverage());
        return mapArray[row][col - 1].getAverage();
    }

    public AdjAFRValue[][] getMapArray() {
        return mapArray;
    }


}