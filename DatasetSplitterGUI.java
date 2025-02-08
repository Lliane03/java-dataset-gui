import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetSplitterGUI extends JFrame {

    private JTextField filePathField;
    private JButton chooseFileButton;
    private JButton splitButton;

    private JTable trueDataTable;
    private JTable falseDataTable;
    private DefaultTableModel trueTableModel;
    private DefaultTableModel falseTableModel;
    private JScrollPane trueScrollPane;
    private JScrollPane falseScrollPane;

    private JLabel trueCountLabel;  // Label to display TRUE count
    private JLabel falseCountLabel; // Label to display FALSE count


    private List<Map<String, String>> trueData;
    private List<Map<String, String>> falseData;
    private String[] header;

    public DatasetSplitterGUI() {
        setTitle("JAVA DATASET SPLIT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(4, 11, 24));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(4, 11, 24));
        add(mainPanel);

        JLabel titleLabel = new JLabel("JAVA DATASET SPLIT");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setForeground(new Color(226, 234, 51));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel descriptionLabel = new JLabel("This program splits a dataset based on Boolean value.");
        descriptionLabel.setFont(new Font("Georgia", Font.PLAIN, 25));
        descriptionLabel.setForeground(new Color(226, 234, 51));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filePanel.setBackground(new Color(4, 11, 24));
        chooseFileButton = new JButton("Choose File");
        filePathField = new JTextField("No file chosen", 40);
        filePathField.setEditable(false);
        filePanel.add(chooseFileButton);
        filePanel.add(filePathField);
        mainPanel.add(filePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        splitButton = new JButton("Split Dataset");
        splitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(splitButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add labels for counts
        trueCountLabel = new JLabel("TRUE Count: 0");
        falseCountLabel = new JLabel("FALSE Count: 0");

        // Add labels to the panel (adjust layout as needed)
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout for centering
        countPanel.add(trueCountLabel);
        countPanel.add(falseCountLabel);
        mainPanel.add(countPanel);  // Add countPanel to mainPanel

        trueTableModel = new DefaultTableModel();
        trueDataTable = new JTable(trueTableModel);
        trueScrollPane = new JScrollPane(trueDataTable);

        falseTableModel = new DefaultTableModel();
        falseDataTable = new JTable(falseTableModel);
        falseScrollPane = new JScrollPane(falseDataTable);

        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(trueScrollPane);
        textPanel.add(falseScrollPane);

        mainPanel.add(textPanel);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(DatasetSplitterGUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        splitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if (!filePath.equals("No file chosen")) {
                    splitDataset(filePath);
                } else {
                    JOptionPane.showMessageDialog(DatasetSplitterGUI.this, "Select your CVS file first.");
                }
            }
        });

        setVisible(true);
    }

    private void splitDataset(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            String delimiter = headerLine.contains(",") ? "," : "\\s+";
            header = headerLine.split(delimiter);

            List<Map<String, String>> data = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);

                if (values.length != header.length) {
                    System.err.println("Row has incorrect number of values: " + line);
                    continue;
                }

                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < header.length; i++) {
                    row.put(header[i], values[i]);
                }
                data.add(row);
            }

            trueData = new ArrayList<>();
            falseData = new ArrayList<>();

            for (Map<String, String> row : data) {
                String booleanColumnName = header[3]; // Assumes 4th column is Boolean. ADJUST IF NEEDED
                String booleanValue = row.get(booleanColumnName);

                if (booleanValue != null) {
                    if (booleanValue.equalsIgnoreCase("true")) {
                        trueData.add(row);
                    } else if (booleanValue.equalsIgnoreCase("false")) {
                        falseData.add(row);
                    } else {
                        System.err.println("Invalid boolean value: " + booleanValue + " in row: " + row);
                    }
                } else {
                    System.err.println("Boolean column is missing in row: " + row);
                }
            }

            displayData(trueData, trueTableModel, trueDataTable);
            displayData(falseData, falseTableModel, falseDataTable);

            // Update counts and labels
            int trueCount = trueData.size();
            int falseCount = falseData.size();
            trueCountLabel.setText("TRUE Count: " + trueCount);
            falseCountLabel.setText("FALSE Count: " + falseCount);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(DatasetSplitterGUI.this, "Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayData(List<Map<String, String>> data, DefaultTableModel tableModel, JTable table) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data.isEmpty()) {
            return;
        }

        // Add header to table model
        for (String col : header) {
            tableModel.addColumn(col);
        }

        // Add data rows to table model
        for (Map<String, String> row : data) {
            Object[] rowData = new Object[header.length];
            for (int i = 0; i < header.length; i++) {
                rowData[i] = row.get(header[i]);
            }
            tableModel.addRow(rowData);
        }

        // Calculate max widths for each column
        int[] columnWidths = new int[header.length];
        for (String col : header) {
            columnWidths[header.length - 1] = Math.max(columnWidths[header.length - 1], col.length());
        }
        for (Map<String, String> row : data) {
            for (int i = 0; i < header.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row.get(header[i]).length());
            }
        }

        // Set preferred column widths and alignment
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < header.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i] * 8);
            if (header[i].equalsIgnoreCase("boolean")) {
                columnModel.getColumn(i).setCellRenderer(new BooleanRenderer());
            }
        }
    }

    // Custom renderer for boolean values (right alignment)
    private class BooleanRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT); // Right align
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DatasetSplitterGUI());
    }
}