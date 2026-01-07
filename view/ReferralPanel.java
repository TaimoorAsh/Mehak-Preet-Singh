package view;

import controller.ReferralController;
import model.*;
import service.DataService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReferralPanel extends JPanel {
    private DataService dataService;
    private ReferralController referralController;
    private User currentUser;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Specialist> specialistCombo;
    private JTextField reasonField;
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

    public ReferralPanel(User user) {
        this.currentUser = user;
        this.dataService = DataService.getInstance();
        this.referralController = new ReferralController();
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Create Referral");
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
        
        // Specialist selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("To Specialist:"), gbc);
        
        List<Specialist> specialists = dataService.getSpecialists();
        if (specialists.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No specialists available. Please add specialists to the system.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        specialistCombo = new JComboBox<>(specialists.toArray(new Specialist[0]));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(specialistCombo, gbc);
        
        // Reason field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Referral Reason:"), gbc);
        
        reasonField = new JTextField(30);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(reasonField, gbc);
        
        // Create button
        createBtn = new JButton("Create Referral");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createBtn, gbc);
        
        createBtn.addActionListener(e -> {
            if (patientCombo.getSelectedItem() == null || specialistCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both patient and specialist",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String reason = reasonField.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a referral reason",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = (Patient) patientCombo.getSelectedItem();
            Specialist specialist = (Specialist) specialistCombo.getSelectedItem();
            
            // Get current user as clinician (if they are one)
            Clinician fromClinician = null;
            if (currentUser instanceof Clinician) {
                fromClinician = (Clinician) currentUser;
            } else {
                // If current user is not a clinician, get first GP as default
                List<GP> gps = dataService.getGPs();
                if (!gps.isEmpty()) {
                    fromClinician = gps.get(0);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No GP available to create referral",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            Referral referral = new Referral(fromClinician, specialist, patient, reason);
            referralController.processReferral(referral);
            
            JOptionPane.showMessageDialog(this,
                    "Referral created successfully! ID: " + referral.getReferralId() + 
                    "\nProcessed via Singleton Manager",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            reasonField.setText("");
        });
    }
}
