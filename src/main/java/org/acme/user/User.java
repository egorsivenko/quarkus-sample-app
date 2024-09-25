package org.acme.user;

import io.quarkus.elytron.security.common.BcryptUtil;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private boolean isVerified;
    private final LocalDateTime createdAt;

    public User(String fullName, String email, String password) {
        this(fullName, email, password, UserRole.USER, false);
    }

    public User(String fullName, String email, String password, UserRole role, boolean isVerified) {
        this.fullName = fullName;
        this.email = email;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
        this.isVerified = isVerified;
        this.createdAt = LocalDateTime.now();
    }

    public boolean verifyPassword(String password) {
        return BcryptUtil.matches(password, this.password);
    }

    public void changePassword(String newPassword) {
        this.password = BcryptUtil.bcryptHash(newPassword);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
