package com.larz1.afranalyzer;

import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Controller
public class AfrAnalyzer extends JFrame {
    private static final Logger logger = LoggerFactory
            .getLogger(AfrAnalyzer.class);

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
    private AfrModel sourceModel;
    private AfrModel tuneModel;
    private AfrModel egoModel;

    /* UI Components */
    private JFileChooser afrFileChooser;
    private JFileChooser targetAfrFileChooser;

    // button for test, select both files for convinience
    private JButton testFileButton;
    private JButton afrFileSelectButton;
    private JButton targetAfrFileSelectButton;
    private JButton calculateButton;
    private JButton adjustButton;

    private JPanel contentPane;

    private JTable afrTable;
    private JTable filteredAfrTable;
    private JTable targetAfrTable;
    private JTable compAfrTable;
    private JTable cellCountTable;
    private JTable sourceMapTable;
    private JTable tuneMapTable;
    private JTable egoTable;

    private JScrollPane afrTableScroll;
    private JScrollPane filteredAfrTableScroll;
    private JScrollPane targetAfrTableScroll;
    private JScrollPane compAfrTableScroll;
    private JScrollPane sourceTableScroll;
    private JScrollPane tuneTableScroll;
    private JScrollPane egoTableScroll;

    private JCheckBox quickShiftBox;
    private JCheckBox neutralBox;
    private JCheckBox egoCompensationBox;

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

    protected JCheckBox tuneStrengthBox;
    protected JTextField tuneStrengthField;

    protected JCheckBox cellToleranceBox;
    protected JTextField cellToleranceField;

    protected JCheckBox minValuesInCellBox;
    protected JTextField minValuesInCellField;


    private List<LogValue> rawLogValues;
    private List<LogValue> filteredLogValues;
    private AdjAFRValue[][] targetMapArray;
    private File lastDir;

    private void selectAfrFile() {
        File file;

        if (lastDir != null) {
            afrFileChooser.setCurrentDirectory(lastDir);
        }
        int rVal = afrFileChooser.showDialog(AfrAnalyzer.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = afrFileChooser.getSelectedFile();
                lastDir = afrFileChooser.getCurrentDirectory();
                this.rawLogValues = autoTuneService.readAfrFile(file);
                AdjAFRValue[][] rawMapArray = autoTuneService.convert2Map(rawLogValues);
                if (!AutoTuneService.validateMapCount(rawLogValues, rawMapArray)) {
                    System.out.println("ERROR");
                }

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

        if (lastDir != null) {
            afrFileChooser.setCurrentDirectory(lastDir);
        }
        int rVal = targetAfrFileChooser.showDialog(AfrAnalyzer.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = targetAfrFileChooser.getSelectedFile();
                lastDir = afrFileChooser.getCurrentDirectory();
                targetMapArray = autoTuneService.readTargetAfrFile(file);
                targetAfrModel.setMapArray(targetMapArray);
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
            List<LogValue> lv = null;
            this.rawLogValues = autoTuneService.readAfrFile();
            if (afrAnalyzerSettings.egoCompensationEnabled) {
                lv = autoTuneService.applyEgo(this.rawLogValues);
            } else {
                lv = this.rawLogValues;
            }
            AdjAFRValue[][] rawMapArray = autoTuneService.convert2Map(lv);
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
        List<LogValue> lv = null;
        if (afrAnalyzerSettings.egoCompensationEnabled) {
            lv = autoTuneService.applyEgo(this.rawLogValues);
        } else {
            lv = this.rawLogValues;
        }

        this.filteredLogValues = autoTuneService.filter(lv);
        AdjAFRValue[][] filteredMapArray = autoTuneService.convert2Map(filteredLogValues);
        filteredAfrModel.setMapArray(filteredMapArray);

        // calculate the compensation
        AdjAFRValue[][] compMap = autoTuneService.calculateCompensation(filteredMapArray, targetMapArray);
        //autoTuneService.print(AutoTuneService.PRINT.AFR, compMap, "Compensation");
        compAfrModel.setMapArray(compMap);

        AdjAFRValue[][] egoMap = autoTuneService.calculateEgo();
        //autoTuneService.print(AutoTuneService.PRINT.AFR, egoMap, "Ego");
        egoModel.setMapArray(egoMap);
    }

    private void adjust() {
        AdjAFRValue[][] adjMapArray = autoTuneService.calculateTotalCompensation(compAfrModel.getMapArray(), sourceModel.getMapArray());
        tuneModel.setMapArray(adjMapArray);
    }

    /**
     * Constructs an instance of the demo.
     */
    @Autowired
    public AfrAnalyzer(AfrAnalyzerSettings afrAnalyzerSettings) {
        super("AFRanalyzer 0.1");

        testFileButton = new JButton("load test files");
        testFileButton.addActionListener(ae -> loadTestData());

        calculateButton = new JButton("calculate");
        calculateButton.addActionListener(ae -> filterAndRecalculate());

        adjustButton= new JButton("Tune");
        adjustButton.addActionListener(ae -> adjust());

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
        egoModel = new AfrModel(false, true);
        sourceModel = new AfrModel(true);
        tuneModel = new AfrModel(true);
        AdjAFRValue[][] mArr = new AdjAFRValue[AutoTuneService.tpsArray.length][AutoTuneService.rpmArray.length];
        for (int i = 0; i < AutoTuneService.tpsArray.length; i++) {
            for (int j = 0; j < AutoTuneService.rpmArray.length; j++) {
                mArr[i][j] = new AdjAFRValue();
            }
        }
        sourceModel.setMapArray(mArr);

        afrTable = new TpsRpmTable(afrModel, afrAnalyzerSettings);
        filteredAfrTable = new TpsRpmTable(filteredAfrModel, compAfrModel, true, afrAnalyzerSettings);
        targetAfrTable = new TpsRpmTable(targetAfrModel, afrAnalyzerSettings);
        compAfrTable = new TpsRpmTable(compAfrModel, null, true, afrAnalyzerSettings);
        egoTable = new TpsRpmTable(egoModel, afrAnalyzerSettings);
        sourceMapTable = new TpsRpmTable(sourceModel, afrAnalyzerSettings);
        tuneMapTable = new TpsRpmTable(tuneModel, afrAnalyzerSettings);

        afrTableScroll = new JScrollPane(afrTable);
        filteredAfrTableScroll = new JScrollPane(filteredAfrTable);
        targetAfrTableScroll = new JScrollPane(targetAfrTable);
        compAfrTableScroll = new JScrollPane(compAfrTable);
        egoTableScroll = new JScrollPane(egoTable);
        sourceTableScroll = new JScrollPane(sourceMapTable);
        tuneTableScroll= new JScrollPane(tuneMapTable);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Raw afr file", afrTableScroll);
        tabbedPane.addTab("Filtered afr file", filteredAfrTableScroll);
        tabbedPane.addTab("Target afr file", targetAfrTableScroll);
        tabbedPane.addTab("Afr compensation (%)", compAfrTableScroll);
        tabbedPane.addTab("Source map", sourceTableScroll);
        tabbedPane.addTab("Tuned map", tuneTableScroll);
        tabbedPane.addTab("Ego (ms)", egoTableScroll);
        //tabbedPane.addTab("Afr 3d", egoTableScroll);

        String tooltipText;

        tooltipText = "Max AFR filter";
        maxAfrBox = new JCheckBox("Max Afr:", afrAnalyzerSettings.maxAfrEnabled);
        maxAfrBox.addActionListener(ae -> checkMaxAfrFilter());
        maxAfrBox.setToolTipText(tooltipText);
        tooltipText = "Max AFR filter value";
        maxAfrField = new JTextField("" + afrAnalyzerSettings.maxAfr);
        maxAfrField.addActionListener(ae -> maxAfrChanged());
        maxAfrField.setToolTipText(tooltipText);

        tooltipText = "Min AFR filter";
        minAfrBox = new JCheckBox("Min Afr:", afrAnalyzerSettings.minAfrEnabled);
        minAfrBox.addActionListener(ae -> checkMinAfrFilter());
        minAfrBox.setToolTipText(tooltipText);
        tooltipText = "Min AFR filter value";
        minAfrField = new JTextField("" + afrAnalyzerSettings.minAfr);
        minAfrField.addActionListener(ae -> minAfrChanged());
        minAfrField.setToolTipText(tooltipText);

        tooltipText = "Filter out values due to upshifting with quickshifter";
        quickShiftBox = new JCheckBox("Quick shift filter", afrAnalyzerSettings.quickshiftEnabled);
        quickShiftBox.setToolTipText(tooltipText);
        quickShiftBox.addActionListener(ae -> quickShiftFilter());

        tooltipText = "Filter out values when in neutral gear";
        neutralBox = new JCheckBox("Neutral filter", afrAnalyzerSettings.neutralEnabled);
        neutralBox.setToolTipText(tooltipText);
        neutralBox.addActionListener(ae -> neutralFilter());

        tooltipText = "Do compensation for exhaust gas offset (EGO)";
        egoCompensationBox = new JCheckBox("EGO compensation", afrAnalyzerSettings.egoCompensationEnabled);
        egoCompensationBox.setToolTipText(tooltipText);
        egoCompensationBox.addActionListener(ae -> egoCompensation());

        tooltipText = "Filter out values when when engine coolant temp is less than ";
        minEctBox = new JCheckBox("Min ECT:", afrAnalyzerSettings.minEctEnabled);
        minEctBox.setToolTipText(tooltipText);
        minEctBox.addActionListener(ae -> minEctFilter());
        tooltipText = "Min ECT filter value:";
        minEctField = new JTextField("" + afrAnalyzerSettings.minEct, 4);
        minEctField.addActionListener(ae -> minEctChanged());
        minEctField.setToolTipText(tooltipText);

        tooltipText = "Filter out values when RPM is less than ";
        lowRpmBox = new JCheckBox("Low RPM filter", afrAnalyzerSettings.minEctEnabled);
        lowRpmBox.setToolTipText(tooltipText);
        lowRpmBox.addActionListener(ae -> lowRpmFilter());
        tooltipText = "Low RPM filter value:";
        lowRpmField = new JTextField("" + afrAnalyzerSettings.lowRpm, 4);
        lowRpmField.addActionListener(ae -> lowRpmChanged());
        lowRpmField.setToolTipText(tooltipText);

        tooltipText = "Max tune percent 0.0 - 1.0";
        maxTunePercentageBox = new JCheckBox("Max tune %:", afrAnalyzerSettings.maxtunepercentageEnabled);
        maxTunePercentageBox.setToolTipText(tooltipText);
        maxTunePercentageBox.addActionListener(ae -> maxTune());
        tooltipText = "Max tune %value";
        maxTunePercentageField = new JTextField("" + afrAnalyzerSettings.maxTunePercentage, 4);
        maxTunePercentageField.addActionListener(ae -> maxTuneChanged());
        maxTunePercentageField.setToolTipText(tooltipText);

        tooltipText = "Tune strength <= 1.0";
        tuneStrengthBox = new JCheckBox("Tune strength:", afrAnalyzerSettings.tuneStrengthEnabled);
        tuneStrengthBox.setToolTipText(tooltipText);
        tuneStrengthBox.addActionListener(ae -> tuneStrength());
        tooltipText = "Tune strength %value";
        tuneStrengthField = new JTextField("" + afrAnalyzerSettings.tuneStrength, 4);
        tuneStrengthField.addActionListener(ae -> tuneStrengthChanged());
        tuneStrengthField.setToolTipText(tooltipText);

        tooltipText = "Controls the binning";
        cellToleranceBox = new JCheckBox("Cell tolerance", afrAnalyzerSettings.cellToleranceEnabled);
        cellToleranceBox.setToolTipText(tooltipText);
        cellToleranceBox.addActionListener(ae -> cellTolerance());
        tooltipText = "Binning tolerance %value";
        cellToleranceField = new JTextField("" + afrAnalyzerSettings.cellTolerance, 4);
        cellToleranceField.addActionListener(ae -> cellToleranceChanged());
        cellToleranceField.setToolTipText(tooltipText);

        tooltipText = "Min values in cell";
        minValuesInCellBox = new JCheckBox("Min values in cell", afrAnalyzerSettings.minValuesInCellEnabled);
        minValuesInCellBox.setToolTipText(tooltipText);
        minValuesInCellBox.addActionListener(ae -> minValuesInCell());
        tooltipText = "Min Values %value";
        minValuesInCellField = new JTextField("" + afrAnalyzerSettings.minValuesInCell, 4);
        minValuesInCellField.addActionListener(ae -> minValuesInCellChanged());
        minValuesInCellField.setToolTipText(tooltipText);

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

        bp.add(maxAfrBox);
        bp.add(maxAfrField);

        bp.add(minAfrBox);
        bp.add(minAfrField, "wrap");

        bp.add(minEctBox);
        bp.add(minEctField);

        bp.add(lowRpmBox);
        bp.add(lowRpmField, "wrap");

        bp.add(quickShiftBox);
        bp.add(neutralBox);
        bp.add(egoCompensationBox, "wrap");

        bp.add(maxTunePercentageBox);
        bp.add(maxTunePercentageField);

        bp.add(tuneStrengthBox);
        bp.add(tuneStrengthField, "wrap");

        bp.add(cellToleranceBox);
        bp.add(cellToleranceField);

        bp.add(minValuesInCellBox);
        bp.add(minValuesInCellField, "wrap");

        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                        .addComponent(targetAfrFileSelectButton)
                                        .addComponent(afrFileSelectButton)
                                        .addComponent(testFileButton)
                                        .addComponent(calculateButton)
                                        .addComponent(adjustButton)
                                        //.addComponent(bp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addComponent(bp))


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
                                .addComponent(adjustButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                //.addComponent(bp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(bp)
                                .addContainerGap())
        );
    }

    private void checkMaxAfrFilter() {
        afrAnalyzerSettings.maxAfrEnabled = maxAfrBox.isSelected();
        filterAndRecalculate();
    }

    private void checkMinAfrFilter() {
        afrAnalyzerSettings.minAfrEnabled = minAfrBox.isSelected();
        filterAndRecalculate();
    }

    private void quickShiftFilter() {
        afrAnalyzerSettings.quickshiftEnabled = quickShiftBox.isSelected();
        filterAndRecalculate();
    }

    private void neutralFilter() {
        afrAnalyzerSettings.neutralEnabled = neutralBox.isSelected();
        filterAndRecalculate();
    }

    private void egoCompensation() {
        afrAnalyzerSettings.egoCompensationEnabled = egoCompensationBox.isSelected();
        filterAndRecalculate();
    }

    private void maxAfrChanged() {
        afrAnalyzerSettings.maxAfr = new Double(maxAfrField.getText());
        if (maxAfrBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void minAfrChanged() {
        afrAnalyzerSettings.minAfr = new Double(minAfrField.getText());
        if (minAfrBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void minEctFilter() {
        afrAnalyzerSettings.minEctEnabled = minEctBox.isSelected();
        filterAndRecalculate();
    }

    private void minEctChanged() {
        afrAnalyzerSettings.minEct = new Integer(minEctField.getText());
        if (minEctBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void lowRpmFilter() {
        afrAnalyzerSettings.lowRpmEnabled = lowRpmBox.isSelected();
        filterAndRecalculate();
    }

    private void lowRpmChanged() {
        afrAnalyzerSettings.lowRpm = new Integer(lowRpmField.getText());
        if (lowRpmBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void maxTune() {
        afrAnalyzerSettings.maxtunepercentageEnabled = maxTunePercentageBox.isSelected();
        filterAndRecalculate();
    }

    private void maxTuneChanged() {
        afrAnalyzerSettings.maxTunePercentage = new Double(maxTunePercentageField.getText());
        if (maxTunePercentageBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void tuneStrength() {
        afrAnalyzerSettings.tuneStrengthEnabled = tuneStrengthBox.isSelected();
        filterAndRecalculate();
    }

    private void tuneStrengthChanged() {
        afrAnalyzerSettings.tuneStrength = new Double(tuneStrengthField.getText());
        if (tuneStrengthBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void cellTolerance() {
        afrAnalyzerSettings.cellToleranceEnabled = cellToleranceBox.isSelected();
        filterAndRecalculate();
    }

    private void cellToleranceChanged() {
        afrAnalyzerSettings.cellTolerance = new Double(cellToleranceField.getText());
        if (cellToleranceBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

    private void minValuesInCell() {
        afrAnalyzerSettings.minValuesInCellEnabled = minValuesInCellBox.isSelected();
        filterAndRecalculate();
    }

    private void minValuesInCellChanged() {
        afrAnalyzerSettings.minValuesInCell = new Integer(minValuesInCellField.getText());
        if (minValuesInCellBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

}