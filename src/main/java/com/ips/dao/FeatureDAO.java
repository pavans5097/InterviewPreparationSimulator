package com.ips.dao;

import com.ips.db.DBConnection;
import com.ips.model.Question;
import java.sql.*;
import java.util.*;

public class FeatureDAO {
    public record AttemptRow(long id, String mode, Timestamp date, int score, int total) {}
    public record Stats(int attempts, int questions, int correct, double average, int best) {}

    public Stats getStats(long userId) throws SQLException {
        String sql="SELECT COUNT(*) attempts, COALESCE(SUM(total_questions),0) questions, COALESCE(SUM(score),0) correct, COALESCE(AVG(100.0*score/NULLIF(total_questions,0)),0) average, COALESCE(MAX(100.0*score/NULLIF(total_questions,0)),0) best FROM attempts WHERE user_id=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setLong(1,userId); try(ResultSet r=p.executeQuery()){r.next(); return new Stats(r.getInt("attempts"),r.getInt("questions"),r.getInt("correct"),r.getDouble("average"),r.getInt("best"));}
        }
    }
    public List<String[]> subjectStats(long userId) throws SQLException {
        String sql="SELECT q.category,COUNT(*) attempted,SUM(a.is_correct) correct FROM user_answers a JOIN attempts t ON t.attempt_id=a.attempt_id JOIN questions q ON q.question_id=a.question_id WHERE t.user_id=? GROUP BY q.category ORDER BY q.category";
        List<String[]> rows=new ArrayList<>(); try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())rows.add(new String[]{r.getString(1),r.getString(2),r.getString(3),String.format(Locale.US,"%.1f",100.0*r.getInt(3)/Math.max(1,r.getInt(2)))});}} return rows;
    }
    public List<AttemptRow> history(long userId) throws SQLException {
        List<AttemptRow> rows=new ArrayList<>(); String sql="SELECT attempt_id,mode,start_time,score,total_questions FROM attempts WHERE user_id=? ORDER BY start_time DESC";
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())rows.add(new AttemptRow(r.getLong(1),r.getString(2),r.getTimestamp(3),r.getInt(4),r.getInt(5)));}}return rows;
    }
    public List<Question> bookmarks(long userId) throws SQLException {
        List<Question> rows=new ArrayList<>(); String sql="SELECT q.question_id,q.category,q.difficulty,q.question_text,q.option_a,q.option_b,q.option_c,q.option_d,q.correct_option,q.explanation FROM bookmarks b JOIN questions q ON q.question_id=b.question_id WHERE b.user_id=? ORDER BY b.created_at DESC";
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())rows.add(question(r));}}return rows;
    }
    public boolean isBookmarked(long userId,long questionId) throws SQLException {try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT 1 FROM bookmarks WHERE user_id=? AND question_id=?")){p.setLong(1,userId);p.setLong(2,questionId);try(ResultSet r=p.executeQuery()){return r.next();}}}
    public void toggleBookmark(long userId,long questionId) throws SQLException {try(Connection c=DBConnection.getConnection()){if(isBookmarked(userId,questionId)){try(PreparedStatement p=c.prepareStatement("DELETE FROM bookmarks WHERE user_id=? AND question_id=?")){p.setLong(1,userId);p.setLong(2,questionId);p.executeUpdate();}}else{try(PreparedStatement p=c.prepareStatement("INSERT IGNORE INTO bookmarks(user_id,question_id) VALUES(?,?)")){p.setLong(1,userId);p.setLong(2,questionId);p.executeUpdate();}}}}
    public void saveQuestion(String category,String difficulty,String text,String a,String b,String c,String d,String correct,String explanation,Long id) throws SQLException {
        if(id==null){String sql="INSERT INTO questions(category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) VALUES(?,?,?,?,?,?,?,?,?)";try(Connection cn=DBConnection.getConnection();PreparedStatement p=cn.prepareStatement(sql)){fill(p,category,difficulty,text,a,b,c,d,correct,explanation);p.executeUpdate();}}
        else {String sql="UPDATE questions SET category=?,difficulty=?,question_text=?,option_a=?,option_b=?,option_c=?,option_d=?,correct_option=?,explanation=? WHERE question_id=?";try(Connection cn=DBConnection.getConnection();PreparedStatement p=cn.prepareStatement(sql)){fill(p,category,difficulty,text,a,b,c,d,correct,explanation);p.setLong(10,id);p.executeUpdate();}}
    }
    private void fill(PreparedStatement p,String category,String difficulty,String text,String a,String b,String c,String d,String correct,String explanation)throws SQLException{p.setString(1,category);p.setString(2,difficulty);p.setString(3,text);p.setString(4,a);p.setString(5,b);p.setString(6,c);p.setString(7,d);p.setString(8,correct);p.setString(9,explanation);}
    public List<String[]> allQuestions() throws SQLException {List<String[]> rows=new ArrayList<>();try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT question_id,category,difficulty,question_text FROM questions ORDER BY category,question_id");ResultSet r=p.executeQuery()){while(r.next())rows.add(new String[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4)});}return rows;}
    public void deleteQuestion(long id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM questions WHERE question_id=?")){p.setLong(1,id);p.executeUpdate();}}
    public Question getQuestion(long id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT question_id,category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation FROM questions WHERE question_id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?question(r):null;}}}
    private Question question(ResultSet r)throws SQLException{return new Question(r.getLong("question_id"),r.getString("category"),r.getString("difficulty"),r.getString("question_text"),r.getString("option_a"),r.getString("option_b"),r.getString("option_c"),r.getString("option_d"),r.getString("correct_option"),r.getString("explanation"));}
}
