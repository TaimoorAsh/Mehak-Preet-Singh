package view;

import controller.AppointmentController;
import model.*;
import service.AppointmentService;
import service.DataService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentPanel extends JPanel {
    private DataService dataService;
    private AppointmentController appointmentController;
    private User currentUser;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Clinician> clinicianCombo;
    private JTextField dateField;
    private JTextField timeField;
    private JButton createBtn;
    
    public void refreshPatientList() {
        if (patientCombo != null) {
            Patient selected = (Patient) patientCombo.getSelectedItem();
            int selectedId = selected != null ? selected.getUserId() : -1;
            List<Patient> patients = dataService.getPatients();
            patientCombo.removeAllItems();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
            // Try to reselect the previously selected patient by ID
            if (selectedId > 0) {
                for (int i = 0; i < patientCombo.getItemCount(); i++) {
                    Patient p = (Patient) patientCombo.getItemAt(i);
                    if (p.getUserId() == selectedId) {
                        patientCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    public AppointmentPanel(User user) {
        this.currentUser = user;
        this.dataService = DataService.getInstance();
        this.appointmentController = new AppointmentController();
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Create Appointment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Patient selection
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Patient:"), gbc);
        
        List<Patient> patients = dataService.getPatients();
        patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(patientCombo, gbc);
        
        // Clinician selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Clinician:"), gbc);
        
        List<Clinician> clinicians = dataService.getClinicians();
        clinicianCombo = new JComboBox<>(clinicians.toArray(new Clinician[0]));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(clinicianCombo, gbc);
        
        // Date field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        
        dateField = new JTextField(15);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(sdf.format(new Date()));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(dateField, gbc);
        
        // Time field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Time (HH:mm):"), gbc);
        
        timeField = new JTextField(15);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeField.setText(timeFormat.format(new Date()));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(timeField, gbc);
        
        // Create button
        createBtn = new JButton("Create Appointment");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createBtn, gbc);
        
        createBtn.addActionListener(e -> {
            if (patientCombo.getSelectedItem() == null || clinicianCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both patient and clinician",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Patient patient = (Patient) patientCombo.getSelectedItem();
                Clinician clinician = (Clinician) clinicianCombo.getSelectedItem();
                
                String dateStr = dateField.getText().trim();
                String timeStr = timeField.getText().trim();
                
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date appointmentDate = dateTimeFormat.parse(dateStr + " " + timeStr);
                
                Appointment appointment = new Appointment(patient, clinician, appointmentDate);
                appointmentController.createAppointment(appointment);
                
                JOptionPane.showMessageDialog(this,
                        "Appointment created successfully! ID: " + appointment.getAppointmentId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error creating appointment: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

