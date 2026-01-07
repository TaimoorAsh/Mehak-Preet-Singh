package controller;

import model.AuditLog;
import model.Patient;
import service.DataService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientController {
    private DataService dataService;

    public PatientController() {
        this.dataService = DataService.getInstance();
    }

    public Patient addPatient(String firstName, String lastName, String email, String phone, 
                             String address, String nhsNumber, Date dateOfBirth, 
                             String medicalHistoryRef, String username, String password) {
        int newId = dataService.getNextPatientId();
        Patient patient = new Patient(newId, username, password, "Patient", email,
                firstName, lastName, email, phone, address,
                nhsNumber, dateOfBirth, medicalHistoryRef);
        dataService.addPatient(patient);
        AuditLog.recordAction(newId, "PATIENT CREATED");
        return patient;
    }

    public boolean updatePatient(Patient patient) {
        Patient existing = dataService.findPatientById(patient.getUserId());
        if (existing != null) {
            dataService.updatePatient(patient);
            AuditLog.recordAction(patient.getUserId(), "PATIENT UPDATED");
            return true;
        }
        return false;
    }

    public boolean deletePatient(int userId) {
        boolean deleted = dataService.deletePatient(userId);
        if (deleted) {
            AuditLog.recordAction(userId, "PATIENT DELETED");
        }
        return deleted;
    }
}

