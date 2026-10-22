package com.ips.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/interview_simulator"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("IPS_DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("IPS_DB_USER", "root");
        String password = System.getenv().getOrDefault("IPS_DB_PASSWORD", "root5097");

        return DriverManager.getConnection(url, user, password);
    }
}
