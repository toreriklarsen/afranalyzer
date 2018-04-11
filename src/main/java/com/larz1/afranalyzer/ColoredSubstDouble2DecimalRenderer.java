package com.larz1.afranalyzer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;


public class ColoredSubstDouble2DecimalRenderer implements TableCellRenderer {

    public SubstDouble2DecimalRenderer DEFAULT_RENDERER;
    private AfrAnalyzerSettings afrAnalyzerSettings;


    public ColoredSubstDouble2DecimalRenderer(AfrAnalyzerSettings afrAnalyzerSettings) {
        DEFAULT_RENDERER = new SubstDouble2DecimalRenderer(afrAnalyzerSettings.tableCellPrecision);
        this.afrAnalyzerSettings = afrAnalyzerSettings;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        Number colorControlValue;

        if (value instanceof Number) {
            TableModel colorModel = ((TpsRpmTable) table).getColorControlModel();
            if (colorModel != null) {
                colorControlValue = (Number) colorModel.getValueAt(row, column);
            } else {
                colorControlValue = (Number)value;
            }

            DEFAULT_RENDERER.numberValue = (Number) value;
            value = DEFAULT_RENDERER.nf.format(DEFAULT_RENDERER.numberValue.doubleValue());

            c.setBackground(getCellBackgroundColor(colorControlValue.doubleValue()));
            c.setForeground(getCellForegroundColor(colorControlValue.doubleValue()));
            if (column == 0) return c;
            AfrModel am = (AfrModel)colorModel;
            if (am == null) return c;
            AdjAFRValue[][] mArr = am.getMapArray();
            if (mArr == null) return c;

            if (mArr[row][column - 1].isRelevant()) {
                ((JComponent) c).setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
            }
        }

        return c;
    }

    private Color getCellBackgroundColor(double value) {
        value = value / 100;
        if (value > afrAnalyzerSettings.maxTunePercentage) {
            return new Color(255, 0, 0);
        } else if (value > 0.1 * afrAnalyzerSettings.maxTunePercentage) {
            int greenvalue = (int) ((afrAnalyzerSettings.maxTunePercentage - value) / afrAnalyzerSettings.maxTunePercentage * 255);
            return new Color(255, greenvalue, greenvalue);
        } else if ((value <= 0.1 * afrAnalyzerSettings.maxTunePercentage) && (value >= -0.1 * afrAnalyzerSettings.maxTunePercentage)) {
            return Color.white;
        } else if ((value < -0.1 * afrAnalyzerSettings.maxTunePercentage) && (value > -afrAnalyzerSettings.maxTunePercentage)) {
            int greenvalue = (int) ((afrAnalyzerSettings.maxTunePercentage + value) / afrAnalyzerSettings.maxTunePercentage * 255);
            return new Color(0, greenvalue, 255);

        } else {
            return new Color(0, 0, 255);
        }
    }

    private Color getCellForegroundColor(double value) {
        value = value / 100;

        if (value > afrAnalyzerSettings.maxTunePercentage) {
            return Color.white;
        } else if (value < (-afrAnalyzerSettings.maxTunePercentage)) {
            return Color.white;
        } else {
            return Color.black;
        }
    }
}
