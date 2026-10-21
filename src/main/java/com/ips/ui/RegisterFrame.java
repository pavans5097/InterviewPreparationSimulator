package com.ips.ui;

import com.ips.model.User;
import com.ips.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class RegisterFrame extends JFrame {
    private final JTextField nameField = UIStyle.textField();
    private final JTextField emailField = UIStyle.textField();
    private final JPasswordField passwordField = UIStyle.passwordField();
    private final JPasswordField confirmField = UIStyle.passwordField();
    private final AuthService authService = new AuthService();

    public RegisterFrame() {
        UIStyle.configureFrame(this, "Interview Preparation Simulator | Register", 540, 700);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel card = new JPanel();
        card.setBackground(UIStyle.PANEL);
        card.setBorder(new EmptyBorder(26, 28, 26, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("INTERVIEW PREP");
        brand.setForeground(UIStyle.ACCENT);
        brand.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel title = UIStyle.heading("Create your account");
        JLabel subtitle = new JLabel("Start building your interview skills.");
        subtitle.setForeground(UIStyle.MUTED);

        card.add(brand);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));

        addField(card, "Full name", nameField);
        addField(card, "Email address", emailField);
        addField(card, "Password (at least 8 characters)", passwordField);
        addField(card, "Confirm password", confirmField);

        JButton registerButton = UIStyle.primaryButton("Create account");
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> register());
        card.add(Box.createVerticalStrut(8));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(12));

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setBackground(UIStyle.PANEL);
        JLabel prompt = new JLabel("Already registered? ");
        prompt.setForeground(UIStyle.MUTED);
        JButton loginButton = UIStyle.linkButton("Back to sign in");
        loginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        loginRow.add(prompt);
        loginRow.add(loginButton);
        card.add(loginRow);

        outer.add(card);
        setContentPane(outer);
    }

    private void addField(JPanel panel, String label, JComponent field) {
        panel.add(UIStyle.fieldLabel(label));
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void register() {
        char[] password = passwordField.getPassword();
        char[] confirm = confirmField.getPassword();

        try {
            User user = authService.register(nameField.getText(), emailField.getText(),
                    password, confirm);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully. Welcome, " + user.getFullName() + "!",
                    "Registration complete", JOptionPane.INFORMATION_MESSAGE);
            new DashboardFrame(user).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Check your details", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not save your account to MySQL.\n"
                    + "Check the server, schema, and database credentials.\n\n"
                    + "Details: " + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirm, '\0');
            passwordField.setText("");
            confirmField.setText("");
        }
    }
}
