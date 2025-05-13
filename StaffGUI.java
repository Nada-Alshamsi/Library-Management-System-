package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * GUI class for managing staff operations such as adding, viewing, and clearing staff data.
 * Integrates with a MySQL database.
 */
public class StaffGUI extends JFrame implements ActionListener {
    private JTextField nameField, ageField, emailField, staffIdField;
    private JComboBox<String> positionComboBox;
    private JButton addButton, viewButton, clearButton;

    /**
     * Constructs the StaffGUI frame and initializes all GUI components.
     */
    public StaffGUI() {
        setTitle("Staff Management");
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

        JLabel titleLabel = new JLabel("Staff Management Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        addComponent(gbc, labelFont, fieldFont, "Full Name:", 1, nameField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Age:", 2, ageField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Email:", 3, emailField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Staff ID:", 4, staffIdField = new JTextField(20));

        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(positionLabel, gbc);

        String[] positions = {"Librarian", "Manager", "Assistant", "Technician"};
        positionComboBox = new JComboBox<>(positions);
        positionComboBox.setFont(fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(positionComboBox, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        addButton = new JButton("Add Staff");
        viewButton = new JButton("View Staff");
        clearButton = new JButton("Clear Fields");

        styleButton(addButton);
        styleButton(viewButton);
        styleButton(clearButton);

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        clearButton.addActionListener(this);

        setVisible(true);
    }

    /**
     * Adds a label and its corresponding text field to the layout.
     *
     * @param gbc        Layout constraints
     * @param labelFont  Font for the label
     * @param fieldFont  Font for the text field
     * @param labelText  The label text
     * @param row        The row position in the layout
     * @param field      The text field component
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
     * Styles a JButton with consistent colors and fonts.
     *
     * @param button The button to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    /**
     * Handles action events for the buttons.
     *
     * @param e The ActionEvent object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addStaff();
        } else if (e.getSource() == viewButton) {
            viewStaff();
        } else if (e.getSource() == clearButton) {
            clearFields();
        }
    }

    /**
     * Adds a new staff member to the database using the provided input fields.
     */
    private void addStaff() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in both Name and Email.");
                return;
            }

            int age = Integer.parseInt(ageField.getText().trim());
            int staffId = Integer.parseInt(staffIdField.getText().trim());
            String position = (String) positionComboBox.getSelectedItem();

            try (Connection conn = DatabaseConnection.getConnection()) {
                String insertStaff = "INSERT INTO staff (StaffID, Fname, minit, Lname, age, email, position, is_available) VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)";
                PreparedStatement ps = conn.prepareStatement(insertStaff);
                ps.setInt(1, staffId);
                ps.setString(2, name);
                ps.setString(3, "");   // Placeholder for middle initial
                ps.setString(4, "");   // Placeholder for last name
                ps.setInt(5, age);
                ps.setString(6, email);
                ps.setString(7, position);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Staff member added successfully!");
                clearFields();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for age and staff ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Retrieves and displays all staff members from the database.
     */
    private void viewStaff() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM staff";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder("List of Staff Members:\n\n");
            while (rs.next()) {
                sb.append("Name: ").append(rs.getString("Fname"))
                  .append("\nAge: ").append(rs.getInt("age"))
                  .append("\nEmail: ").append(rs.getString("email"))
                  .append("\nStaff ID: ").append(rs.getInt("StaffID"))
                  .append("\nPosition: ").append(rs.getString("position"))
                  .append("\n----------------------------\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(520, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "All Staff Members", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        emailField.setText("");
        staffIdField.setText("");
        positionComboBox.setSelectedIndex(0);
    }
}
