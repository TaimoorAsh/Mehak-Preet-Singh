package model;

import java.util.List;

public class Clinician extends User {
    protected String specialty;
    protected String qualifications;

    public Clinician(int userId, String username, String passwordHash, String role, String contactInfo,
                     String specialty, String qualifications) {
        super(userId, username, passwordHash, role, contactInfo);
        this.specialty = specialty;
        this.qualifications = qualifications;
    }

    public List<Appointment> viewAppointments() {
        return null;
    }

    public Prescription createPrescription(Patient patient, Drug drug, String condition) {
        return new Prescription(patient, this, drug, condition);
    }

    public Referral createReferral(Patient patient, Specialist specialist, String reason) {
        return new Referral(this, specialist, patient, reason);
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return username + " (" + specialty + ")";
    }
}
