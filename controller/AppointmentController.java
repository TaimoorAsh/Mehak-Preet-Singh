package controller;

import model.Appointment;
import service.AppointmentService;

public class AppointmentController {

    private AppointmentService service = new AppointmentService();

    public void createAppointment(Appointment appointment) {
        service.createAppointment(appointment);
    }
}
