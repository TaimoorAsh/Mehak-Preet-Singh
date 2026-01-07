package view;

import controller.PrescriptionController;
import model.*;
import service.DataService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PrescriptionPanel extends JPanel {
    private DataService dataService;
    private PrescriptionController prescriptionController;
    private User currentUser;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Drug> drugCombo;
    private JTextField conditionField;
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

    public PrescriptionPanel(User user) {
        this.currentUser = user;
        this.dataService = DataService.getInstance();
        this.prescriptionController = new PrescriptionController();
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Issue Prescription");
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
        
        // Drug selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Drug:"), gbc);
        
        List<Drug> drugs = dataService.getDrugs();
        drugCombo = new JComboBox<>(drugs.toArray(new Drug[0]));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(drugCombo, gbc);
        
        // Condition field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Condition:"), gbc);
        
        conditionField = new JTextField(30);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(conditionField, gbc);
        
        // Create button
        createBtn = new JButton("Issue Prescription");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createBtn, gbc);
        
        createBtn.addActionListener(e -> {
            if (patientCombo.getSelectedItem() == null || drugCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both patient and drug",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String condition = conditionField.getText().trim();
            if (condition.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a condition",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = (Patient) patientCombo.getSelectedItem();
            Drug drug = (Drug) drugCombo.getSelectedItem();
            
            // Get current user as clinician
            Clinician clinician = null;
            if (currentUser instanceof Clinician) {
                clinician = (Clinician) currentUser;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Only clinicians can issue prescriptions",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Prescription prescription = new Prescription(patient, clinician, drug, condition);
            prescriptionController.issuePrescription(prescription);
            
            JOptionPane.showMessageDialog(this,
                    "Prescription issued successfully! ID: " + prescription.getPrescriptionId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            conditionField.setText("");
        });
    }
}

