package model;

public class Nurse extends Clinician {
    private String shiftSchedule;

    public Nurse(int userId, String username, String passwordHash, String role, String contactInfo,
                 String specialty, String qualifications, String shiftSchedule) {
        super(userId, username, passwordHash, role, contactInfo, specialty, qualifications);
        this.shiftSchedule = shiftSchedule;
    }

    public boolean updatePatientRecord(int patientId, String notes) {
        return true;
    }

    public String getShiftSchedule() {
        return shiftSchedule;
    }

    public void setShiftSchedule(String shiftSchedule) {
        this.shiftSchedule = shiftSchedule;
    }
}
