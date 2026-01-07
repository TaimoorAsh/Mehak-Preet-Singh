package service;

import model.AuditLog;
import model.Referral;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class ReferralManager {

    private static ReferralManager instance;

    private Queue<Referral> referralQueue;
    private NotificationService notificationService;
    private EHRIntegration ehrIntegration;

    private ReferralManager() {
        referralQueue = new LinkedList<>();
        notificationService = new NotificationService();
        ehrIntegration = new EHRIntegration();
    }

    public static synchronized ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    public void processReferral(Referral referral) {
        referralQueue.add(referral);
        persist(referral);
        String specialistEmail = referral.getToSpecialist().getContactInfo();
        notificationService.sendEmail(
                specialistEmail != null ? specialistEmail : "specialist@email",
                "New referral created. ID: " + referral.getReferralId() + 
                " for patient: " + referral.getPatient().getUsername()
        );
        ehrIntegration.pushReferral(referral);
        AuditLog.recordAction(referral.getFromClinician().getUserId(), "REFERRAL PROCESSED");
    }

    private void persist(Referral referral) {
        try (FileWriter fw = new FileWriter("data/referrals.txt", true)) {
            fw.write(referral.toFileString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
