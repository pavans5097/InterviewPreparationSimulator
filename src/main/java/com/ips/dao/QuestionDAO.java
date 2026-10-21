package com.ips.dao;

import com.ips.db.DBConnection;
import com.ips.model.Question;
import java.sql.*;
import java.util.*;

public class QuestionDAO {
    public List<String> getCategories() throws SQLException {
        List<String> values = new ArrayList<>();
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("SELECT DISTINCT category FROM questions ORDER BY category");
             ResultSet r=p.executeQuery()) {
            while(r.next()) values.add(r.getString(1));
        }
        return values;
    }

    public List<Question> getQuestions(String category) throws SQLException {
        List<Question> values = new ArrayList<>();
        String sql = "SELECT question_id,category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation FROM questions WHERE category=? ORDER BY question_id";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1, category);
            try (ResultSet r=p.executeQuery()) {
                while(r.next()) values.add(new Question(r.getLong("question_id"),
                    r.getString("category"),r.getString("difficulty"),r.getString("question_text"),
                    r.getString("option_a"),r.getString("option_b"),r.getString("option_c"),
                    r.getString("option_d"),r.getString("correct_option"),r.getString("explanation")));
            }
        }
        Collections.shuffle(values);
        return values;
    }
}
