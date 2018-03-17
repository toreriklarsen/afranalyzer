package com.larz1.afranalyzer;

import net.miginfocom.swing.MigLayout;
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
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;


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
    private AfrModel cpModel;
    private AfrModel egoModel;


    /* UI Components */
    private JFileChooser afrFileChooser;
    private JFileChooser targetAfrFileChooser;

    // button for test, select both files for convinience
    private JButton testFileButton;
    private JButton afrFileSelectButton;
    private JButton targetAfrFileSelectButton;


    private JButton calculateButton;

    private JPanel contentPane;

    private JTable afrTable;
    private JTable filteredAfrTable;
    private JTable targetAfrTable;
    private JTable compAfrTable;
    private JTable cpTable;
    private JTable egoTable;

    private JScrollPane afrTableScroll;
    private JScrollPane filteredAfrTableScroll;
    private JScrollPane targetAfrTableScroll;
    private JScrollPane compAfrTableScroll;
    private JScrollPane cpTableScroll;
    private JScrollPane egoTableScroll;

    private JCheckBox quickShiftBox;
    private JCheckBox neutralBox;

    private JTabbedPane tabbedPane;


    /* Protected so that they can be modified/disabled by subclasses */
    protected JCheckBox maxAfrBox;
    protected JTextField minAfrField;

    protected JCheckBox minAfrBox;
    protected JTextField maxAfrField;

    protected JCheckBox minEctBox;
    protected JTextField minEctField;

    protected JCheckBox lowRpmBox;
    protected JTextField lowRpmField;

    // for autotune
    protected JCheckBox maxTunePercentageBox;
    protected JTextField maxTunePercentageField;


    private List<LogValue> rawLogValues;
    private List<LogValue> filteredLogValues;
    private AdjAFRValue[][] targetMapArray;

    private void selectAfrFile() {
        File file;

        int rVal = afrFileChooser.showDialog(AutoTuneTable.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = afrFileChooser.getSelectedFile();
                this.rawLogValues = autoTuneService.readAfrFile(file);
                AdjAFRValue[][] rawMapArray = autoTuneService.convert2Map(rawLogValues);
                afrModel.setMapArray(rawMapArray);

                // filter the raw data
                this.filteredLogValues = autoTuneService.filter(rawLogValues);
                AdjAFRValue[][] filteredMapArray = autoTuneService.convert2Map(filteredLogValues);
                filteredAfrModel.setMapArray(filteredMapArray);

                logger.debug("Opening afr file: {} ", file.getName());
            } catch (IOException ioe) {
                logger.warn("Error opening afr file");
            }
        } else {
            logger.debug("Cancelled by user.");
        }
    }

    private void selectTargetAfrFile() {
        File file;

        int rVal = targetAfrFileChooser.showDialog(AutoTuneTable.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = targetAfrFileChooser.getSelectedFile();
                targetAfrModel.setMapArray(autoTuneService.readTargetAfrFile(file));
                logger.debug("Opening target afr file: {} ", file.getName());
            } catch (IOException ioe) {
                logger.warn("Error opening target afr file");
            }
        } else {
            logger.debug("Cancelled by user.");
        }
    }


    private void loadTestData() {
        logger.trace("load test data");
        try {
            this.rawLogValues = autoTuneService.readAfrFile();
            AdjAFRValue[][] rawMapArray = autoTuneService.convert2Map(rawLogValues);
            afrModel.setMapArray(rawMapArray);

            this.targetMapArray = autoTuneService.readTargetAfrFile();
            targetAfrModel.setMapArray(targetMapArray);

            filterAndRecalculate();
        } catch (IOException ioe) {
            logger.error("error reading files {}", ioe);
        }
    }

    private void filterAndRecalculate() {
        logger.trace("recalculate the correction values");
        // filter the raw data
        this.filteredLogValues = autoTuneService.filter(rawLogValues);
        AdjAFRValue[][] filteredMapArray = autoTuneService.convert2Map(filteredLogValues);
        filteredAfrModel.setMapArray(filteredMapArray);

        // calculate the compensation
        AdjAFRValue[][] compMap = autoTuneService.calculateCompensation(filteredMapArray, targetMapArray);
        //autoTuneService.print(AutoTuneService.PRINT.AFR, compMap, "Compensation");
        compAfrModel.setMapArray(compMap);

        // calculate the compensation
        AdjAFRValue[][] egoMap = autoTuneService.calculateEgo();
        //autoTuneService.print(AutoTuneService.PRINT.AFR, egoMap, "Ego");
        egoModel.setMapArray(egoMap);
    }

    /**
     * Constructs an instance of the demo.
     */
    @Autowired
    public AutoTuneTable(AfrAnalyzerSettings afrAnalyzerSettings) {
        super("AFRanalyzer 0.1");

        testFileButton = new JButton("load test files");
        testFileButton.addActionListener(ae -> loadTestData());

        calculateButton = new JButton("calculate");
        calculateButton.addActionListener(ae -> filterAndRecalculate());

        afrFileSelectButton = new JButton("select afr file");
        afrFileSelectButton.addActionListener(ae -> selectAfrFile());
        afrFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        afrFileChooser.setDialogTitle("Open AFR file");
        FileFilter csvFilter = new FileNameExtensionFilter("CSV file", "csv");
        afrFileChooser.addChoosableFileFilter(csvFilter);

        targetAfrFileSelectButton = new JButton("select target afr file");
        targetAfrFileSelectButton.addActionListener(ae -> selectTargetAfrFile());
        targetAfrFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        targetAfrFileChooser.setDialogTitle("Open target AFR file");
        targetAfrFileChooser.addChoosableFileFilter(csvFilter);

        afrModel = new AfrModel();
        filteredAfrModel = new AfrModel();
        targetAfrModel = new AfrModel();
        compAfrModel = new AfrModel();
        egoModel = new AfrModel();
        cpModel = new AfrModel(true);
        AdjAFRValue[][] mArr = new AdjAFRValue[AutoTuneService.tpsArray.length][AutoTuneService.rpmArray.length];
        for (int i = 0; i < AutoTuneService.tpsArray.length; i++) {
            for (int j = 0; j < AutoTuneService.rpmArray.length; j++) {
                mArr[i][j] = new AdjAFRValue();
            }
        }
        cpModel.setMapArray(mArr);

        afrTable = createTable(afrModel);
        filteredAfrTable = createTable(filteredAfrModel);
        targetAfrTable = createTable(targetAfrModel);
        compAfrTable = createTable(compAfrModel, true);
        egoTable = createTable(egoModel);
        cpTable = createTable(cpModel);

        afrTableScroll = new JScrollPane(afrTable);
        filteredAfrTableScroll = new JScrollPane(filteredAfrTable);
        targetAfrTableScroll = new JScrollPane(targetAfrTable);
        compAfrTableScroll = new JScrollPane(compAfrTable);
        egoTableScroll = new JScrollPane(egoTable);
        cpTableScroll = new JScrollPane(cpTable);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Raw afr file", afrTableScroll);
        tabbedPane.addTab("Filtered afr file", filteredAfrTableScroll);
        tabbedPane.addTab("Target afr file", targetAfrTableScroll);
        tabbedPane.addTab("Afr compensation (%)", compAfrTableScroll);
        tabbedPane.addTab("Afr C&P (%)", cpTableScroll);
        tabbedPane.addTab("Ego (ms)", egoTableScroll);

        String tooltipText;

        tooltipText = "Max AFR filter";
        maxAfrBox = new JCheckBox("Max Afr:", afrAnalyzerSettings.maxAfrEnabled);
        maxAfrBox.addActionListener(ae -> checkMaxAfrFilter());
        maxAfrBox.setToolTipText(tooltipText);
        tooltipText = "Max AFR filter value";
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
        neutralBox = new JCheckBox("Neutral filter", afrAnalyzerSettings.neutralEnabled);
        neutralBox.setToolTipText(tooltipText);
        neutralBox.addActionListener(ae -> neutralFilter());

        tooltipText = "Filter out values when when engine coolant temp is less than ";
        minEctBox = new JCheckBox("Min ECT:", afrAnalyzerSettings.minEctEnabled);
        minEctBox.setToolTipText(tooltipText);
        minEctBox.addActionListener(ae -> minEctFilter());
        tooltipText = "Min ECT filter value";
        minEctField = new JTextField("" + afrAnalyzerSettings.minEct, 4);
        minEctField.addActionListener(ae -> minEctChanged());
        minEctField.setToolTipText(tooltipText);

        tooltipText = "Filter out values when RPM is less than ";
        lowRpmBox = new JCheckBox("Low RPM filter", afrAnalyzerSettings.minEctEnabled);
        lowRpmBox.setToolTipText(tooltipText);
        lowRpmBox.addActionListener(ae -> lowRpmFilter());
        tooltipText = "Low RPM filter value";
        lowRpmField = new JTextField("" + afrAnalyzerSettings.lowRpm, 4);
        lowRpmField.addActionListener(ae -> lowRpmChanged());
        lowRpmField.setToolTipText(tooltipText);

        contentPane = new JPanel();
        addComponentsToContentPane();
        setContentPane(contentPane);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }


    /**
     * Adds to and lays out all GUI components on the {@code contentPane} panel.
     * <p>
     * It is recommended that you <b>NOT</b> try to understand this code. It was
     * automatically generated by the NetBeans GUI builder.
     */


    private void addComponentsToContentPane() {
        JPanel bp = new JPanel(new MigLayout());
        bp.setBorder(BorderFactory.createTitledBorder("Filter"));

        bp.add(maxAfrBox, "gap para");
        bp.add(maxAfrField, "span, growx, wrap");

        bp.add(minAfrBox, "gap para");
        bp.add(minAfrField, "span, growx, wrap");

        bp.add(minEctBox, "gap para");
        bp.add(minEctField, "span, growx, wrap");

        bp.add(lowRpmBox, "gap para");
        bp.add(lowRpmField, "span, growx, wrap");

        bp.add(quickShiftBox, "gap para, wrap");
        bp.add(neutralBox, "gap para, wrap");


        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                        .addComponent(targetAfrFileSelectButton)
                                        .addComponent(afrFileSelectButton)
                                        .addComponent(testFileButton)
                                        .addComponent(calculateButton)
                                        .addComponent(bp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(targetAfrFileSelectButton)
                                .addComponent(afrFileSelectButton)
                                .addComponent(testFileButton)
                                .addComponent(calculateButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
            /*
            public void changeSelection(int row, int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);

                if (editCellAt(row, column)) {
                    Component editor = getEditorComponent();
                    editor.requestFocusInWindow();
                }
            }*/


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
        };

        jt.setFillsViewportHeight(true);
        jt.setRowHeight(24);
        jt.setCellSelectionEnabled(true);

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
        ExcelAdapter myAd = new ExcelAdapter(jt);

        return jt;
    }

    protected TableCellRenderer createSubstDouble2DecimalRenderer() {
        return new SubstDouble2DecimalRenderer(1);
    }

    protected TableCellRenderer createColoredSubstDouble2DecimalRenderer() {
        return new ColoredSubstDouble2DecimalRenderer(1);
    }

    private void checkMaxAfrFilter() {
        afrAnalyzerSettings.maxAfrEnabled = maxAfrBox.isSelected();
        loadTestData();
    }

    private void checkMinAfrFilter() {
        afrAnalyzerSettings.minAfrEnabled = minAfrBox.isSelected();
        loadTestData();
    }

    private void quickShiftFilter() {
        afrAnalyzerSettings.quickshiftEnabled = quickShiftBox.isSelected();
        loadTestData();
    }

    private void neutralFilter() {
        afrAnalyzerSettings.neutralEnabled = neutralBox.isSelected();
        // rerun filter, for now reload
        loadTestData();
    }

    private void maxAfrChanged() {
        afrAnalyzerSettings.maxAfr = new Double(maxAfrField.getText());
        if (maxAfrBox.isEnabled()) {
            loadTestData();
        }
    }

    private void minEctFilter() {
        afrAnalyzerSettings.minEctEnabled = minEctBox.isSelected();
        // rerun filter, for now reload
        loadTestData();
    }

    private void minEctChanged() {
        afrAnalyzerSettings.minEct = new Integer(minEctField.getText());
        if (minEctBox.isEnabled()) {
            loadTestData();
        }
    }

    private void lowRpmFilter() {
        afrAnalyzerSettings.lowRpmEnabled = lowRpmBox.isSelected();
        // rerun filter, for now reload
        loadTestData();
    }

    private void lowRpmChanged() {
        afrAnalyzerSettings.lowRpm = new Integer(lowRpmField.getText());
        if (lowRpmBox.isEnabled()) {
            loadTestData();
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