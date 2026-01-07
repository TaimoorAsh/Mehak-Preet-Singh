package service;

import model.AuditLog;
import model.Prescription;

import java.io.FileWriter;
import java.io.IOException;

public class PrescriptionService {

    private EHRIntegration ehr = new EHRIntegration();

    public void issuePrescription(Prescription prescription) {
        save(prescription);
        ehr.pushPrescription(prescription);
        AuditLog.recordAction(prescription.getClinician().getUserId(), "PRESCRIPTION ISSUED");
    }

    private void save(Prescription prescription) {
        try (FileWriter fw = new FileWriter("data/prescriptions.txt", true)) {
            fw.write(prescription.toFileString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
