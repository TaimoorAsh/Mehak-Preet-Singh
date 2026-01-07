package service;

import java.io.FileWriter;
import java.io.IOException;

public class NotificationService {

    private static final String EMAIL_FILE = "data/notifications_email.txt";
    private static final String SMS_FILE = "data/notifications_sms.txt";

    public void sendEmail(String recipient, String message) {
        writeToFile(EMAIL_FILE, "EMAIL to " + recipient + ": " + message);
    }

    public void sendSMS(String recipient, String message) {
        writeToFile(SMS_FILE, "SMS to " + recipient + ": " + message);
    }

    private void writeToFile(String file, String content) {
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(content + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
