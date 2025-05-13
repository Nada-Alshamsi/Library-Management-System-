package oop2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * BookGUI is a Swing-based graphical user interface for managing books in a library system.
 * It allows adding, borrowing, returning, displaying, and deleting books using a MySQL database.
 */
public class BookGUI extends JFrame implements ActionListener {
    private JTextField titleField, authorField, copiesField;
    private JButton addButton, borrowButton, returnButton, displayButton, deleteButton;

    /**
     * Constructs the BookGUI window with input fields and buttons for book management.
     */
    public BookGUI() {
        setTitle("Book Management");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);

        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        add(titleLabel, gbc);

        titleField = new JTextField(20);
        titleField.setFont(fieldFont);
        gbc.gridx = 1;
        add(titleField, gbc);

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1;
        add(authorLabel, gbc);

        authorField = new JTextField(20);
        authorField.setFont(fieldFont);
        gbc.gridx = 1;
        add(authorField, gbc);

        JLabel copiesLabel = new JLabel("Copies:");
        copiesLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2;
        add(copiesLabel, gbc);

        copiesField = new JTextField(5);
        copiesField.setFont(fieldFont);
        gbc.gridx = 1;
        add(copiesField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(10, 10, 10, 10);
        gbcButtons.fill = GridBagConstraints.BOTH;

        addButton = new JButton("Add Book");
        borrowButton = new JButton("Borrow Book");
        returnButton = new JButton("Return Book");
        displayButton = new JButton("Display Books");
        deleteButton = new JButton("Delete Book");

        JButton[] buttons = {addButton, borrowButton, returnButton, displayButton, deleteButton};
        for (JButton button : buttons) {
            styleButton(button);
            button.addActionListener(this);
        }

        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        buttonPanel.add(addButton, gbcButtons);

        gbcButtons.gridx = 1;
        buttonPanel.add(borrowButton, gbcButtons);

        gbcButtons.gridx = 0; gbcButtons.gridy = 1;
        buttonPanel.add(returnButton, gbcButtons);

        gbcButtons.gridx = 1;
        buttonPanel.add(displayButton, gbcButtons);

        gbcButtons.gridx = 0; gbcButtons.gridy = 2;
        gbcButtons.gridwidth = 2;
        buttonPanel.add(deleteButton, gbcButtons);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setVisible(true);
    }

    /**
     * Styles a JButton with a custom look and feel.
     *
     * @param button the JButton to be styled
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 60));
    }

    /**
     * Handles button click events and routes them to the appropriate methods.
     *
     * @param e the ActionEvent triggered by a button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addBook();
        } else if (e.getSource() == borrowButton) {
            borrowBook();
        } else if (e.getSource() == returnButton) {
            returnBook();
        } else if (e.getSource() == displayButton) {
            displayBooks();
        } else if (e.getSource() == deleteButton) {
            deleteBook();
        }
    }

    /**
     * Adds a new book to the database along with its inventory data.
     */
    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String copiesText = copiesField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || copiesText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Author, and Copies cannot be empty.");
            return;
        }

        int copies;
        try {
            copies = Integer.parseInt(copiesText);
            if (copies <= 0) {
                JOptionPane.showMessageDialog(this, "Copies must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Copies must be a valid number.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertBook = "INSERT INTO book (bookID, title, author, is_available) VALUES (?, ?, ?, TRUE)";
            PreparedStatement psBook = conn.prepareStatement(insertBook);
            int newBookId = generateNewBookID(conn);
            psBook.setInt(1, newBookId);
            psBook.setString(2, title);
            psBook.setString(3, author);
            psBook.executeUpdate();

            String insertInventory = "INSERT INTO inventory (bookID, total_copies, available_copies) VALUES (?, ?, ?)";
            PreparedStatement psInventory = conn.prepareStatement(insertInventory);
            psInventory.setInt(1, newBookId);
            psInventory.setInt(2, copies);
            psInventory.setInt(3, copies);
            psInventory.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book added successfully!");
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Borrows a book by decreasing its available copies in the inventory.
     */
    private void borrowBook() {
        String title = titleField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String update = "UPDATE inventory i JOIN book b ON i.bookID = b.bookID " +
                            "SET i.available_copies = i.available_copies - 1 " +
                            "WHERE b.title = ? AND i.available_copies > 0";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.setString(1, title);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Book not available or not found!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Returns a book by increasing its available copies in the inventory.
     */
    private void returnBook() {
        String title = titleField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String update = "UPDATE inventory i JOIN book b ON i.bookID = b.bookID " +
                            "SET i.available_copies = i.available_copies + 1 WHERE b.title = ?";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.setString(1, title);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Deletes a book and its inventory entry from the database.
     */
    private void deleteBook() {
        String title = titleField.getText().trim();

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
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Displays a list of books from the database along with their inventory details.
     */
    private void displayBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.title, b.author, i.total_copies, i.available_copies " +
                           "FROM book b JOIN inventory i ON b.bookID = i.bookID";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder bookList = new StringBuilder("Book List:\n\n");
            while (rs.next()) {
                bookList.append("Title: ").append(rs.getString("title"))
                        .append(" | Author: ").append(rs.getString("author"))
                        .append(" | Copies: ").append(rs.getInt("total_copies"))
                        .append(" | Available: ").append(rs.getInt("available_copies"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this, bookList.toString(), "Books", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Clears the input fields.
     */
    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        copiesField.setText("");
    }

    /**
     * Generates a new book ID by retrieving the maximum existing bookID from the database.
     *
     * @param conn the active database connection
     * @return a new unique book ID
     * @throws SQLException if a database access error occurs
     */
    private int generateNewBookID(Connection conn) throws SQLException {
        String query = "SELECT MAX(bookID) AS max_id FROM book";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt("max_id") != 0) {
            return rs.getInt("max_id") + 1;
        } else {
            return 1;
        }
    }
}
