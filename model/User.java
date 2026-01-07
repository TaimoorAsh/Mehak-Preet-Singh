package model;

public abstract class User {
    protected int userId;
    protected String username;
    protected String passwordHash;
    protected String role;
    protected String contactInfo;

    public User(int userId, String username, String passwordHash, String role, String contactInfo) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.contactInfo = contactInfo;
    }

    public boolean authenticate(String username, String password) {
        return this.username.equals(username)
                && this.passwordHash.equals(password); // simplified
    }

    public boolean authorize(String action) {
        return true; // role-based logic can expand later
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
