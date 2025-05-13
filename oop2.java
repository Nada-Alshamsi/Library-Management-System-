package oop2;

import java.awt.*;
import javax.swing.*;

public class oop2 {
    private static JFrame mainWindow = null;

    /**
     * The main method launches the welcome screen of the Library Management System.
     * All GUI logic is defined inside this method using lambdas and inner classes.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            /**
             * Displays the initial welcome screen with an image and a progress bar.
             * After loading, it automatically transitions to the main menu.
             */
            JWindow window = new JWindow();
            window.setSize(600, 400);
            window.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());

            ImageIcon originalIcon = new ImageIcon("C:\\Users\\khawl\\Desktop\\Screenshot 2025-03-28 022217.png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

            JLabel label = new JLabel("Welcome!", JLabel.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 40));
            label.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            label.setForeground(Color.BLUE);

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setBackground(Color.WHITE);
            progressBar.setForeground(new Color(100, 149, 237));

            Timer timer = new Timer(40, e -> {
                int value = progressBar.getValue();
                if (value < 100) {
                    progressBar.setValue(value + 1);
                } else {
                    window.dispose();

                    /**
                     * Displays the main menu with Member, Staff, and Exit options.
                     */
                    if (mainWindow == null) {
                        mainWindow = new JFrame("Library System");
                        mainWindow.setSize(700, 550);
                        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        mainWindow.setLocationRelativeTo(null);

                        JPanel mainPanel = new JPanel(new BorderLayout());
                        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
                        mainPanel.setBackground(Color.WHITE);

                        JLabel title = new JLabel("Choose one of the following options:", SwingConstants.CENTER);
                        title.setFont(new Font("SansSerif", Font.BOLD, 26));
                        title.setForeground(new Color(60, 60, 60));
                        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                        mainPanel.add(title, BorderLayout.NORTH);

                        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 30, 30));
                        buttonPanel.setBackground(Color.WHITE);

                        JButton memberButton = new JButton("Member");
                        JButton staffButton = new JButton("Staff");
                        JButton exitButton = new JButton("Exit");

                        for (JButton btn : new JButton[]{memberButton, staffButton, exitButton}) {
                            btn.setFont(new Font("SansSerif", Font.BOLD, 22));
                            btn.setBackground(new Color(100, 149, 237));
                            btn.setForeground(Color.WHITE);
                            btn.setFocusPainted(false);
                            btn.setPreferredSize(new Dimension(300, 60));
                        }

                        memberButton.addActionListener(ev -> {
                            JFrame optionFrame = new JFrame("Membership Options");
                            optionFrame.setSize(700, 500);
                            optionFrame.setLocationRelativeTo(null);
                            optionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                            JPanel optPanel = new JPanel(new BorderLayout());
                            optPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
                            optPanel.setBackground(Color.WHITE);

                            JLabel optTitle = new JLabel("Choose one of the following options:", SwingConstants.CENTER);
                            optTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
                            optTitle.setForeground(new Color(60, 60, 60));
                            optTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                            optPanel.add(optTitle, BorderLayout.NORTH);

                            JPanel optButtons = new JPanel(new GridLayout(3, 1, 30, 30));
                            optButtons.setBackground(Color.WHITE);

                            JButton signInBtn = new JButton("Sign In");
                            JButton signUpBtn = new JButton("Sign Up");
                            JButton backBtn = new JButton("Back");

                            for (JButton btn : new JButton[]{signInBtn, signUpBtn, backBtn}) {
                                btn.setFont(new Font("SansSerif", Font.BOLD, 22));
                                btn.setBackground(new Color(100, 149, 237));
                                btn.setForeground(Color.WHITE);
                                btn.setFocusPainted(false);
                                btn.setPreferredSize(new Dimension(300, 60));
                            }

                            signInBtn.addActionListener(event -> {
                                optionFrame.dispose();
                                MemberGUI memberGUI = new MemberGUI();
                                memberGUI.setVisible(true);
                            });

                            signUpBtn.addActionListener(event -> {
                                optionFrame.dispose();
                                MembershipGUI membershipGUI = new MembershipGUI();
                                membershipGUI.setVisible(true);
                            });

                            backBtn.addActionListener(event -> optionFrame.dispose());

                            optButtons.add(signInBtn);
                            optButtons.add(signUpBtn);
                            optButtons.add(backBtn);

                            optPanel.add(optButtons, BorderLayout.CENTER);
                            optionFrame.add(optPanel);
                            optionFrame.setVisible(true);
                        });

                        staffButton.addActionListener(ev -> {
                            JFrame staffFrame = new JFrame("Staff Management");
                            staffFrame.setSize(700, 550);
                            staffFrame.setLocationRelativeTo(null);
                            staffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                            JPanel staffPanel = new JPanel(new BorderLayout());
                            staffPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
                            staffPanel.setBackground(Color.WHITE);

                            JLabel staffTitle = new JLabel("Choose one of the following options:", SwingConstants.CENTER);
                            staffTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
                            staffTitle.setForeground(new Color(60, 60, 60));
                            staffTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                            staffPanel.add(staffTitle, BorderLayout.NORTH);

                            JPanel staffButtons = new JPanel(new GridLayout(5, 1, 25, 25));
                            staffButtons.setBackground(Color.WHITE);

                            JButton addStaff = new JButton("Add Staff");
                            JButton addRemoveBook = new JButton("Add/Remove Book");
                            JButton manageBooks = new JButton("Manage Books");
                            JButton librarySignIn = new JButton("Library Sign-In");
                            JButton backBtn = new JButton("Back");

                            for (JButton btn : new JButton[]{addStaff, addRemoveBook, manageBooks, librarySignIn, backBtn}) {
                                btn.setFont(new Font("SansSerif", Font.BOLD, 22));
                                btn.setBackground(new Color(100, 149, 237));
                                btn.setForeground(Color.WHITE);
                                btn.setFocusPainted(false);
                                btn.setPreferredSize(new Dimension(300, 60));
                            }

                            addStaff.addActionListener(event -> {
                                StaffGUI staffGUI = new StaffGUI();
                                staffGUI.setVisible(true);
                            });

                            addRemoveBook.addActionListener(event -> {
                                BookGUI bookGUI = new BookGUI();
                                bookGUI.setVisible(true);
                            });

                            manageBooks.addActionListener(event -> {
                                InventoryGUI inventoryGUI = new InventoryGUI();
                                inventoryGUI.setVisible(true);
                            });

                            librarySignIn.addActionListener(event -> {
                                LibrarySignInGUI signInGUI = new LibrarySignInGUI();
                                signInGUI.setVisible(true);
                            });

                            backBtn.addActionListener(event -> staffFrame.dispose());

                            staffButtons.add(addStaff);
                            staffButtons.add(addRemoveBook);
                            staffButtons.add(manageBooks);
                            staffButtons.add(librarySignIn);
                            staffButtons.add(backBtn);

                            staffPanel.add(staffButtons, BorderLayout.CENTER);
                            staffFrame.add(staffPanel);
                            staffFrame.setVisible(true);
                        });

                        exitButton.addActionListener(ev -> System.exit(0));

                        buttonPanel.add(memberButton);
                        buttonPanel.add(staffButton);
                        buttonPanel.add(exitButton);

                        mainPanel.add(buttonPanel, BorderLayout.CENTER);
                        mainWindow.add(mainPanel);
                        mainWindow.setVisible(true);
                    }
                }
            });
            timer.start();

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(label, BorderLayout.CENTER);
            bottomPanel.add(progressBar, BorderLayout.SOUTH);

            panel.add(imageLabel, BorderLayout.CENTER);
            panel.add(bottomPanel, BorderLayout.SOUTH);

            window.add(panel);
            window.setVisible(true);
            window.setAlwaysOnTop(true);
            window.toFront();
        });
    }
}
