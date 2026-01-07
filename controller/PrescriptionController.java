package controller;

import model.Prescription;
import service.PrescriptionService;

public class PrescriptionController {

    private PrescriptionService service = new PrescriptionService();

    public void issuePrescription(Prescription prescription) {
        service.issuePrescription(prescription);
    }
}
