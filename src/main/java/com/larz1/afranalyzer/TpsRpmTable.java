package com.larz1.afranalyzer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseEvent;

public class TpsRpmTable extends JTable {
    private TableModel colorControlModel;
    private Boolean color;
    private AfrAnalyzerSettings afrAnalyzerSettings;

    public TpsRpmTable(TableModel dm, AfrAnalyzerSettings afrAnalyzerSettings) {
        this(dm, null, false, afrAnalyzerSettings);
    }

    public TpsRpmTable(TableModel dm, TableModel colorControlModel, Boolean color, AfrAnalyzerSettings afrAnalyzerSettings) {
        super(dm);
        this.colorControlModel = colorControlModel;
        this.color = color;
        this.afrAnalyzerSettings = afrAnalyzerSettings;

        this.setFillsViewportHeight(true);
        this.setRowHeight(24);
        this.setCellSelectionEnabled(true);

        for (int i = 1; i <= 13; i++) {
            if (color) {
                this.getColumn(String.valueOf((i - 1) * 1000)).setCellRenderer(new ColoredSubstDouble2DecimalRenderer(afrAnalyzerSettings));
            } else {
                this.getColumn("" + (i - 1) * 1000).setCellRenderer(new SubstDouble2DecimalRenderer(afrAnalyzerSettings.tableCellPrecision));
            }
        }
        if (color) {
            this.getColumn("12500").setCellRenderer(new ColoredSubstDouble2DecimalRenderer(afrAnalyzerSettings));
            this.getColumn("13000").setCellRenderer(new ColoredSubstDouble2DecimalRenderer(afrAnalyzerSettings));
            this.getColumn("13500").setCellRenderer(new ColoredSubstDouble2DecimalRenderer(afrAnalyzerSettings));
            this.getColumn("14000").setCellRenderer(new ColoredSubstDouble2DecimalRenderer(afrAnalyzerSettings));
        } else {
            this.getColumn("12500").setCellRenderer(new SubstDouble2DecimalRenderer(1));
            this.getColumn("13000").setCellRenderer(new SubstDouble2DecimalRenderer(1));
            this.getColumn("13500").setCellRenderer(new SubstDouble2DecimalRenderer(1));
            this.getColumn("14000").setCellRenderer(new SubstDouble2DecimalRenderer(1));
        }
        ExcelAdapter myAd = new ExcelAdapter(this);

    }

    //Implement table cell tool tips.
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            //tip = getValueAt(rowIndex, colIndex).toString();
            tip = ((AfrModel) getModel()).getMapArray()[rowIndex][colIndex - 1].toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        return tip;
    }

    public TableModel getColorControlModel() {
        return colorControlModel;
    }
}

