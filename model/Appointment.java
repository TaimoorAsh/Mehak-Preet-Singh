package model;

import java.util.Date;

public class Appointment {

    private static int counter = 1;

    private int appointmentId;
    private Date dateTime;
    private String type;
    private String status;
    private String notes;

    private Patient patient;
    private Clinician clinician;

    public Appointment(Patient patient, Clinician clinician, Date dateTime) {
        this.appointmentId = counter++;
        this.patient = patient;
        this.clinician = clinician;
        this.dateTime = dateTime;
        this.status = "SCHEDULED";
    }

    public void modify(Date newDateTime) {
        this.dateTime = newDateTime;
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public static boolean checkAvailability(Clinician clinician, Date dateTime) {
        return true; // simplified – file scan later if needed
    }

    public String toFileString() {
        return appointmentId + "," + patient.getUserId() + "," +
               clinician.getUserId() + "," + dateTime + "," + status;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public Patient getPatient() {
        return patient;
    }

    public Clinician getClinician() {
        return clinician;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
