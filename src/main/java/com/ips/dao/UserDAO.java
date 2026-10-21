package com.ips.dao;

import com.ips.db.DBConnection;
import com.ips.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public long createUser(String fullName, String email, String passwordHash,
                           String passwordSalt, int passwordIterations) throws SQLException {
        String sql = """
                INSERT INTO users
                    (full_name, email, password_hash, password_salt, password_iterations)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, passwordSalt);
            statement.setInt(5, passwordIterations);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("The database did not return a user ID.");
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = """
                SELECT user_id, full_name, email, password_hash,
                       password_salt, password_iterations
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new User(
                            result.getLong("user_id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getString("password_hash"),
                            result.getString("password_salt"),
                            result.getInt("password_iterations")
                    );
                }
                return null;
            }
        }
    }
}
