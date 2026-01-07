package model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditLog {

    public static void recordAction(int userId, String action) {
        try (FileWriter fw = new FileWriter("data/audit.log", true)) {
            fw.write(LocalDateTime.now() + " | USER " + userId + " | " + action + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
