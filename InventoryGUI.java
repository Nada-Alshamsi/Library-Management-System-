package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * InventoryGUI is a graphical user interface for managing the book inventory of a library system.
 * It allows users to add, remove, list books and check the inventory status by interacting with a MySQL database.
 */
public class InventoryGUI extends JFrame implements ActionListener {
    private JTextField titleField, authorField, copiesField;
    private JButton addButton, removeButton, listButton, statusButton;
    private JTextArea outputArea;

    /**
     * Constructs the InventoryGUI and sets up the layout, components, and event handling.
     */
    public InventoryGUI() {
        setTitle("Library Inventory");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 13);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Info"));
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(labelFont);
        titleField = new JTextField(20);
        titleField.setFont(fieldFont);

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(labelFont);
        authorField = new JTextField(20);
        authorField.setFont(fieldFont);

        JLabel copiesLabel = new JLabel("Number of Copies:");
        copiesLabel.setFont(labelFont);
        copiesField = new JTextField(20);
        copiesField.setFont(fieldFont);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(authorLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(copiesLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(copiesField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        listButton = new JButton("List");
        statusButton = new JButton("Status");

        JButton[] buttons = {addButton, removeButton, listButton, statusButton};
        for (JButton button : buttons) {
            styleButton(button);
            button.setPreferredSize(new Dimension(100, 45));
            buttonPanel.add(button);
            button.addActionListener(this);
        }

        outputArea = new JTextArea(8, 50);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Styles the given JButton with consistent font and color settings.
     *
     * @param button The JButton to style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    /**
     * Handles button actions for add, remove, list, and status functionality.
     *
     * @param e The ActionEvent triggered by button clicks.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addBook();
        } else if (e.getSource() == removeButton) {
            removeBook();
        } else if (e.getSource() == listButton) {
            listBooks();
        } else if (e.getSource() == statusButton) {
            displayInventoryStatus();
        }
    }

    /**
     * Adds a book to the library database and inventory.
     */
    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String copies = copiesField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || copies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertBook = "INSERT INTO book (title, author) VALUES (?, ?)";
            PreparedStatement psBook = conn.prepareStatement(insertBook);
            psBook.setString(1, title);
            psBook.setString(2, author);
            int rows = psBook.executeUpdate();

            if (rows > 0) {
                String insertInventory = "INSERT INTO inventory (bookID, total_copies, available_copies) VALUES (LAST_INSERT_ID(), ?, ?)";
                PreparedStatement psInventory = conn.prepareStatement(insertInventory);
                psInventory.setInt(1, Integer.parseInt(copies));
                psInventory.setInt(2, Integer.parseInt(copies));
                psInventory.executeUpdate();

                outputArea.setText("Book added successfully: " + title);
            } else {
                outputArea.setText("Error adding book.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            outputArea.setText("Database error: " + ex.getMessage());
        }
    }

    /**
     * Removes a book and its inventory record from the database based on the title.
     */
    private void removeBook() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            outputArea.setText("Error: Title cannot be empty.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteInventory = "DELETE i FROM inventory i JOIN book b ON i.bookID = b.bookID WHERE b.title = ?";
            PreparedStatement psInventory = conn.prepareStatement(deleteInventory);
            psInventory.setString(1, title);
            psInventory.executeUpdate();

            String deleteBook = "DELETE FROM book WHERE title = ?";
            PreparedStatement psBook = conn.prepareStatement(deleteBook);
            psBook.setString(1, title);
            int rows = psBook.executeUpdate();

            if (rows > 0) {
                outputArea.setText("Book removed successfully: " + title);
            } else {
                outputArea.setText("Book not found: " + title);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            outputArea.setText("Database error: " + ex.getMessage());
        }
    }

    /**
     * Lists all books with their total and available copies.
     */
    private void listBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.title, b.author, i.total_copies, i.available_copies FROM book b JOIN inventory i ON b.bookID = i.bookID";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder bookList = new StringBuilder("Books List:\n\n");
            while (rs.next()) {
                bookList.append("Title: ").append(rs.getString("title"))
                        .append(", Author: ").append(rs.getString("author"))
                        .append(", Total Copies: ").append(rs.getInt("total_copies"))
                        .append(", Available: ").append(rs.getInt("available_copies"))
                        .append("\n");
            }
            outputArea.setText(bookList.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
            outputArea.setText("Database error: " + ex.getMessage());
        }
    }

    /**
     * Displays overall inventory statistics including total books and copies.
     */
    private void displayInventoryStatus() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total_books, SUM(total_copies) AS total_copies, SUM(available_copies) AS available_copies FROM inventory";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                outputArea.setText("Inventory Status\n"
                        + "Total books: " + rs.getInt("total_books") + "\n"
                        + "Total copies: " + rs.getInt("total_copies") + "\n"
                        + "Available copies: " + rs.getInt("available_copies"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            outputArea.setText("Database error: " + ex.getMessage());
        }
    }
}
