package service;

import model.Appointment;
import model.AuditLog;

import java.io.FileWriter;
import java.io.IOException;

public class AppointmentService {

    private NotificationService notificationService = new NotificationService();

    public void createAppointment(Appointment appointment) {
        save(appointment);
        String patientEmail = appointment.getPatient().getContactInfo();
        notificationService.sendEmail(
                patientEmail != null ? patientEmail : "patient@email",
                "Appointment scheduled for " + appointment.getDateTime() + 
                " with " + appointment.getClinician().getUsername()
        );
        AuditLog.recordAction(appointment.getPatient().getUserId(), "APPOINTMENT CREATED");
    }

    private void save(Appointment appointment) {
        try (FileWriter fw = new FileWriter("data/appointments.txt", true)) {
            fw.write(appointment.toFileString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
