package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * MemberGUI provides a GUI for managing library members, allowing users to
 * add members, borrow and return books, cancel or renew memberships,
 * and display member information. It connects to a MySQL database.
 */
public class MemberGUI extends JFrame implements ActionListener {
    private JTextField nameField, ageField, emailField, memberIdField;
    private JRadioButton threeMonthsButton, sixMonthsButton, twelveMonthsButton;
    private JButton addButton, borrowButton, returnButton, cancelButton, renewButton, displayButton;

    /**
     * Constructs the GUI window and initializes all UI components.
     */
    public MemberGUI() {
        setTitle("Library Member Management");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Library Member Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Member Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);

        nameField = new JTextField(20);
        ageField = new JTextField(20);
        emailField = new JTextField(20);
        memberIdField = new JTextField(20);

        threeMonthsButton = new JRadioButton("3 Months");
        sixMonthsButton = new JRadioButton("6 Months");
        twelveMonthsButton = new JRadioButton("12 Months");

        ButtonGroup durationGroup = new ButtonGroup();
        durationGroup.add(threeMonthsButton);
        durationGroup.add(sixMonthsButton);
        durationGroup.add(twelveMonthsButton);
        threeMonthsButton.setSelected(true);

        JRadioButton[] durationButtons = {threeMonthsButton, sixMonthsButton, twelveMonthsButton};
        for (JRadioButton btn : durationButtons) {
            btn.setBackground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        }

        JLabel[] labels = {
            new JLabel("Name:"), new JLabel("Age:"),
            new JLabel("Email:"), new JLabel("Member ID:"),
            new JLabel("Membership Duration:")
        };
        JTextField[] fields = {nameField, ageField, emailField, memberIdField};

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(labelFont);
            gbc.gridx = 0;
            gbc.gridy = i;
            inputPanel.add(labels[i], gbc);

            if (i == 4) {
                gbc.gridx = 1;
                inputPanel.add(threeMonthsButton, gbc);
                gbc.gridx = 2;
                inputPanel.add(sixMonthsButton, gbc);
                gbc.gridx = 3;
                inputPanel.add(twelveMonthsButton, gbc);
            } else {
                gbc.gridx = 1;
                gbc.gridwidth = 3;
                inputPanel.add(fields[i], gbc);
                gbc.gridwidth = 1;
            }
        }

        add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Member");
        borrowButton = new JButton("Borrow Book");
        returnButton = new JButton("Return Book");
        cancelButton = new JButton("Cancel Membership");
        renewButton = new JButton("Renew Membership");
        displayButton = new JButton("Display Members");

        JButton[] buttons = {addButton, borrowButton, returnButton, cancelButton, renewButton, displayButton};
        for (JButton button : buttons) {
            styleButton(button);
            buttonPanel.add(button);
            button.addActionListener(this);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Applies visual style to a given JButton.
     *
     * @param button the JButton to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
    }

    /**
     * Handles button actions and routes to appropriate method.
     *
     * @param e the ActionEvent triggered by button press
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addMember();
        } else if (e.getSource() == displayButton) {
            displayMembers();
        } else if (e.getSource() == cancelButton) {
            cancelMembership();
        } else if (e.getSource() == renewButton) {
            renewMembership();
        } else if (e.getSource() == borrowButton) {
            JOptionPane.showMessageDialog(this, "Feature not implemented yet (borrowBook).");
        } else if (e.getSource() == returnButton) {
            JOptionPane.showMessageDialog(this, "Feature not implemented yet (returnBook).");
        }
    }

    /**
     * Adds a new member and corresponding membership to the database.
     */
    private void addMember() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String email = emailField.getText().trim();
            int memberId = Integer.parseInt(memberIdField.getText().trim());

            int durationMonths = threeMonthsButton.isSelected() ? 3 : sixMonthsButton.isSelected() ? 6 : 12;

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                String insertMember = "INSERT INTO members (MemberID, Fname, minit, Lname, age, email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psMember = conn.prepareStatement(insertMember);
                psMember.setInt(1, memberId);
                psMember.setString(2, name);
                psMember.setString(3, "");
                psMember.setString(4, "");
                psMember.setInt(5, age);
                psMember.setString(6, email);
                psMember.executeUpdate();

                String insertMembership = "INSERT INTO Membership (MemberID, DurationMonths, StartDate, EndDate, MembershipType, MembershipFee, MembershipStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psMembership = conn.prepareStatement(insertMembership);
                LocalDate now = LocalDate.now();
                psMembership.setInt(1, memberId);
                psMembership.setInt(2, durationMonths);
                psMembership.setDate(3, Date.valueOf(now));
                psMembership.setDate(4, Date.valueOf(now.plusMonths(durationMonths)));
                psMembership.setString(5, "Regular");
                psMembership.setInt(6, durationMonths == 3 ? 30 : (durationMonths == 6 ? 100 : 150));
                psMembership.setString(7, "Active");
                psMembership.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Member added successfully!");
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Age and Member ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays all members in a scrollable text area from the database.
     */
    private void displayMembers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM members";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Name: ").append(rs.getString("Fname")).append(" ").append(rs.getString("Lname"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Member ID: ").append(rs.getInt("MemberID")).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString(), 15, 80);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Member List", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Cancels the membership of the member whose ID is provided.
     */
    private void cancelMembership() {
        String inputId = memberIdField.getText().trim();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Member ID to cancel membership.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String update = "UPDATE Membership SET MembershipStatus = 'Cancelled' WHERE MemberID = ?";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.setInt(1, Integer.parseInt(inputId));
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Membership cancelled for Member ID: " + inputId);
            } else {
                JOptionPane.showMessageDialog(this, "Member ID not found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Renews the membership for a member with the given ID by extending it for 12 months.
     */
    private void renewMembership() {
        String inputId = memberIdField.getText().trim();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Member ID to renew membership.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String update = "UPDATE Membership SET StartDate = ?, EndDate = ?, MembershipStatus = 'Active' WHERE MemberID = ?";
            PreparedStatement ps = conn.prepareStatement(update);
            LocalDate now = LocalDate.now();
            ps.setDate(1, Date.valueOf(now));
            ps.setDate(2, Date.valueOf(now.plusMonths(12)));
            ps.setInt(3, Integer.parseInt(inputId));
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Membership renewed for Member ID: " + inputId);
            } else {
                JOptionPane.showMessageDialog(this, "Member ID not found.");
            }

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
        memberIdField.setText("");
    }
}
