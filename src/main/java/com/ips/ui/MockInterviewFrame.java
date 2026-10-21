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

public class MockInterviewFrame extends JFrame {
    private final User user;
    private final List<Question> questions;
    private final List<String> answers = new ArrayList<>();
    private final List<Integer> secondsTaken = new ArrayList<>();
    private final JLabel counter = new JLabel();
    private final JLabel timerLabel = new JLabel();
    private final JTextArea prompt = new JTextArea();
    private final JRadioButton[] options = new JRadioButton[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private final JButton previous = new JButton("Previous");
    private final JButton next = new JButton("Next");
    private final long[] shownAt;
    private final Timer timer;
    private int index;
    private int remaining;
    private boolean finished;

    public MockInterviewFrame(User user, List<Question> questions, int minutes) {
        this.user = user;
        this.questions = questions;
        this.remaining = minutes * 60;
        this.shownAt = new long[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            answers.add(null);
            secondsTaken.add(0);
        }
        shownAt[0] = System.currentTimeMillis();

        UIStyle.configureFrame(this, "Mock Interview", 850, 650);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(UIStyle.BACKGROUND);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.BACKGROUND);
        JLabel heading = UIStyle.heading("Mock Interview");
        JPanel status = new JPanel(new GridLayout(2, 1, 0, 4));
        status.setBackground(UIStyle.BACKGROUND);
        counter.setForeground(UIStyle.MUTED);
        timerLabel.setForeground(new Color(255, 205, 100));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        status.add(counter);
        status.add(timerLabel);
        header.add(heading, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        JPanel card = new JPanel();
        card.setBackground(UIStyle.PANEL);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel instruction = new JLabel("Choose one answer. Answers are revealed after submission.");
        instruction.setForeground(UIStyle.MUTED);
        card.add(instruction);
        card.add(Box.createVerticalStrut(18));

        prompt.setEditable(false);
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setFocusable(false);
        prompt.setRows(4);
        prompt.setFont(new Font("SansSerif", Font.BOLD, 19));
        prompt.setForeground(UIStyle.TEXT);
        prompt.setBackground(UIStyle.PANEL);
        prompt.setBorder(null);
        card.add(prompt);
        card.add(Box.createVerticalStrut(20));

        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setForeground(UIStyle.TEXT);
            options[i].setBackground(UIStyle.PANEL);
            options[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            options[i].setFocusPainted(false);
            options[i].putClientProperty("letter", String.valueOf((char) ('A' + i)));
            optionGroup.add(options[i]);
            card.add(options[i]);
            card.add(Box.createVerticalStrut(12));
        }

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UIStyle.BACKGROUND);
        JButton exit = UIStyle.linkButton("Exit mock interview");
        exit.addActionListener(e -> confirmExit());
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        nav.setBackground(UIStyle.BACKGROUND);
        previous.addActionListener(e -> move(-1));
        next.addActionListener(e -> {
            if (index == questions.size() - 1) submit();
            else move(1);
        });
        nav.add(previous);
        nav.add(next);
        footer.add(exit, BorderLayout.WEST);
        footer.add(nav, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);

        timer = new Timer(1000, e -> {
            remaining--;
            updateTimer();
            if (remaining <= 0) submit();
        });
        updateTimer();
        showQuestion();
        timer.start();
    }

    private void showQuestion() {
        Question q = questions.get(index);
        counter.setText("Question " + (index + 1) + " of " + questions.size()
                + "  •  " + q.getCategory() + " / " + q.getDifficulty());
        prompt.setText(q.getQuestionText());
        optionGroup.clearSelection();
        for (int i = 0; i < options.length; i++) {
            String letter = String.valueOf((char) ('A' + i));
            options[i].setText(letter + ". " + q.getOption(letter));
            options[i].setSelected(letter.equals(answers.get(index)));
        }
        previous.setEnabled(index > 0);
        next.setText(index == questions.size() - 1 ? "Submit interview" : "Next");
    }

    private void storeAnswerAndTime() {
        String selected = null;
        for (JRadioButton option : options) {
            if (option.isSelected()) {
                selected = (String) option.getClientProperty("letter");
                break;
            }
        }
        answers.set(index, selected);
        int elapsed = (int) Math.max(0, (System.currentTimeMillis() - shownAt[index]) / 1000);
        secondsTaken.set(index, secondsTaken.get(index) + elapsed);
    }

    private void move(int amount) {
        if (finished) return;
        storeAnswerAndTime();
        index += amount;
        shownAt[index] = System.currentTimeMillis();
        showQuestion();
    }

    private void updateTimer() {
        timerLabel.setText(String.format("Time remaining: %02d:%02d",
                Math.max(0, remaining) / 60, Math.max(0, remaining) % 60));
    }

    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this,
                "Exit now? This mock interview will not be saved.",
                "Exit mock interview", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            finished = true;
            timer.stop();
            dispose();
        }
    }

    private void submit() {
        if (finished) return;
        storeAnswerAndTime();
        finished = true;
        timer.stop();

        int score = 0;
        StringBuilder review = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean correct = answers.get(i) != null && answers.get(i).equals(q.getCorrectOption());
            if (correct) score++;
            review.append(i + 1).append(". ").append(q.getQuestionText()).append("\n")
                    .append("Your answer: ")
                    .append(answers.get(i) == null ? "Skipped" : answers.get(i) + ". " + q.getOption(answers.get(i)))
                    .append("\nCorrect answer: ").append(q.getCorrectOption()).append(". ")
                    .append(q.getOption(q.getCorrectOption())).append("\n")
                    .append(q.getExplanation()).append("\n\n");
        }

        String saveMessage;
        try {
            new AttemptDAO().saveAttempt(user.getId(), "Mock Interview",
                    score, questions, answers, secondsTaken);
            saveMessage = "Result saved to MySQL.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            saveMessage = "Could not save result to MySQL: " + ex.getMessage();
        }

        JTextArea report = new JTextArea(review.toString());
        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        report.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(report);
        scroll.setPreferredSize(new Dimension(600, 350));

        JOptionPane.showMessageDialog(this,
                "Mock interview completed!\nScore: " + score + " / " + questions.size()
                        + "\nAccuracy: " + Math.round(score * 100.0 / questions.size()) + "%\n"
                        + saveMessage,
                "Interview result", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, scroll, "Answer review",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
