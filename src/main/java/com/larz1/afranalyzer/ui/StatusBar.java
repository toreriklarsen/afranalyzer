package com.larz1.afranalyzer.ui;

import com.larz1.afranalyzer.Status;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class StatusBar extends JPanel implements Status {
    private static final Logger logger = LoggerFactory.getLogger(StatusBar.class);

    private JLabel afrFile;
    private JLabel nLogValues;
    private JLabel nFilteredValues;
    private JLabel valueRate;

    public StatusBar() {
        afrFile = new JLabel();
        nLogValues = new JLabel();
        nFilteredValues = new JLabel();
        valueRate = new JLabel();

        this.setLayout(new MigLayout("fillx, insets 2 n n n", "[grow]16[][]", "[]"));

        this.add(afrFile);
        this.add(nLogValues);
        this.add(nFilteredValues);
        this.add(valueRate);

        setAfrFileName("");
        setNumlogValues(0);
        setNumFilteredValues(0);
        setResolution(0);
        //this.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    }

    public void setAfrFileName(String fName) {
        afrFile.setText("Afr file: " + fName);
    }

    public void setNumlogValues(int nvals) {
        nLogValues.setText("# logvalues: " + nvals);
    }

    public void setNumFilteredValues(int nvals) {
        nFilteredValues.setText("  # filtered values: " + nvals);
    }

    public void setResolution(int nvals) {
        valueRate.setText(" resolution: " + nvals + "ms");
    }
}
