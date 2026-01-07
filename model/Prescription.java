package model;

import java.util.Date;

public class Prescription {

    private static int counter = 1;

    private int prescriptionId;
    private Date dateIssued;
    private String condition;
    private String dosage;

    private Patient patient;
    private Clinician clinician;
    private Drug drug;

    public Prescription(Patient patient, Clinician clinician, Drug drug, String condition) {
        this.prescriptionId = counter++;
        this.dateIssued = new Date();
        this.patient = patient;
        this.clinician = clinician;
        this.drug = drug;
        this.condition = condition;
        this.dosage = drug.getDosageInfo();
    }

    public void revoke() {
        this.dosage = "REVOKED";
    }

    public String toFileString() {
        return prescriptionId + "," + patient.getUserId() + "," +
               clinician.getUserId() + "," + drug.getName() + "," +
               dosage + "," + condition;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Clinician getClinician() {
        return clinician;
    }

    public Drug getDrug() {
        return drug;
    }

    public String getCondition() {
        return condition;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public String getDosage() {
        return dosage;
    }
}
