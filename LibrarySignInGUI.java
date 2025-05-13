package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

/**
 * A GUI-based application for handling sign-ins in a library system.
 * Allows users (staff or readers) to sign in, export reports, and clear fields.
 * Data is saved to a file called {@code signin_report.txt}.
 */
public class LibrarySignInGUI extends JFrame implements ActionListener {
    private JTextField idField, nameField;
    private JComboBox<String> typeComboBox;
    private JButton signInButton, viewReportButton, clearButton;
    private static final String FILE_NAME = "signin_report.txt";

    /**
     * Constructs the Library Sign-In GUI and initializes all components.
     */
    public LibrarySignInGUI() {
        setTitle("Library Sign-In");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 17);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 15);

        JLabel titleLabel = new JLabel("Library Sign-In Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        addComponent(gbc, labelFont, fieldFont, "ID:", 1, idField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Name:", 2, nameField = new JTextField(20));

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(typeLabel, gbc);

        String[] types = {"Staff", "Reader"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(typeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        signInButton = new JButton("Sign In");
        viewReportButton = new JButton("View Report");
        clearButton = new JButton("Clear Fields");

        styleButton(signInButton);
        styleButton(viewReportButton);
        styleButton(clearButton);

        buttonPanel.add(signInButton);
        buttonPanel.add(viewReportButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        signInButton.addActionListener(this);
        viewReportButton.addActionListener(this);
        clearButton.addActionListener(this);

        setVisible(true);
    }

    /**
     * Adds a labeled text field to the frame using GridBagLayout.
     *
     * @param gbc        layout constraints
     * @param labelFont  font used for the label
     * @param fieldFont  font used for the text field
     * @param labelText  text of the label
     * @param row        row index in the layout
     * @param field      text field component
     */
    private void addComponent(GridBagConstraints gbc, Font labelFont, Font fieldFont, String labelText, int row, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        add(label, gbc);

        field.setFont(fieldFont);
        gbc.gridx = 1;
        add(field, gbc);
    }

    /**
     * Styles a button with color, font, and border.
     *
     * @param button the button to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    /**
     * Handles button click events for sign-in, viewing report, and clearing fields.
     *
     * @param e the event triggered by the user
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {
            signIn();
        } else if (e.getSource() == viewReportButton) {
            exportReport();
        } else if (e.getSource() == clearButton) {
            clearFields();
        }
    }

    /**
     * Signs in a user by writing their information into the report file.
     */
    private void signIn() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String record = String.format("%s,%s,%s,%s%n", id, name, type, java.time.LocalDateTime.now());

        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(record);
            JOptionPane.showMessageDialog(this, "Sign-in recorded successfully!");
            clearFields();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file.");
        }
    }

    /**
     * Exports and displays the sign-in report from the file in a scrollable dialog.
     */
    private void exportReport() {
        StringBuilder report = new StringBuilder();
        try {
            Files.lines(Paths.get(FILE_NAME))
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        if (parts.length >= 4) {
                            report.append(String.format("ID: %-10s Name: %-15s Type: %-7s Signed In At: %s%n",
                                    parts[0], parts[1], parts[2], parts[3]));
                        }
                    });
            JTextArea textArea = new JTextArea(report.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(520, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Library Sign-In Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file or file not found.");
        }
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        typeComboBox.setSelectedIndex(0);
    }
}
