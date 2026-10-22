package com.ips;

import com.ips.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Swing will use its default look and feel if the system one is unavailable.
            }
            new LoginFrame().setVisible(true);
        });
    }
}
