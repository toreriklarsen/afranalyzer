package com.larz1.afranalyzer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import static com.larz1.afranalyzer.AutoTuneService.readAfrFile;

/**
 * Demo of the basic features of {@code JTable} printing.
 * Allows the user to configure a couple of options and print
 * a table of student grades.
 * <p>
 * Requires the following other files:
 * <ul>
 *     <li>images/passed.png
 *     <li>images/failed.png
 * </ul>
 *
 * @author Shannon Hickey
 */
@SpringBootApplication
public class AutoTuneTable extends JFrame {



    /* UI Components */
    private JPanel contentPane;
    private JLabel afrLabel;
    private JTable afrTable;
    private JScrollPane scroll;
    private JCheckBox showPrintDialogBox;
    private JCheckBox interactiveBox;
    private JCheckBox fitWidthBox;
    private JButton printButton;

    /* Protected so that they can be modified/disabled by subclasses */
    protected JCheckBox headerBox;
    protected JCheckBox footerBox;
    protected JTextField headerField;
    protected JTextField footerField;

    /**
     * Constructs an instance of the demo.
     */
    public AutoTuneTable() {
        super("AFRanalyzer 1.0");

        afrLabel = new JLabel("AFRanalyzer 1.0");
        afrLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        afrTable = createTable(new AfrModel());
        afrTable.setFillsViewportHeight(true);
        afrTable.setRowHeight(24);

        /* Set a custom renderer on the "Passed" column */
        //gradesTable.getColumn("Passed").setCellRenderer(createPassedColumnRenderer());

        for (int i = 1; i <= 13;i++) {
            afrTable.getColumn("" + (i -1) * 1000).setCellRenderer(createSubstDouble2DecimalRenderer());
        }
        afrTable.getColumn("12500").setCellRenderer(createSubstDouble2DecimalRenderer());
        afrTable.getColumn("13000").setCellRenderer(createSubstDouble2DecimalRenderer());
        afrTable.getColumn("13500").setCellRenderer(createSubstDouble2DecimalRenderer());
        afrTable.getColumn("14000").setCellRenderer(createSubstDouble2DecimalRenderer());

        scroll = new JScrollPane(afrTable);

        String tooltipText;

        tooltipText = "Include a page header";
        headerBox = new JCheckBox("Header:", true);
        headerBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                headerField.setEnabled(headerBox.isSelected());
            }
        });
        headerBox.setToolTipText(tooltipText);
        tooltipText = "Page Header (Use {0} to include page number)";
        headerField = new JTextField("Final Grades - CSC 101");
        headerField.setToolTipText(tooltipText);

        tooltipText = "Include a page footer";
        footerBox = new JCheckBox("Footer:", true);
        footerBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                footerField.setEnabled(footerBox.isSelected());
            }
        });
        footerBox.setToolTipText(tooltipText);
        tooltipText = "Page Footer (Use {0} to Include Page Number)";
        footerField = new JTextField("Page {0}");
        footerField.setToolTipText(tooltipText);

        tooltipText = "Show the Print Dialog Before Printing";
        showPrintDialogBox = new JCheckBox("Show print dialog", true);
        showPrintDialogBox.setToolTipText(tooltipText);
        showPrintDialogBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!showPrintDialogBox.isSelected()) {
                    JOptionPane.showMessageDialog(
                            AutoTuneTable.this,
                            "If the Print Dialog is not shown,"
                                    + " the default printer is used.",
                            "Printing Message",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });


        tooltipText = "Keep the GUI Responsive and Show a Status Dialog During Printing";
        interactiveBox = new JCheckBox("Interactive (Show status dialog)", true);
        interactiveBox.setToolTipText(tooltipText);
        interactiveBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!interactiveBox.isSelected()) {
                    JOptionPane.showMessageDialog(
                            AutoTuneTable.this,
                            "If non-interactive, the GUI is fully blocked"
                                    + " during printing.",
                            "Printing Message",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

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
        setSize(1000, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Adds to and lays out all GUI components on the {@code contentPane} panel.
     * <p>
     * It is recommended that you <b>NOT</b> try to understand this code. It was
     * automatically generated by the NetBeans GUI builder.
     *
     */
    private void addComponentsToContentPane() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Printing"));

        GroupLayout bottomPanelLayout = new GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
                bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(headerBox)
                                        .addComponent(footerBox))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(footerField)
                                        .addComponent(headerField, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                                .addComponent(fitWidthBox)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(printButton))
                                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                                .addComponent(showPrintDialogBox)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(interactiveBox)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
                bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(bottomPanelLayout.createSequentialGroup()
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(headerBox)
                                        .addComponent(headerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(interactiveBox)
                                        .addComponent(showPrintDialogBox))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(footerBox)
                                        .addComponent(footerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                        .addComponent(afrLabel)
                                        .addComponent(bottomPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(afrLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
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
        return new JTable(model);
    }

    /**
     * Create and return a cell renderer for rendering the pass/fail column.
     * This is protected so that a subclass can further customize it.
     */
    protected TableCellRenderer createPassedColumnRenderer() {
        return new PassedColumnRenderer();
    }

    protected TableCellRenderer createSubstDouble2DecimalRenderer() {
        return new SubstDouble2DecimalRenderer(1);
    }

    /**
     * Print the grades table.
     */
    private void printGradesTable() {
        /* Fetch printing properties from the GUI components */

        MessageFormat header = null;

        /* if we should print a header */
        if (headerBox.isSelected()) {
            /* create a MessageFormat around the header text */
            header = new MessageFormat(headerField.getText());
        }

        MessageFormat footer = null;

        /* if we should print a footer */
        if (footerBox.isSelected()) {
            /* create a MessageFormat around the footer text */
            footer = new MessageFormat(footerField.getText());
        }

        boolean fitWidth = fitWidthBox.isSelected();
        boolean showPrintDialog = showPrintDialogBox.isSelected();
        boolean interactive = interactiveBox.isSelected();

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

    /**
     * Start the application.
     */
    public static void main(final String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(AutoTuneTable.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            AutoTuneTable ex = ctx.getBean(AutoTuneTable.class);
            ex.setVisible(true);
        });


        /* Schedule for the GUI to be created and shown on the EDT */

        /*
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", false);
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }
                new TablePrintDemo1().setVisible(true);
            }
        });
        */
    }

    /**
     * A table model containing student grades.
     */
    private static class AfrModel implements TableModel {
        AdjAFRValue[][] filteredMapArray = null;
        AutoTuneService ats = new AutoTuneService();
        public AfrModel() {
            try {
                List<AFRValue> afrValues = readAfrFile();
                ats.print(AutoTuneService.PRINT.AFR, ats.convert2Map(afrValues));
                ats.print(AutoTuneService.PRINT.COUNT, ats.convert2Map(afrValues));

                List<AFRValue> filteredAFRValues = ats.filter(afrValues);

                // convert to maparray
                filteredMapArray = ats.convert2Map(filteredAFRValues);
                ats.print(AutoTuneService.PRINT.AFR, filteredMapArray);
                ats.print(AutoTuneService.PRINT.COUNT, filteredMapArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
        public void addTableModelListener(TableModelListener l) {}
        public void removeTableModelListener(TableModelListener l) {}

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

            switch(col) {
                case 14: return "12500";
                case 15: return "13000";
                case 16: return "13500";
                case 17: return "14000";
            }

            throw new AssertionError("invalid column:" + col);
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return ats.tpsArray[row];
            }

            return filteredMapArray[row][col - 1].getAverage();

            /*
            throw new AssertionError("invalid column");
            */
        }
    }

    /**
     * A custom cell renderer for rendering the "Passed" column.
     */
    protected static class PassedColumnRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);

            setText("");
            setHorizontalAlignment(SwingConstants.CENTER);

            /* set the icon based on the passed status */
            boolean status = (Boolean)value;
            //setIcon(status ? passedIcon : failedIcon);

            return this;
        }
    }

    protected static class SubstDouble2DecimalRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        private int precision = 0;
        private Number numberValue;
        private NumberFormat nf;

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
}