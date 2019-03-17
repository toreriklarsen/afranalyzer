package com.larz1.afranalyzer;

import com.larz1.afranalyzer.ui.StatusBar;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Controller
public class AfrAnalyzerUi extends JFrame {
    private static final Logger logger = LoggerFactory
            .getLogger(AfrAnalyzerUi.class);

    private AfrAnalyzerSettings afrAnalyzerSettings;
    private AutoTuneService autoTuneService;
    private AfrModel afrModel;
    private AfrModel filteredAfrModel;
    private AfrModel targetAfrModel;
    private AfrModel cellCountModel;
    private AfrModel compAfrModel;
    private AfrModel sourceModel;
    private AfrModel tuneModel;
    private AfrModel egoModel;

    /* UI Components */
    private JFileChooser afrFileChooser;
    private JFileChooser targetAfrFileChooser;

    // button for test, select both files for convenience
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
    private JScrollPane cellCountTableScroll;

    private JCheckBox quickShiftBox;
    private JCheckBox neutralBox;
    private JCheckBox egoCompensationBox;

    private JTabbedPane tabbedPane;

    private JCheckBox maxAfrBox;
    private JTextField minAfrField;
    private JCheckBox minAfrBox;
    private JTextField maxAfrField;
    private JCheckBox minEctBox;
    private JTextField minEctField;
    private JCheckBox lowRpmBox;
    private JTextField lowRpmField;
    private JCheckBox minLonAccBox;
    private JTextField minLonAccField;
    private JCheckBox gearBox;
    private JComboBox<Integer> gearComboBox;

    // for autotune
    private JCheckBox maxTunePercentageBox;
    private JTextField maxTunePercentageField;
    private JCheckBox tuneStrengthBox;
    private JTextField tuneStrengthField;
    private JCheckBox cellToleranceBox;
    private JTextField cellToleranceField;
    private JCheckBox minValuesInCellBox;
    private JTextField minValuesInCellField;

    private StatusBar statusBar;
    private List<LogValue> rawLogValues;
    private List<LogValue> filteredLogValues;
    private AdjAFRValue[][] targetMapArray;
    private File lastDir;

    private void selectAfrFile() {
        File file;

        if (lastDir != null) {
            afrFileChooser.setCurrentDirectory(lastDir);
        }
        int rVal = afrFileChooser.showDialog(AfrAnalyzerUi.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = afrFileChooser.getSelectedFile();
                lastDir = file.getParentFile();
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
        int rVal = targetAfrFileChooser.showDialog(AfrAnalyzerUi.this, "Open");
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = targetAfrFileChooser.getSelectedFile();
                lastDir = file.getParentFile();
                targetMapArray = autoTuneService.readTargetAfrFile(file);
                targetAfrModel.setMapArray(targetMapArray);
                //logger.debug("Opening target afr file: {} ", file.getName());
            } catch (IOException ioe) {
                logger.warn("Error opening target afr file");
            }
        } else {
            logger.debug("Cancelled by user.");
        }
    }


    private void loadTestData() {
        logger.debug("load test data");
        try {
            List<LogValue> lv;
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
        logger.debug("recalculate the correction values");
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

        AdjAFRValue[][] countMap = autoTuneService.createCountMap(filteredMapArray);
        cellCountModel.setMapArray(countMap);
    }

    private void tune() {
        AdjAFRValue[][] adjMapArray = autoTuneService.calculateTotalCompensation(compAfrModel.getMapArray(), sourceModel.getMapArray());
        tuneModel.setMapArray(adjMapArray);
    }

    @Autowired
    public AfrAnalyzerUi(AfrAnalyzerSettings afrAnalyzerSettings, AutoTuneService autoTuneService, StatusBar statusBar) {
        super("AFRanalyzer 0.8");

        this.afrAnalyzerSettings = afrAnalyzerSettings;
        this.autoTuneService = autoTuneService;
        this.statusBar = statusBar;

        testFileButton = new JButton("load test files");
        testFileButton.addActionListener(ae -> loadTestData());

        calculateButton = new JButton("Recalculate");
        calculateButton.addActionListener(ae -> filterAndRecalculate());

        adjustButton = new JButton("Tune");
        adjustButton.addActionListener(ae -> tune());

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
        cellCountModel = new AfrModel(false);
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
        cellCountTable = new TpsRpmTable(cellCountModel, afrAnalyzerSettings);

        afrTableScroll = new JScrollPane(afrTable);
        filteredAfrTableScroll = new JScrollPane(filteredAfrTable);
        targetAfrTableScroll = new JScrollPane(targetAfrTable);
        compAfrTableScroll = new JScrollPane(compAfrTable);
        egoTableScroll = new JScrollPane(egoTable);
        sourceTableScroll = new JScrollPane(sourceMapTable);
        tuneTableScroll = new JScrollPane(tuneMapTable);
        cellCountTableScroll = new JScrollPane(cellCountTable);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Raw afr file", afrTableScroll);
        tabbedPane.addTab("Filtered afr file", filteredAfrTableScroll);
        tabbedPane.addTab("Target afr file", targetAfrTableScroll);
        tabbedPane.addTab("Cell count", cellCountTableScroll);
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
        maxAfrField = new JTextField(String.valueOf(afrAnalyzerSettings.maxAfr));
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

        tooltipText = "Min Longitudal Acceleration filter";
        minLonAccBox = new JCheckBox("Min LonAcc:", afrAnalyzerSettings.minLonAccEnabled);
        minLonAccBox.addActionListener(ae -> checkMinLonAccFilter());
        minLonAccBox.setToolTipText(tooltipText);
        tooltipText = "Min Longitudal Acceleration value";
        minLonAccField = new JTextField("" + afrAnalyzerSettings.minLonAcc);
        minLonAccField.addActionListener(ae -> minLonAccChanged());
        minLonAccField.setToolTipText(tooltipText);

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

        tooltipText = "Select gear for data";
        gearBox = new JCheckBox("Select gear", afrAnalyzerSettings.gearEnabled);
        gearBox.setToolTipText(tooltipText);
        gearBox.addActionListener(ae -> gear());
        tooltipText = "Min Values %value";
        Integer[] gears = {1, 2, 3, 4, 5, 6};
        gearComboBox = new JComboBox<>(gears);
        gearComboBox.addActionListener(ae -> gearChanged());


        minValuesInCellField.addActionListener(ae -> minValuesInCellChanged());
        minValuesInCellField.setToolTipText(tooltipText);

        contentPane = new JPanel();

        addComponentsToContentPane();
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 740);
        setLocationRelativeTo(null);
    }


    /**
     * Adds to and lays out all GUI components on the {@code contentPane} panel.
     * <p>
     * It is recommended that you <b>NOT</b> try to understand this code. It was
     * automatically generated by the NetBeans GUI builder.
     */


    private void addComponentsToContentPane() {
        JPanel topPanel = createTopPanel();
        JPanel bottomPanel = createBottomPanel();

        setJMenuBar(createMenuBar());
        contentPane.setLayout(new MigLayout("fill"));
        contentPane.add(tabbedPane, "grow");
        contentPane.add(topPanel, "north");
        contentPane.add(statusBar, "south");
        contentPane.add(bottomPanel, "south");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu toolMenu = new JMenu("Tools");
        menuBar.add(fileMenu);
        menuBar.add(toolMenu);

        JMenuItem openTargetFile = new JMenuItem("Open Target file");
        openTargetFile.addActionListener(ae -> selectTargetAfrFile());
        openTargetFile.setMnemonic(KeyEvent.VK_T);
        openTargetFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        JMenuItem openAfrFile = new JMenuItem("Open Afr log file");
        openAfrFile.addActionListener(ae -> selectAfrFile());
        openAfrFile.setMnemonic(KeyEvent.VK_L);
        openAfrFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));


        JMenuItem exitAction = new JMenuItem("Exit");
        exitAction.addActionListener(ae -> this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        exitAction.setMnemonic(KeyEvent.VK_Q);
        exitAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        fileMenu.add(openTargetFile);
        fileMenu.add(openAfrFile);
        fileMenu.add(exitAction);

        JMenuItem calculateAction = new JMenuItem("Recalculate");
        calculateAction.addActionListener(ae -> filterAndRecalculate());
        calculateAction.setMnemonic(KeyEvent.VK_R);
        calculateAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));


        JMenuItem tuneAction = new JMenuItem("Tune");
        tuneAction.addActionListener(ae -> tune());
        tuneAction.setMnemonic(KeyEvent.VK_ENTER);
        tuneAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        toolMenu.add(calculateAction);
        toolMenu.add(tuneAction);


        return menuBar;
    }


    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new MigLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Filter"));

        bottomPanel.add(maxAfrBox);
        bottomPanel.add(maxAfrField);

        bottomPanel.add(minAfrBox);
        bottomPanel.add(minAfrField, "wrap");

        bottomPanel.add(minEctBox);
        bottomPanel.add(minEctField);

        bottomPanel.add(lowRpmBox);
        bottomPanel.add(lowRpmField, "wrap");

        bottomPanel.add(minLonAccBox);
        bottomPanel.add(minLonAccField);

        bottomPanel.add(gearBox);
        bottomPanel.add(gearComboBox, "wrap");



        bottomPanel.add(quickShiftBox);
        bottomPanel.add(neutralBox);
        bottomPanel.add(egoCompensationBox, "wrap");

        bottomPanel.add(maxTunePercentageBox);
        bottomPanel.add(maxTunePercentageField);

        bottomPanel.add(tuneStrengthBox);
        bottomPanel.add(tuneStrengthField, "wrap");

        bottomPanel.add(cellToleranceBox);
        bottomPanel.add(cellToleranceField);

        bottomPanel.add(minValuesInCellBox);
        bottomPanel.add(minValuesInCellField, "wrap");
        return bottomPanel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new MigLayout());
        //topPanel.add(calculateButton);
        //topPanel.add(adjustButton, "wrap");
        topPanel.add(testFileButton, "wrap");
        return topPanel;
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

    private void checkMinLonAccFilter() {
        afrAnalyzerSettings.minLonAccEnabled = minLonAccBox.isSelected();
        filterAndRecalculate();
    }

    private void minLonAccChanged() {
        afrAnalyzerSettings.minLonAcc = new Double(minLonAccField.getText());
        if (minLonAccBox.isEnabled()) {
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

    private void gear() {
        afrAnalyzerSettings.gearEnabled= gearBox.isSelected();
        filterAndRecalculate();
    }

    private void gearChanged() {
        afrAnalyzerSettings.gear = (Integer)gearComboBox.getSelectedItem();
        if (gearBox.isEnabled()) {
            filterAndRecalculate();
        }
    }

}