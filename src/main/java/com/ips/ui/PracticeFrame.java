package com.ips.ui;

import com.ips.dao.AttemptDAO;
import com.ips.dao.QuestionDAO;
import com.ips.model.Question;
import com.ips.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PracticeFrame extends JFrame {
    private final User user;
    private final List<Question> questions;
    private final List<String> answers = new ArrayList<>();
    private final List<Integer> secondsTaken = new ArrayList<>();
    private final JLabel counter = new JLabel();
    private final JLabel timerLabel = new JLabel();
    private final JTextArea questionText = new JTextArea();
    private final JRadioButton[] choices = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private final JButton previousButton = new JButton("Previous");
    private final JButton nextButton = new JButton("Next");
    private final JLabel feedback = new JLabel(" ");
    private final long[] questionStartMillis;
    private int index = 0;
    private int remainingSeconds = 15 * 60;
    private final Timer timer;
    private boolean finished = false;

    public PracticeFrame(User user, String category, List<Question> questions) {
        this.user = user;
        this.questions = questions;
        for (int i = 0; i < questions.size(); i++) {
            answers.add(null);
            secondsTaken.add(0);
        }
        questionStartMillis = new long[questions.size()];
        questionStartMillis[0] = System.currentTimeMillis();

        UIStyle.configureFrame(this, "Practice Questions | " + category, 820, 650);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(UIStyle.BACKGROUND);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIStyle.BACKGROUND);
        JLabel title = UIStyle.heading(category + " Practice");
        counter.setForeground(UIStyle.MUTED);
        timerLabel.setForeground(new Color(255, 205, 100));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 4));
        right.setBackground(UIStyle.BACKGROUND);
        right.add(counter);
        right.add(timerLabel);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        JPanel card = new JPanel();
        card.setBackground(UIStyle.PANEL);
        card.setBorder(new EmptyBorder(22, 22, 22, 22));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        questionText.setEditable(false);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setFocusable(false);
        questionText.setForeground(UIStyle.TEXT);
        questionText.setBackground(UIStyle.PANEL);
        questionText.setFont(new Font("SansSerif", Font.BOLD, 19));
        questionText.setBorder(null);
        questionText.setRows(4);
        card.add(questionText);
        card.add(Box.createVerticalStrut(20));

        for (int i = 0; i < choices.length; i++) {
            choices[i] = new JRadioButton();
            choices[i].setForeground(UIStyle.TEXT);
            choices[i].setBackground(UIStyle.PANEL);
            choices[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            choices[i].setFocusPainted(false);
            choices[i].putClientProperty("letter", String.valueOf((char) ('A' + i)));
            group.add(choices[i]);
            card.add(choices[i]);
            card.add(Box.createVerticalStrut(12));
        }

        feedback.setForeground(UIStyle.MUTED);
        card.add(Box.createVerticalStrut(8));
        card.add(feedback);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIStyle.BACKGROUND);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(UIStyle.BACKGROUND);
        previousButton.addActionListener(e -> move(-1));
        nextButton.addActionListener(e -> {
            if (index == questions.size() - 1) finishQuiz();
            else move(1);
        });
        previousButton.setEnabled(false);
        buttons.add(previousButton);
        buttons.add(nextButton);
        JButton bookmark = UIStyle.linkButton("☆ Bookmark question");
        bookmark.addActionListener(e -> toggleBookmark(bookmark));
        buttons.add(bookmark, 0);
        JButton exit = UIStyle.linkButton("Exit practice");
        exit.addActionListener(e -> confirmExit());
        bottom.add(exit, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);

        timer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimer();
            if (remainingSeconds <= 0) finishQuiz();
        });
        timer.start();
        showQuestion();
    }

    private void showQuestion() {
        Question q = questions.get(index);
        counter.setText("Question " + (index + 1) + " of " + questions.size()
                + "  •  " + q.getDifficulty());
        questionText.setText(q.getQuestionText());
        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            String letter = String.valueOf((char) ('A' + i));
            choices[i].setText(letter + ". " + q.getOption(letter));
            choices[i].setSelected(letter.equals(answers.get(index)));
        }
        feedback.setText(" ");
        previousButton.setEnabled(index > 0);
        nextButton.setText(index == questions.size() - 1 ? "Submit quiz" : "Next");
    }

    private void storeCurrentAnswerAndTime() {
        String selected = null;
        for (JRadioButton choice : choices) {
            if (choice.isSelected()) {
                selected = (String) choice.getClientProperty("letter");
                break;
            }
        }
        answers.set(index, selected);
        int elapsed = (int) Math.max(0, (System.currentTimeMillis() - questionStartMillis[index]) / 1000);
        secondsTaken.set(index, secondsTaken.get(index) + elapsed);
    }

    private void move(int amount) {
        if (finished) return;
        storeCurrentAnswerAndTime();
        index += amount;
        questionStartMillis[index] = System.currentTimeMillis();
        showQuestion();
    }

    private void updateTimer() {
        int minutes = Math.max(0, remainingSeconds) / 60;
        int seconds = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("Time left: %02d:%02d", minutes, seconds));
    }

    private void confirmExit() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Exit this practice session? Your current attempt will not be saved.",
                "Exit practice", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            timer.stop();
            finished = true;
            dispose();
        }
    }

    private void toggleBookmark(JButton button) {
        try {
            com.ips.dao.FeatureDAO dao = new com.ips.dao.FeatureDAO();
            dao.toggleBookmark(user.getId(), questions.get(index).getId());
            boolean saved = dao.isBookmarked(user.getId(), questions.get(index).getId());
            button.setText(saved ? "★ Bookmarked" : "☆ Bookmark question");
            JOptionPane.showMessageDialog(this, saved ? "Question saved to bookmarks." : "Bookmark removed.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not update bookmark: " + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finishQuiz() {
        if (finished) return;
        storeCurrentAnswerAndTime();
        finished = true;
        timer.stop();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers.get(i) != null &&
                    answers.get(i).equals(questions.get(i).getCorrectOption())) score++;
        }

        try {
            new AttemptDAO().saveAttempt(user.getId(), "Practice", score, questions, answers, secondsTaken);
            String message = "Quiz completed!\nScore: " + score + " / " + questions.size()
                    + "\nAccuracy: " + Math.round(score * 100.0 / questions.size()) + "%"
                    + "\n\nYour result has been saved to MySQL.";
            JOptionPane.showMessageDialog(this, message, "Practice result",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Score: " + score + " / " + questions.size()
                            + "\nCould not save result to MySQL: " + ex.getMessage(),
                    "Practice result", JOptionPane.WARNING_MESSAGE);
        }
        dispose();
    }
}
