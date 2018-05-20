package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;

class AfrModel extends AbstractTableModel {
    private static final Logger logger = LoggerFactory
            .getLogger(AfrModel.class);

    private final static int MAX_COL = 18;
    private final static int MAX_ROW = 13;

    private AdjAFRValue[][] mapArray;
    private boolean editable = false;
    private boolean cellColorControl = false;

    public AfrModel() {
    }

    public AfrModel(boolean editable) {
        this.editable = editable;
    }

    public AfrModel(boolean editable, boolean cellColorControl) {
        this.editable = editable;
        this.cellColorControl = cellColorControl;
    }

    public void setMapArray(AdjAFRValue[][] mapArray) {
        this.mapArray = mapArray;
        AutoTuneService.setRelevantMap(mapArray);
        fireTableDataChanged();

    }

    public boolean isCellColorControl() {
        return cellColorControl;
    }

    public void setCellColorControl(boolean cellColorControl) {
        this.cellColorControl = cellColorControl;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable;
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
            return String.valueOf((col - 1) * 1000);
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

        if (mapArray == null) return 0;
        logger.trace("getValueAt() row {} col {}", row, col);
        logger.trace("getValueAt() value {}", mapArray[row][col - 1].getAverage());

        return mapArray[row][col - 1].getAverage();
    }

    public void setValueAt(Object value, int row, int col) {
        mapArray[row][col - 1] = new AdjAFRValue(new Double(value.toString()));
        fireTableCellUpdated(row, col - 1);
    }

    public AdjAFRValue[][] getMapArray() {
        return mapArray;
    }
}