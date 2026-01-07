package model;

public class Referral {
    private static int counter = 1;

    private int referralId;
    private Clinician fromClinician;
    private Specialist toSpecialist;
    private Patient patient;
    private String reason;
    private String status;

    public Referral(Clinician from, Specialist to, Patient patient, String reason) {
        this.referralId = counter++;
        this.fromClinician = from;
        this.toSpecialist = to;
        this.patient = patient;
        this.reason = reason;
        this.status = "CREATED";
    }

    public int getReferralId() {
        return referralId;
    }

    public String toFileString() {
        return referralId + "," + fromClinician.getUserId() + "," +
               toSpecialist.getUserId() + "," + patient.getUserId() + "," +
               reason + "," + status;
    }

    public Clinician getFromClinician() {
        return fromClinician;
    }

    public Specialist getToSpecialist() {
        return toSpecialist;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
