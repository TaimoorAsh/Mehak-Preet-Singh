package model;

public class GP extends Clinician {
    public GP(int userId, String username, String passwordHash, String role, String contactInfo,
              String specialty, String qualifications) {
        super(userId, username, passwordHash, role, contactInfo, specialty, qualifications);
    }

    public boolean approveReferral(int referralId) {
        return true;
    }
}
