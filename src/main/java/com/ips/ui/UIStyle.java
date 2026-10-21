package com.ips.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class UIStyle {
    // A dark charcoal and teal colour theme used throughout the application.
    static final Color BACKGROUND = new Color(15, 25, 31);
    static final Color PANEL = new Color(27, 43, 50);
    static final Color ACCENT = new Color(20, 184, 166);
    static final Color TEXT = new Color(239, 248, 247);
    static final Color MUTED = new Color(169, 194, 195);
    static final Color FIELD = new Color(13, 23, 29);
    static final Color BORDER = new Color(55, 83, 87);

    private UIStyle() { }

    static void configureFrame(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(500, 520));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND);
    }

    static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 28));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static JTextField textField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBackground(FIELD);
        field.setForeground(TEXT);
        field.setCaretColor(ACCENT);
        field.setSelectionColor(new Color(34, 105, 101));
        field.setSelectedTextColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(350, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(new Color(8, 28, 29));
        button.setBackground(ACCENT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setPreferredSize(new Dimension(160, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(ACCENT);
        button.setBackground(PANEL);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
