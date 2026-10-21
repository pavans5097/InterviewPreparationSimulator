package com.ips.model;

public class User {
    private final long id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final int passwordIterations;

    public User(long id, String fullName, String email,
                String passwordHash, String passwordSalt, int passwordIterations) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.passwordIterations = passwordIterations;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public int getPasswordIterations() {
        return passwordIterations;
    }
}
