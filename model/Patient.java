package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Patient extends User {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String nhsNumber;
    private Date dateOfBirth;
    private String medicalHistoryRef;

    public Patient(int userId, String username, String passwordHash, String role, String contactInfo,
                   String nhsNumber, Date dateOfBirth, String medicalHistoryRef) {
        super(userId, username, passwordHash, role, contactInfo);
        this.nhsNumber = nhsNumber;
        this.dateOfBirth = dateOfBirth;
        this.medicalHistoryRef = medicalHistoryRef;
        // Initialize with defaults if not provided
        this.firstName = "";
        this.lastName = "";
        this.email = contactInfo != null ? contactInfo : "";
        this.phone = "";
        this.address = "";
    }

    public Patient(int userId, String username, String passwordHash, String role, String contactInfo,
                   String firstName, String lastName, String email, String phone, String address,
                   String nhsNumber, Date dateOfBirth, String medicalHistoryRef) {
        super(userId, username, passwordHash, role, contactInfo);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.nhsNumber = nhsNumber;
        this.dateOfBirth = dateOfBirth;
        this.medicalHistoryRef = medicalHistoryRef;
    }

    public Appointment requestAppointment(Date date, Clinician clinician) {
        return new Appointment(this, clinician, date);
    }

    public List<Prescription> viewPrescriptions() {
        return null; // loaded from file later
    }

    public boolean cancelAppointment(int appointmentId) {
        return true;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getMedicalHistoryRef() {
        return medicalHistoryRef;
    }

    public void setMedicalHistoryRef(String medicalHistoryRef) {
        this.medicalHistoryRef = medicalHistoryRef;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFormattedDateOfBirth() {
        if (dateOfBirth == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(dateOfBirth);
    }

    public String getPatientId() {
        return "P" + String.format("%03d", userId);
    }

    @Override
    public String toString() {
        return username + " (NHS: " + nhsNumber + ")";
    }
}
