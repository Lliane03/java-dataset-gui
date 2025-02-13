import javax.swing.*; // Import necessary classes for Swing GUI components and related functionality.
import javax.swing.table.DefaultTableCellRenderer; // Import classes for customizing table cell rendering (how cells are displayed).
import javax.swing.table.DefaultTableModel; // Import classes for working with the table's data model.
import javax.swing.table.TableColumnModel; // Import classes for managing the table's columns.
import java.awt.*; // Import classes for AWT (Abstract Window Toolkit) - used for basic UI elements and layout.
import java.awt.event.ActionEvent; // Import classes for handling ActionEvents (like button clicks).
import java.awt.event.ActionListener; // Import classes for creating ActionListeners (to respond to ActionEvents).
import javax.swing.filechooser.FileNameExtensionFilter; // Import classes for using the JFileChooser (file selection dialog).
import java.io.BufferedReader; // Import classes for efficient reading of text from a file.
import java.io.File; // Import classes for working with files and directories.
import java.io.FileReader; // Import classes for reading character streams from files.
import java.io.IOException; // Import classes for handling input/output exceptions.
import java.util.ArrayList; // Import classes for using dynamic arrays (lists).
import java.util.HashMap; // Import classes for using hash tables (maps).
import java.util.List; // Import classes for working with lists (ordered collections).
import java.util.Map; // Import classes for working with maps (key-value pairs).

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
        setTitle("JAVA DATASET SPLIT");     // Set the title of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Set the default close operation (exit the application when the window is closed)
        setSize(800, 600);      // Set the size of the JFrame
        setLocationRelativeTo(null);        // Center the JFrame on the screen
        getContentPane().setBackground(new Color(4, 11, 24));       // Set the background color of the content pane

        // Create the main panel with a BoxLayout (vertical arrangement)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // Set border and background color for the main panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(4, 11, 24));
        add(mainPanel);     // Add the main panel to the JFrame

        // Create and configure the title label
        JLabel titleLabel = new JLabel("JAVA DATASET SPLIT");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setForeground(new Color(226, 234, 51));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create and configure the description label
        JLabel descriptionLabel = new JLabel("This program splits a dataset based on Boolean value.");
        descriptionLabel.setFont(new Font("Georgia", Font.PLAIN, 25));
        descriptionLabel.setForeground(new Color(226, 234, 51));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create the file selection panel
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

        // Create and configure the split button
        splitButton = new JButton("Split Dataset");
        splitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(splitButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create labels to display the counts of true and false data
        trueCountLabel = new JLabel("TRUE Count: 0");
        falseCountLabel = new JLabel("FALSE Count: 0");

        // Create a panel to hold the count labels and add it to the main panel
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout for centering
        countPanel.add(trueCountLabel);
        countPanel.add(falseCountLabel);
        mainPanel.add(countPanel);  // Add countPanel to mainPanel

        // Initialize the table models and tables for true and false data
        trueTableModel = new DefaultTableModel();
        trueDataTable = new JTable(trueTableModel);
        trueScrollPane = new JScrollPane(trueDataTable);

        falseTableModel = new DefaultTableModel();
        falseDataTable = new JTable(falseTableModel);
        falseScrollPane = new JScrollPane(falseDataTable);

        // Create a panel to hold the tables (side by side using GridLayout)
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(trueScrollPane);
        textPanel.add(falseScrollPane);

        mainPanel.add(textPanel);

        // Add action listener for the "Choose File" button
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();      // Open a JFileChooser to allow the user to select a CSV file.
                // Set a file filter to only show CSV files.
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(DatasetSplitterGUI.this);     // Show the file chooser dialog and store the result.
                // If the user selects a file, update the file path text field
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // Add action listener for the "Split Dataset" button
        splitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the file path from the text field.
                String filePath = filePathField.getText();
                // If a file path is present, call the splitDataset method.
                if (!filePath.equals("No file chosen")) {
                    splitDataset(filePath);
                } else {
                    // Show a message dialog if no file has been chosen
                    JOptionPane.showMessageDialog(DatasetSplitterGUI.this, "Select your CVS file first.");
                }
            }
        });

        setVisible(true);       // Make the JFrame visible
    }

    private void splitDataset(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the header line and determine the delimiter
            String headerLine = br.readLine();
            String delimiter = headerLine.contains(",") ? "," : "\\s+";
            header = headerLine.split(delimiter);

            // Create a list to store the data rows as maps (key: column name, value: cell value).
            List<Map<String, String>> data = new ArrayList<>();
            String line;
            // Read each line of the CSV file.
            while ((line = br.readLine()) != null) {
                // Split the line into values based on the delimiter.
                String[] values = line.split(delimiter);

                // Check if the number of values matches the number of headers.
                if (values.length != header.length) {
                    System.err.println("Row has incorrect number of values: " + line);
                    continue;
                }

                // Create a map for each row and store the data.
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < header.length; i++) {
                    row.put(header[i], values[i]);
                }
                data.add(row);
            }

            // Create separate lists to store true and false data.
            trueData = new ArrayList<>();
            falseData = new ArrayList<>();

            // Iterate through the data and split it based on the boolean column.
            for (Map<String, String> row : data) {
                String booleanColumnName = header[3]; // Assumes 4th column is Boolean. (ADJUST IF NEEDED)
                String booleanValue = row.get(booleanColumnName);

                // Check if the boolean value is valid.
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

            // Display the true and false data in the respective tables.
            displayData(trueData, trueTableModel, trueDataTable);
            displayData(falseData, falseTableModel, falseDataTable);

            // Update the count labels with the number of true and false rows.
            int trueCount = trueData.size();
            int falseCount = falseData.size();
            trueCountLabel.setText("TRUE Count: " + trueCount);
            falseCountLabel.setText("FALSE Count: " + falseCount);

        } catch (IOException e) {
            // Show an error message dialog if there's an issue reading the file.
            JOptionPane.showMessageDialog(DatasetSplitterGUI.this, "Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayData(List<Map<String, String>> data, DefaultTableModel tableModel, JTable table) {
        // Clear the table model before adding new data.
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    
        // If the data list is empty, there's nothing to display.
        if (data.isEmpty()) {
            return;
        }
    
        // Add the header row to the table model.
        for (String col : header) {
            tableModel.addColumn(col);
        }
    
        // Add the data rows to the table model.
        for (Map<String, String> row : data) {
            Object[] rowData = new Object[header.length];
            for (int i = 0; i < header.length; i++) {
                rowData[i] = row.get(header[i]);
            }
            tableModel.addRow(rowData);
        }
    
        // Calculate maximum width for each column based on header and data.
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
    
            // Center align non-boolean columns.
            if (!header[i].equalsIgnoreCase("boolean")) {
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                columnModel.getColumn(i).setCellRenderer(centerRenderer);
            } else { //For the boolean column
                columnModel.getColumn(i).setCellRenderer(new BooleanRenderer());
            }
        }
    }

    // Custom renderer for boolean values (center alignment)
    private class BooleanRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    public static void main(String[] args) {
        // Launch the GUI on the Swing event dispatch thread.
        SwingUtilities.invokeLater(() -> new DatasetSplitterGUI());
    }
}
