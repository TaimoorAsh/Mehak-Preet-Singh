package model;

public class Specialist extends Clinician {
    private String hospitalDepartment;

    public Specialist(int userId, String username, String passwordHash, String role, String contactInfo,
                     String specialty, String qualifications, String hospitalDepartment) {
        super(userId, username, passwordHash, role, contactInfo, specialty, qualifications);
        this.hospitalDepartment = hospitalDepartment;
    }

    public boolean reviewReferral(int referralId) {
        return true;
    }

    public String getHospitalDepartment() {
        return hospitalDepartment;
    }

    public void setHospitalDepartment(String hospitalDepartment) {
        this.hospitalDepartment = hospitalDepartment;
    }
}
