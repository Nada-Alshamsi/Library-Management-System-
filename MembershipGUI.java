package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * MembershipGUI provides a GUI interface to register and manage membership details
 * in a library system. It allows users to input member information, choose membership
 * duration and type, and submit or display membership data stored in a MySQL database.
 */
public class MembershipGUI extends JFrame implements ActionListener {
    private JTextField nameField, startDateField, memberIdField;
    private JComboBox<String> membershipTypeComboBox, durationComboBox;
    private JButton submitButton, displayButton, clearButton;
    private int lastInsertedMemberID = -1;

    /**
     * Constructs the MembershipGUI window and initializes all GUI components and layout.
     */
    public MembershipGUI() {
        setTitle("Membership Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);

        // Form Fields
        addComponent(gbc, labelFont, fieldFont, "Name:", 0, nameField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Member ID:", 1, memberIdField = new JTextField(20));
        addComponent(gbc, labelFont, fieldFont, "Duration:", 2, durationComboBox = new JComboBox<>(new String[]{"3 Months", "6 Months", "12 Months"}));
        addComponent(gbc, labelFont, fieldFont, "Start Date (yyyy-MM-dd):", 3, startDateField = new JTextField(10));
        addComponent(gbc, labelFont, fieldFont, "Membership Type:", 4, membershipTypeComboBox = new JComboBox<>(new String[]{"Regular", "VIP", "Premium"}));

        // Buttons
        submitButton = new JButton("Submit");
        displayButton = new JButton("Display");
        clearButton = new JButton("Clear");

        styleButton(submitButton);
        styleButton(displayButton);
        styleButton(clearButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.add(submitButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Action Listeners
        submitButton.addActionListener(this);
        displayButton.addActionListener(this);
        clearButton.addActionListener(this);

        setVisible(true);
    }

    /**
     * Adds a label and corresponding input component to the frame using GridBagLayout.
     *
     * @param gbc         GridBagConstraints for layout management
     * @param labelFont   Font used for the label
     * @param fieldFont   Font used for the input field
     * @param labelText   Text of the label
     * @param row         Grid row position
     * @param component   Input component to be added
     */
    private void addComponent(GridBagConstraints gbc, Font labelFont, Font fieldFont, String labelText, int row, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        add(label, gbc);

        component.setFont(fieldFont);
        gbc.gridx = 1;
        add(component, gbc);
    }

    /**
     * Styles a JButton with custom font and background color.
     *
     * @param button JButton to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    /**
     * Handles actions performed on the buttons: Submit, Display, and Clear.
     *
     * @param e the event triggered by button actions
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == submitButton) {
            createMembership();
        } else if (src == displayButton) {
            displayMembershipDetails();
        } else if (src == clearButton) {
            clearFields();
        }
    }

    /**
     * Creates a new membership entry in the database using input values.
     */
    private void createMembership() {
        try {
            String name = nameField.getText().trim();
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            String selectedDuration = (String) durationComboBox.getSelectedItem();
            int durationMonths = Integer.parseInt(selectedDuration.split(" ")[0]);
            String startDateStr = startDateField.getText().trim();
            String membershipType = (String) membershipTypeComboBox.getSelectedItem();

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO Membership (MemberID, DurationMonths, StartDate, EndDate, MembershipType, MembershipFee, MembershipStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, memberId);
                ps.setInt(2, durationMonths);
                ps.setDate(3, Date.valueOf(startDateStr));
                ps.setDate(4, Date.valueOf(LocalDate.parse(startDateStr).plusMonths(durationMonths)));
                ps.setString(5, membershipType);
                ps.setInt(6, getMembershipFee(membershipType));
                ps.setString(7, "Active");

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    lastInsertedMemberID = memberId;
                    JOptionPane.showMessageDialog(this, "Membership created successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create membership.");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Displays the most recently inserted membership details in a dialog window.
     */
    private void displayMembershipDetails() {
        if (lastInsertedMemberID == -1) {
            JOptionPane.showMessageDialog(this, "No membership created yet.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Membership WHERE MemberID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, lastInsertedMemberID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("Member ID: ").append(rs.getInt("MemberID"))
                        .append("\nDuration: ").append(rs.getInt("DurationMonths")).append(" months")
                        .append("\nStart Date: ").append(rs.getDate("StartDate"))
                        .append("\nEnd Date: ").append(rs.getDate("EndDate"))
                        .append("\nType: ").append(rs.getString("MembershipType"))
                        .append("\nFee: ").append(rs.getInt("MembershipFee"))
                        .append("\nStatus: ").append(rs.getString("MembershipStatus"));

                showDialog("Membership Details", details.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Membership not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Calculates and returns the membership fee based on the type.
     *
     * @param type The membership type
     * @return the corresponding membership fee
     */
    private int getMembershipFee(String type) {
        switch (type) {
            case "Regular": return 50;
            case "VIP": return 100;
            case "Premium": return 150;
            default: return 0;
        }
    }

    /**
     * Clears all input fields and resets combo boxes to their default selection.
     */
    private void clearFields() {
        nameField.setText("");
        memberIdField.setText("");
        startDateField.setText("");
        durationComboBox.setSelectedIndex(0);
        membershipTypeComboBox.setSelectedIndex(0);
    }

    /**
     * Displays a dialog box with a given title and message.
     *
     * @param title   The title of the dialog
     * @param message The message to display
     */
    private void showDialog(String title, String message) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(20, 50);
        textArea.setText(message);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
