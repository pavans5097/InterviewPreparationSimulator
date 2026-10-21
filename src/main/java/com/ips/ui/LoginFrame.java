package com.ips.ui;
import com.ips.model.User;
import com.ips.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private final JTextField emailField = UIStyle.textField();
    private final JPasswordField passwordField = UIStyle.passwordField();
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        UIStyle.configureFrame(this, "Interview Preparation Simulator | Login", 620, 680);
        setMinimumSize(new Dimension(560, 620));

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIStyle.PANEL);
        card.setBorder(new EmptyBorder(34, 38, 32, 38));
        card.setPreferredSize(new Dimension(450, 500));

        JLabel brand = new JLabel("INTERVIEW PREP");
        brand.setForeground(UIStyle.ACCENT);
        brand.setFont(new Font("SansSerif", Font.BOLD, 13));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = UIStyle.heading("Welcome back");
        JLabel subtitle = new JLabel("Sign in to continue your preparation.");
        subtitle.setForeground(UIStyle.MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(brand);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(UIStyle.fieldLabel("Email address"));
        card.add(Box.createVerticalStrut(8));
        card.add(emailField);
        card.add(Box.createVerticalStrut(20));
        card.add(UIStyle.fieldLabel("Password"));
        card.add(Box.createVerticalStrut(8));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(26));

        JButton loginButton = UIStyle.primaryButton("Sign in");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> login());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerRow.setBackground(UIStyle.PANEL);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel prompt = new JLabel("New here?  ");
        prompt.setForeground(UIStyle.MUTED);
        JButton registerButton = UIStyle.linkButton("Create an account");
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        registerRow.add(prompt);
        registerRow.add(registerButton);
        card.add(registerRow);

        outer.add(card);
        setContentPane(outer);
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        char[] password = passwordField.getPassword();
        try {
            User user = authService.login(emailField.getText(), password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Incorrect email or password.",
                        "Sign-in failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new DashboardFrame(user).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not connect to MySQL. Check that MySQL is running and schema.sql has been executed.\n\n"
                    + ex.getMessage(), "Database connection error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}
