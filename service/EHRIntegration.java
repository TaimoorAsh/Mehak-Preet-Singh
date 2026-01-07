package service;

import model.Prescription;
import model.Referral;

import java.io.FileWriter;
import java.io.IOException;

public class EHRIntegration {

    private String endpointURL = "HospitalEHR";
    private String authToken = "LOCAL_TOKEN";

    public void pushReferral(Referral referral) {
        write("Referral pushed: ID " + referral.getReferralId());
    }

    public void pushPrescription(Prescription prescription) {
        write("Prescription pushed: " + prescription.toFileString());
    }

    private void write(String record) {
        try (FileWriter fw = new FileWriter("data/ehr_log.txt", true)) {
            fw.write(record + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
