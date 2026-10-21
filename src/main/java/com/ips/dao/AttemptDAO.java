package com.ips.dao;

import com.ips.db.DBConnection;
import com.ips.model.Question;
import java.sql.*;
import java.util.List;

public class AttemptDAO {
    public void saveAttempt(long userId, String mode, int score, List<Question> questions,
                            List<String> answers, List<Integer> secondsTaken) throws SQLException {
        String sqlAttempt = "INSERT INTO attempts(user_id,mode,start_time,end_time,score,total_questions) VALUES(?,?,NOW(),NOW(),?,?)";
        String sqlAnswer = "INSERT INTO user_answers(attempt_id,question_id,selected_option,is_correct,time_taken) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                long attemptId;
                try (PreparedStatement p = c.prepareStatement(sqlAttempt, Statement.RETURN_GENERATED_KEYS)) {
                    p.setLong(1, userId);
                    p.setString(2, mode);
                    p.setInt(3, score);
                    p.setInt(4, questions.size());
                    p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Could not save attempt.");
                        attemptId = keys.getLong(1);
                    }
                }
                try (PreparedStatement p = c.prepareStatement(sqlAnswer)) {
                    for (int i = 0; i < questions.size(); i++) {
                        p.setLong(1, attemptId);
                        p.setLong(2, questions.get(i).getId());
                        if (answers.get(i) == null) p.setNull(3, Types.CHAR);
                        else p.setString(3, answers.get(i));
                        p.setBoolean(4, answers.get(i) != null
                                && answers.get(i).equals(questions.get(i).getCorrectOption()));
                        p.setInt(5, secondsTaken.get(i));
                        p.addBatch();
                    }
                    p.executeBatch();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}
