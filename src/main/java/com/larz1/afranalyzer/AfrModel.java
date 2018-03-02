package com.larz1.afranalyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.util.List;

@Component
class AfrModel extends AbstractTableModel {

    AdjAFRValue[][] filteredMapArray = null;

    @Autowired
    public AfrModel(AutoTuneService autoTuneService) {
        /*
        try {
            if (autoTuneService != null) {
                List<AFRValue> afrValues = autoTuneService.readAfrFile();
                autoTuneService.print(AutoTuneService.PRINT.AFR, autoTuneService.convert2Map(afrValues));
                autoTuneService.print(AutoTuneService.PRINT.COUNT, autoTuneService.convert2Map(afrValues));

                List<AFRValue> filteredAFRValues = autoTuneService.filter(afrValues);

                // convert to maparray
                filteredMapArray = autoTuneService.convert2Map(filteredAFRValues);
                autoTuneService.print(AutoTuneService.PRINT.AFR, filteredMapArray);
                autoTuneService.print(AutoTuneService.PRINT.COUNT, filteredMapArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void setFilteredMapArray(AdjAFRValue[][] filteredMapArray) {
        this.filteredMapArray = filteredMapArray;
        fireTableDataChanged();

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class<?> getColumnClass(int col) {
        if (col == 0) return Integer.class;
        if ((col > 0) && (col < 18)) return Double.class;

        throw new AssertionError("invalid column:" + col);
    }

    public int getRowCount() {
        return 13;
    }

    public int getColumnCount() {
        return 18;
    }

    public String getColumnName(int col) {
        if (col == 0) {
            return "TPS";
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

        if (filteredMapArray == null) return new Integer(0);
        return filteredMapArray[row][col - 1].getAverage();
    }
}