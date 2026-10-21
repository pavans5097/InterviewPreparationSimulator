package com.ips.service;
import com.ips.dao.UserDAO;
import com.ips.model.User;
import com.ips.util.PasswordUtil;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO = new UserDAO();

    public User register(String fullName, String email, char[] password,
                        char[] confirmPassword) throws SQLException {
        String cleanName = fullName == null ? "" : fullName.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();

        if (cleanName.length() < 2 || cleanName.length() > 100) {
            throw new IllegalArgumentException("Name must contain 2 to 100 characters.");
        }
        if (cleanEmail.length() > 254 || !EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        if (confirmPassword == null || !java.util.Arrays.equals(password, confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userDAO.findByEmail(cleanEmail) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        PasswordUtil.PasswordData data = PasswordUtil.hash(password);
        try {
            long id = userDAO.createUser(cleanName, cleanEmail,
                    data.hash(), data.salt(), data.iterations());
            return new User(id, cleanName, cleanEmail, null, null, 0);
        } catch (SQLException ex) {
            // The unique constraint also protects against two simultaneous registrations.
            if ("23000".equals(ex.getSQLState())) {
                throw new IllegalArgumentException(
                        "An account with this email already exists.", ex);
            }
            throw ex;
        }
    }

    public User login(String email, char[] password) throws SQLException {
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        if (cleanEmail.isBlank() || password == null || password.length == 0) {
            return null;
        }

        User user = userDAO.findByEmail(cleanEmail);
        if (user == null) {
            return null;
        }

        boolean valid = PasswordUtil.verify(password, user.getPasswordHash(),
                user.getPasswordSalt(), user.getPasswordIterations());

        if (valid) {
            // Return a session user without password material.
            return new User(user.getId(), user.getFullName(), user.getEmail(),
                    null, null, 0);
        }
        return null;
    }
}
