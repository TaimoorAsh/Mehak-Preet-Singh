package controller;

import model.AuditLog;
import model.User;

public class AuthenticationController {

    public boolean login(User user, String username, String password) {
        boolean success = user.authenticate(username, password);
        AuditLog.recordAction(user.getUserId(),
                success ? "LOGIN SUCCESS" : "LOGIN FAILURE");
        return success;
    }
}
