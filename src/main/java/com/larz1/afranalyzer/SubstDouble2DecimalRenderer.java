package com.larz1.afranalyzer;

import javax.swing.table.DefaultTableCellRenderer;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class SubstDouble2DecimalRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
    private int precision = 0;
    protected Number numberValue;
    protected NumberFormat nf;

    public SubstDouble2DecimalRenderer(int p_precision) {
        super();
        setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        precision = p_precision;
        nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(p_precision);
        nf.setMaximumFractionDigits(p_precision);
        nf.setRoundingMode(RoundingMode.HALF_UP);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            numberValue = (Number) value;
            value = nf.format(numberValue.doubleValue());
        }
        super.setValue(value);
    }
}

