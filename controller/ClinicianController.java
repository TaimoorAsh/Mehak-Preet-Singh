package controller;

import model.AuditLog;
import model.Clinician;
import service.DataService;

public class ClinicianController {
    private DataService dataService;

    public ClinicianController() {
        this.dataService = DataService.getInstance();
    }

    public Clinician addClinician(String username, String password, String role, String contactInfo,
                                 String specialty, String qualifications, String extraField) {
        int newId = dataService.getNextClinicianId();
        Clinician clinician = null;
        
        if ("GP".equalsIgnoreCase(role)) {
            clinician = new model.GP(newId, username, password, role, contactInfo, specialty, qualifications);
        } else if ("Nurse".equalsIgnoreCase(role)) {
            clinician = new model.Nurse(newId, username, password, role, contactInfo, specialty, qualifications, extraField);
        } else if ("Specialist".equalsIgnoreCase(role)) {
            clinician = new model.Specialist(newId, username, password, role, contactInfo, specialty, qualifications, extraField);
        } else {
            clinician = new Clinician(newId, username, password, role, contactInfo, specialty, qualifications);
        }
        
        dataService.addClinician(clinician);
        AuditLog.recordAction(newId, "CLINICIAN CREATED");
        return clinician;
    }

    public boolean updateClinician(Clinician clinician) {
        Clinician existing = dataService.findClinicianById(clinician.getUserId());
        if (existing != null) {
            dataService.updateClinician(clinician);
            AuditLog.recordAction(clinician.getUserId(), "CLINICIAN UPDATED");
            return true;
        }
        return false;
    }

    public boolean deleteClinician(int userId) {
        boolean deleted = dataService.deleteClinician(userId);
        if (deleted) {
            AuditLog.recordAction(userId, "CLINICIAN DELETED");
        }
        return deleted;
    }
}

