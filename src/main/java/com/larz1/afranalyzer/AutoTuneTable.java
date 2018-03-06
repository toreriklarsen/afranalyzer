package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Demo of the basic features of {@code JTable} printing.
 * Allows the user to configure a couple of options and print
 * a table of student grades.
 * <p>
 * Requires the following other files:
 * <ul>
 * <li>images/passed.png
 * <li>images/failed.png
 * </ul>
 *
 * @author Shannon Hickey
 */
@Controller
public class AutoTuneTable extends JFrame {
    private static final Logger logger = LoggerFactory
            .getLogger(AutoTuneTable.class);

    @Autowired
    private AfrAnalyzerSettings afrAnalyzerSettings;

    //@Autowired
    //private AfrModel afrModel;
    @Autowired
    private AutoTuneService autoTuneService;
    //@Autowired
    private AfrModel afrModel;
    private AfrModel filteredAfrModel;
    private AfrModel targetAfrModel;
    private AfrModel compAfrModel;
    private AfrModel egoModel;


    /* UI Components */
    private JFileChooser afrFileChooser = new JFileChooser();
    private JButton afrLoadButton;
    private JButton afrFileSelectButton;
    private JPanel contentPane;
    private JLabel afrLabel;
    private JTable afrTable;
    private JTable filteredAfrTable;
    private JTable targetAfrTable;
    private JTable compAfrTable;
    private JTable egoTable;

    private JScrollPane afrTableScroll;
    private JScrollPane filteredAfrTableScroll;
    private JScrollPane targetAfrTableScroll;
    private JScrollPane compAfrTableScroll;
    private JScrollPane egoTableScroll;

    private JCheckBox quickShiftBox;
    private JCheckBox neutralBox;
    private JCheckBox fitWidthBox;
    private JButton printButton;

    private JTabbedPane tabbedPane;


    /* Protected so that they can be modified/disabled by subclasses */
    protected JCheckBox maxAfrBox;
    protected JCheckBox minAfrBox;
    protected JTextField minAfrField;
    protected JTextField maxAfrField;

    List<AFRValue> rawAfrValues;
    List<AFRValue> filteredAfrValues;

    private void loadRawData() {
        logger.debug("load raw data");
        try {
            AdjAFRValue[][] targetMapArray = autoTuneService.readTargetAfrFile();
            autoTuneService.print(AutoTuneService.PRINT.AFR, targetMapArray, "Target");
            targetAfrModel.setMapArray(targetMapArray);

            if ((rawAfrValues == null) || (rawAfrValues.isEmpty())) {
                rawAfrValues = autoTuneService.readAfrFile();
            }
            AdjAFRValue[][] rawMapArray = autoTuneService.convert2Map(rawAfrValues);
            autoTuneService.print(AutoTuneService.PRINT.AFR, rawMapArray, "Raw");
            afrModel.setMapArray(rawMapArray);

            filteredAfrValues = autoTuneService.filter(rawAfrValues);
            AdjAFRValue[][] filteredMapArray = autoTuneService.convert2Map(filteredAfrValues);
            autoTuneService.print(AutoTuneService.PRINT.AFR, filteredMapArray, "Filtered");
            filteredAfrModel.setMapArray(filteredMapArray);

            // calculate the compensation
            AdjAFRValue[][] compMap = autoTuneService.calculateCompensation(filteredMapArray, targetMapArray);
            autoTuneService.print(AutoTuneService.PRINT.AFR, compMap, "Compensation");
            compAfrModel.setMapArray(compMap);

            // calculate the compensation
            AdjAFRValue[][] egoMap = autoTuneService.calculateEgo();
            autoTuneService.print(AutoTuneService.PRINT.AFR, egoMap, "Ego");
            egoModel.setMapArray(egoMap);

        } catch (IOException ioe) {
            // Todo NBNBNBNBNB
        }
    }

    /**
     * Constructs an instance of the demo.
     */
    @Autowired
    public AutoTuneTable(AfrAnalyzerSettings afrAnalyzerSettings) {
        super("AFRanalyzer 1.0");

        afrLoadButton = new JButton("load afr file");
        afrLoadButton.addActionListener(ae -> loadRawData());

        afrFileSelectButton = new JButton("select afr file");
        afrFileSelectButton.addActionListener(ae -> selectAfrFile());
        afrFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        FileFilter csvFilter = new FileNameExtensionFilter("CSV file", "csv");
        afrFileChooser.addChoosableFileFilter(csvFilter);

        afrLabel = new JLabel("AFRanalyzer 1.0");
        afrLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        afrModel = new AfrModel();
        filteredAfrModel = new AfrModel();
        targetAfrModel = new AfrModel();
        compAfrModel = new AfrModel();
        egoModel = new AfrModel();

        afrTable = createTable(afrModel);
        filteredAfrTable = createTable(filteredAfrModel);
        targetAfrTable = createTable(targetAfrModel);
        compAfrTable = createTable(compAfrModel, true);
        egoTable = createTable(egoModel);

        afrTableScroll = new JScrollPane(afrTable);
        filteredAfrTableScroll = new JScrollPane(filteredAfrTable);
        targetAfrTableScroll = new JScrollPane(targetAfrTable);
        compAfrTableScroll = new JScrollPane(compAfrTable);
        egoTableScroll = new JScrollPane(egoTable);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Raw afr file", afrTableScroll);
        tabbedPane.addTab("Filtered afr file", filteredAfrTableScroll);
        tabbedPane.addTab("Target afr file", targetAfrTableScroll);
        tabbedPane.addTab("Afr compensation (%)", compAfrTableScroll);
        tabbedPane.addTab("Ego (ms)", egoTableScroll);

        String tooltipText;

        tooltipText = "Max AFR filter";
        maxAfrBox = new JCheckBox("Max Afr:", afrAnalyzerSettings.maxAfrEnabled);
        maxAfrBox.addActionListener(ae -> checkMaxAfrFilter());
        maxAfrBox.setToolTipText(tooltipText);
        tooltipText = "Min AFR filter value";
        maxAfrField = new JTextField("" + afrAnalyzerSettings.maxAfr, 4);
        maxAfrField.addActionListener(ae -> maxAfrChanged());
        maxAfrField.setToolTipText(tooltipText);

        tooltipText = "Min AFR filter";
        minAfrBox = new JCheckBox("Min Afr:", afrAnalyzerSettings.minAfrEnabled);
        minAfrBox.addActionListener(ae -> checkMinAfrFilter());
        minAfrBox.setToolTipText(tooltipText);
        tooltipText = "Min AFR filter value";
        minAfrField = new JTextField("" + afrAnalyzerSettings.minAfr);
        minAfrField.setToolTipText(tooltipText);

        tooltipText = "Filter out values due to upshifting with quickshifter";
        quickShiftBox = new JCheckBox("Quick shift filter", afrAnalyzerSettings.quickshiftEnabled);
        quickShiftBox.setToolTipText(tooltipText);
        quickShiftBox.addActionListener(ae -> quickShiftFilter());


        tooltipText = "Filter out values when in neutral gear";
        neutralBox = new JCheckBox("Neutral filter", true);
        neutralBox.setToolTipText(tooltipText);
        neutralBox.addActionListener(ae -> neutralFilter());

        tooltipText = "Shrink the Table to Fit the Entire Width on a Page";
        fitWidthBox = new JCheckBox("Fit width to printed page", true);
        fitWidthBox.setToolTipText(tooltipText);

        tooltipText = "Print the Table";
        printButton = new JButton("Print");
        printButton.setToolTipText(tooltipText);

        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                printGradesTable();
            }
        });

        contentPane = new JPanel();
        addComponentsToContentPane();
        setContentPane(contentPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void selectAfrFile() {
        File file;

        int rVal = afrFileChooser.showDialog(AutoTuneTable.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = afrFileChooser.getSelectedFile();
                this.rawAfrValues = autoTuneService.readAfrFile(file);
                logger.debug("Opening afr file: {} ", file.getName());
            } catch (IOException ioe) {
                logger.warn("Error opening afr file");
            }
        } else {
            logger.debug("Cancelled by user.");
        }
    }

    /**
     * Adds to and lays out all GUI components on the {@code contentPane} panel.
     * <p>
     * It is recommended that you <b>NOT</b> try to understand this code. It was
     * automatically generated by the NetBeans GUI builder.
     */
    private void addComponentsToContentPane() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Filter"));

        GroupLayout bottomPanelLayout = new GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
                bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(maxAfrBox)
                                        .addComponent(minAfrBox))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(minAfrField)
                                        .addComponent(maxAfrField, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                                .addComponent(fitWidthBox)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(printButton))
                                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                                .addComponent(quickShiftBox)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(neutralBox)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
                bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(maxAfrBox)
                                        .addComponent(maxAfrField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(neutralBox)
                                        .addComponent(quickShiftBox))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(minAfrBox)
                                        .addComponent(minAfrField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fitWidthBox)
                                        .addComponent(printButton))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                        //.addComponent(afrLabel)
                                        .addComponent(afrFileSelectButton)
                                        .addComponent(afrLoadButton)
                                        .addComponent(bottomPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                //.addComponent(afrLabel)
                                .addComponent(afrFileSelectButton)
                                .addComponent(afrLoadButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bottomPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }

    /**
     * Create and return a table for the given model.
     * <p>
     * This is protected so that a subclass can return an instance
     * of a different {@code JTable} subclass. This is interesting
     * only for {@code TablePrintDemo3} where we want to return a
     * subclass that overrides {@code getPrintable} to return a
     * custom {@code Printable} implementation.
     */

    protected JTable createTable(TableModel model) {
        return createTable(model, false);
    }

    protected JTable createTable(TableModel model, Boolean color) {
        JTable jt = new JTable(model) {

            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    //tip = getValueAt(rowIndex, colIndex).toString();
                    tip = ((AfrModel) getModel()).mapArray[rowIndex][colIndex-1].toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
        };

        jt.setFillsViewportHeight(true);
        jt.setRowHeight(24);

        for (int i = 1; i <= 13; i++) {
            if (color) {
                jt.getColumn("" + (i - 1) * 1000).setCellRenderer(createColoredSubstDouble2DecimalRenderer());
            } else {
                jt.getColumn("" + (i - 1) * 1000).setCellRenderer(createSubstDouble2DecimalRenderer());
            }
        }
        if (color) {
            jt.getColumn("12500").setCellRenderer(createColoredSubstDouble2DecimalRenderer());
            jt.getColumn("13000").setCellRenderer(createColoredSubstDouble2DecimalRenderer());
            jt.getColumn("13500").setCellRenderer(createColoredSubstDouble2DecimalRenderer());
            jt.getColumn("14000").setCellRenderer(createColoredSubstDouble2DecimalRenderer());
        } else {
            jt.getColumn("12500").setCellRenderer(createSubstDouble2DecimalRenderer());
            jt.getColumn("13000").setCellRenderer(createSubstDouble2DecimalRenderer());
            jt.getColumn("13500").setCellRenderer(createSubstDouble2DecimalRenderer());
            jt.getColumn("14000").setCellRenderer(createSubstDouble2DecimalRenderer());
        }


        return jt;
    }

    protected TableCellRenderer createSubstDouble2DecimalRenderer() {
        return new SubstDouble2DecimalRenderer(1);
    }

    protected TableCellRenderer createColoredSubstDouble2DecimalRenderer() {
        return new ColoredSubstDouble2DecimalRenderer(1);
    }


    /**
     * Print the grades table.
     */
    private void printGradesTable() {
        /* Fetch printing properties from the GUI components */

        MessageFormat header = null;

        /* if we should print a header */
        if (maxAfrBox.isSelected()) {
            /* create a MessageFormat around the header text */
            header = new MessageFormat(maxAfrField.getText());
        }

        MessageFormat footer = null;

        /* if we should print a footer */
        if (minAfrBox.isSelected()) {
            /* create a MessageFormat around the footer text */
            footer = new MessageFormat(minAfrField.getText());
        }

        boolean fitWidth = fitWidthBox.isSelected();
        boolean showPrintDialog = quickShiftBox.isSelected();
        boolean interactive = neutralBox.isSelected();

        /* determine the print mode */
        JTable.PrintMode mode = fitWidth ? JTable.PrintMode.FIT_WIDTH
                : JTable.PrintMode.NORMAL;

        try {
            /* print the table */
            boolean complete = afrTable.print(mode, header, footer,
                    showPrintDialog, null,
                    interactive, null);

            /* if printing completes */
            if (complete) {
                /* show a success message */
                JOptionPane.showMessageDialog(this,
                        "Printing Complete",
                        "Printing Result",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                /* show a message indicating that printing was cancelled */
                JOptionPane.showMessageDialog(this,
                        "Printing Cancelled",
                        "Printing Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException pe) {
            /* Printing failed, report to the user */
            JOptionPane.showMessageDialog(this,
                    "Printing Failed: " + pe.getMessage(),
                    "Printing Result",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkMaxAfrFilter() {
        //maxAfrField.setEnabled(maxAfrBox.isSelected());
        afrAnalyzerSettings.maxAfrEnabled = maxAfrBox.isSelected();
        // rerun filter, for now reload
        loadRawData();
    }

    private void checkMinAfrFilter() {
        //minAfrField.setEnabled(minAfrBox.isSelected());
        afrAnalyzerSettings.minAfrEnabled = minAfrBox.isSelected();
        // rerun filter, for now reload
        loadRawData();
    }

    private void quickShiftFilter() {
        afrAnalyzerSettings.quickshiftEnabled = quickShiftBox.isSelected();
        // rerun filter, for now reload
        loadRawData();
    }

    private void neutralFilter() {
        afrAnalyzerSettings.neutralEnabled = neutralBox.isSelected();
        // rerun filter, for now reload
        loadRawData();
    }

    private void maxAfrChanged() {
        afrAnalyzerSettings.maxAfr = new Double(maxAfrField.getText());
        if (maxAfrBox.isEnabled()) {
            loadRawData();
        }
    }


    protected static class SubstDouble2DecimalRenderer extends DefaultTableCellRenderer {

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

        public SubstDouble2DecimalRenderer() {
            super();
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
            nf.setRoundingMode(RoundingMode.HALF_UP);
        }

        @Override
        public void setValue(Object value) {
            if ((value != null) && (value instanceof Number)) {
                numberValue = (Number) value;
                value = nf.format(numberValue.doubleValue());
            }
            super.setValue(value);
        }
    }

    protected class ColoredSubstDouble2DecimalRenderer extends SubstDouble2DecimalRenderer {
        public ColoredSubstDouble2DecimalRenderer(int p_precision) {
            super(p_precision);
        }

        @Override
        public void setValue(Object value) {
            if ((value != null) && (value instanceof Number)) {
                numberValue = (Number) value;
                value = nf.format(numberValue.doubleValue());
                setBackground(getCellBackgroundColor(new Double((String) value)));
                setForeground(getCellForegroundColor(new Double((String) value)));
            }
            super.setValue(value);
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
}