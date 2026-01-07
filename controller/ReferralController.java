package controller;

import model.Referral;
import service.ReferralManager;

public class ReferralController {

    public void processReferral(Referral referral) {
        ReferralManager.getInstance().processReferral(referral);
    }
}
