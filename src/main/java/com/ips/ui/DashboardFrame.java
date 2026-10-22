package com.ips.ui;

import com.ips.dao.QuestionDAO;
import com.ips.dao.FeatureDAO;
import com.ips.model.Question;
import com.ips.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final User user;

    public DashboardFrame(User user) {
        this.user = user;
        UIStyle.configureFrame(this, "Interview Preparation Simulator | Dashboard", 1180, 780);
        setMinimumSize(new Dimension(980, 680));

        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(UIStyle.BACKGROUND);
        root.setBorder(new EmptyBorder(26, 30, 22, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.BACKGROUND);
        JPanel titles = new JPanel();
        titles.setBackground(UIStyle.BACKGROUND);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("YOUR WORKSPACE");
        eyebrow.setForeground(UIStyle.ACCENT);
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel welcome = UIStyle.heading("Hello, " + user.getFullName());
        JLabel email = new JLabel(user.getEmail());
        email.setForeground(UIStyle.MUTED);
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(8));
        titles.add(welcome);
        titles.add(Box.createVerticalStrut(5));
        titles.add(email);

        JButton logout = UIStyle.primaryButton("Log out");
        logout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        header.add(titles, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);

        JPanel content = new JPanel(new GridLayout(2, 3, 18, 18));
        content.setBorder(new EmptyBorder(24, 0, 24, 0));
        content.setBackground(UIStyle.BACKGROUND);
        content.add(featureCard("Practice questions", "Choose a subject and answer questions at your own pace.", "Start practice", this::openPractice));
        content.add(featureCard("Mock interview", "Timed randomized interview with answer review.", "Start mock interview", this::openMockInterview));
        content.add(featureCard("Performance analytics", "See average score, accuracy, and subject-wise performance.", "View analytics", () -> new AnalyticsFrame(user).setVisible(true)));
        content.add(featureCard("Saved questions", "Bookmark tricky questions and practice them again.", "Open bookmarks", () -> new BookmarksFrame(user).setVisible(true)));
        content.add(featureCard("Attempt history", "Review previous practice and mock interview scores.", "View history", () -> new HistoryFrame(user).setVisible(true)));
        String adminEmail = System.getenv().getOrDefault("IPS_ADMIN_EMAIL", "admin@ips.local");
        if (user.getEmail().equalsIgnoreCase(adminEmail)) {
            content.add(featureCard("Admin question bank", "Add, edit, and delete questions in the MySQL question bank.", "Manage questions", () -> new AdminFrame().setVisible(true)));
        } else {
            content.add(featureCard("Keep improving", "Find your weakest subject from your saved answers and practise it next.", "Practise weak subject", this::openImprovement));
        }

        JLabel footer = new JLabel("Java Swing • JDBC • MySQL   |   Your progress is saved automatically.");
        footer.setForeground(UIStyle.MUTED);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));

        root.add(header, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel featureCard(String title, String description, String buttonText, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(UIStyle.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, UIStyle.ACCENT),
                new EmptyBorder(20, 20, 18, 20)));
        card.setMinimumSize(new Dimension(240, 180));

        JLabel heading = new JLabel(title);
        heading.setForeground(UIStyle.TEXT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        JTextArea body = new JTextArea(description);
        body.setForeground(UIStyle.MUTED);
        body.setBackground(UIStyle.PANEL);
        body.setFont(new Font("SansSerif", Font.PLAIN, 13));
        body.setLineWrap(true);
        body.setOpaque(false);
        body.setWrapStyleWord(true);
        body.setEditable(false);
        body.setFocusable(false);
        body.setBorder(null);

        card.add(heading, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        if (buttonText != null) {
            JButton start = UIStyle.primaryButton(buttonText);
            start.addActionListener(e -> action.run());
            card.add(start, BorderLayout.SOUTH);
        } else {
            JLabel planned = new JLabel("Coming soon");
            planned.setForeground(UIStyle.ACCENT);
            card.add(planned, BorderLayout.SOUTH);
        }
        return card;
    }

    private String chooseCategory(List<String> categories, String title) {
        JComboBox<String> combo = new JComboBox<>(categories.toArray(new String[0]));
        int result = JOptionPane.showConfirmDialog(this, combo, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION ? (String) combo.getSelectedItem() : null;
    }

    private List<String> getCategories() throws SQLException {
        List<String> categories = new QuestionDAO().getCategories();
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No questions found. Run database/schema.sql in MySQL Workbench.",
                    "No questions", JOptionPane.WARNING_MESSAGE);
        }
        return categories;
    }

    private void openImprovement() {
        try {
            List<String[]> results = new FeatureDAO().subjectStats(user.getId());
            if (results.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "You do not have any saved answers yet. Start a practice quiz to get a personalised recommendation?",
                        "Keep improving", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) openPractice();
                return;
            }

            String weakestSubject = null;
            double lowestAccuracy = Double.MAX_VALUE;
            int attempted = 0;
            for (String[] row : results) {
                int subjectAttempted = Integer.parseInt(row[1]);
                double accuracy = Double.parseDouble(row[3]);
                if (subjectAttempted > 0 && accuracy < lowestAccuracy) {
                    lowestAccuracy = accuracy;
                    weakestSubject = row[0];
                    attempted = subjectAttempted;
                }
            }

            if (weakestSubject == null) {
                JOptionPane.showMessageDialog(this,
                        "Complete a practice quiz or mock interview first to get a recommendation.",
                        "Keep improving", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(this,
                    "Your recommended subject is: " + weakestSubject + "\nAccuracy: "
                            + String.format(java.util.Locale.US, "%.1f%%", lowestAccuracy)
                            + "\nAnswers recorded: " + attempted
                            + "\n\nWould you like to practise this subject now?",
                    "Your next step", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                List<Question> questions = new QuestionDAO().getQuestions(weakestSubject);
                if (questions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No questions are available for " + weakestSubject + ".");
                    return;
                }
                new PracticeFrame(user, weakestSubject, questions).setVisible(true);
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not generate a recommendation. Check the database connection and saved results.\n"
                            + ex.getMessage(),
                    "Recommendation error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPractice() {
        try {
            List<String> categories = getCategories();
            if (categories.isEmpty()) return;
            String category = chooseCategory(categories, "Choose practice subject");
            if (category == null) return;
            List<Question> questions = new QuestionDAO().getQuestions(category);
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available for this subject.");
                return;
            }
            new PracticeFrame(user, category, questions).setVisible(true);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void openMockInterview() {
        try {
            List<String> categories = getCategories();
            if (categories.isEmpty()) return;

            JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
            JComboBox<String> category = new JComboBox<>();
            category.addItem("All subjects");
            categories.forEach(category::addItem);
            JComboBox<String> count = new JComboBox<>(new String[]{"5", "10", "15", "20"});
            JComboBox<String> duration = new JComboBox<>(new String[]{"5", "10", "15", "20", "30"});
            form.add(new JLabel("Subject"));
            form.add(category);
            form.add(new JLabel("Number of questions"));
            form.add(count);

            JPanel wrapper = new JPanel(new BorderLayout(0, 12));
            wrapper.add(form, BorderLayout.NORTH);
            JPanel durationRow = new JPanel(new GridLayout(1, 2, 8, 0));
            durationRow.add(new JLabel("Duration (minutes)"));
            durationRow.add(duration);
            wrapper.add(durationRow, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, wrapper,
                    "Configure mock interview", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String selectedCategory = (String) category.getSelectedItem();
            List<Question> pool = new java.util.ArrayList<>();
            if ("All subjects".equals(selectedCategory)) {
                for (String c : categories) pool.addAll(new QuestionDAO().getQuestions(c));
                java.util.Collections.shuffle(pool);
            } else {
                pool = new QuestionDAO().getQuestions(selectedCategory);
            }

            int requested = Integer.parseInt((String) count.getSelectedItem());
            if (pool.size() < requested) {
                int answer = JOptionPane.showConfirmDialog(this,
                        "Only " + pool.size() + " questions are available for this selection. Use all of them?",
                        "Not enough questions", JOptionPane.YES_NO_OPTION);
                if (answer != JOptionPane.YES_OPTION) return;
                requested = pool.size();
            }
            if (requested == 0) return;
            List<Question> selected = new java.util.ArrayList<>(pool.subList(0, requested));
            int minutes = Integer.parseInt((String) duration.getSelectedItem());
            new MockInterviewFrame(user, selected, minutes).setVisible(true);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Could not load questions from MySQL.\nCheck that the schema has been executed.\n\n"
                        + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    }
}
