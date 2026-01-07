package view;

import controller.PatientController;
import controller.ClinicianController;
import model.Patient;
import model.Clinician;
import model.User;
import service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PatientViewPanel extends JPanel {
    private User currentUser;
    private DataService dataService;
    private PatientController patientController;
    private ClinicianController clinicianController;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private AppointmentPanel appointmentPanel;
    private PrescriptionPanel prescriptionPanel;
    private ReferralPanel referralPanel;
    private boolean isPatientUser;

    public PatientViewPanel(User user) {
        this.currentUser = user;
        this.dataService = DataService.getInstance();
        this.patientController = new PatientController();
        this.clinicianController = new ClinicianController();
        this.isPatientUser = (user instanceof Patient);
        
        setLayout(new BorderLayout());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Patients tab - only show for clinicians
        if (!isPatientUser) {
            tabbedPane.addTab("Patients", createPatientsPanel());
            tabbedPane.addTab("Clinicians", createCliniciansPanel());
        }
        
        // Appointments tab
        tabbedPane.addTab("Appointments", createAppointmentsPanel());
        
        // Prescriptions tab
        tabbedPane.addTab("Prescriptions", createPrescriptionsPanel());
        
        // Referrals tab
        tabbedPane.addTab("Referrals", createReferralsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load initial data
        if (!isPatientUser) {
            refreshPatientTable();
        }
    }

    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Patient");
        JButton editButton = new JButton("Edit Patient");
        JButton deleteButton = new JButton("Delete Patient");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"Patient ID", "First Name", "Last Name", "Email", "Phone", "Address", "DOB", "NHS Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(250);
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        patientTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(1100, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button actions
        addButton.addActionListener(e -> showAddPatientDialog());
        editButton.addActionListener(e -> showEditPatientDialog());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        refreshButton.addActionListener(e -> refreshPatientTable());
        
        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        if (currentUser instanceof Patient) {
            panel.add(createPatientAppointmentsView((Patient) currentUser), BorderLayout.CENTER);
        } else if (!isPatientUser) {
            // Show full CRUD for clinicians
            panel.add(createAppointmentsManagementPanel(), BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("Appointment management - coming soon");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createAppointmentsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Appointment");
        JButton editButton = new JButton("Edit Appointment");
        JButton deleteButton = new JButton("Delete Appointment");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"Appointment ID", "Patient", "Clinician", "Date & Time", "Status"};
        DefaultTableModel appointmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh function
        Runnable refreshAppointments = () -> {
            appointmentTableModel.setRowCount(0);
            List<model.Appointment> appointments = dataService.getAllAppointments();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (model.Appointment appointment : appointments) {
                Object[] row = {
                    appointment.getAppointmentId(),
                    appointment.getPatient() != null ? appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() : "",
                    appointment.getClinician() != null ? appointment.getClinician().getUsername() : "",
                    appointment.getDateTime() != null ? dateTimeFormat.format(appointment.getDateTime()) : "",
                    appointment.getStatus()
                };
                appointmentTableModel.addRow(row);
            }
        };
        
        // Button actions
        addButton.addActionListener(e -> {
            // Use existing AppointmentPanel for creation
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Appointment", true);
            dialog.add(new AppointmentPanel(currentUser));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            refreshAppointments.run();
        });
        editButton.addActionListener(e -> showEditAppointmentDialog(appointmentTable, appointmentTableModel, refreshAppointments));
        deleteButton.addActionListener(e -> deleteSelectedAppointment(appointmentTable, appointmentTableModel, refreshAppointments));
        refreshButton.addActionListener(e -> refreshAppointments.run());
        
        // Initial load
        refreshAppointments.run();
        
        return panel;
    }

    private JPanel createPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        if (currentUser instanceof Patient) {
            panel.add(createPatientPrescriptionsView((Patient) currentUser), BorderLayout.CENTER);
        } else if (!isPatientUser) {
            // Show full CRUD for clinicians
            panel.add(createPrescriptionsManagementPanel(), BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("Prescription management - coming soon");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createPrescriptionsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Prescription");
        JButton editButton = new JButton("Edit Prescription");
        JButton deleteButton = new JButton("Delete Prescription");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"Prescription ID", "Patient", "Drug", "Condition", "Clinician", "Date Issued"};
        DefaultTableModel prescriptionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable prescriptionTable = new JTable(prescriptionTableModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh function
        Runnable refreshPrescriptions = () -> {
            prescriptionTableModel.setRowCount(0);
            List<model.Prescription> prescriptions = dataService.getAllPrescriptions();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (model.Prescription prescription : prescriptions) {
                Object[] row = {
                    prescription.getPrescriptionId(),
                    prescription.getPatient() != null ? prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName() : "",
                    prescription.getDrug() != null ? prescription.getDrug().getName() : "",
                    prescription.getCondition(),
                    prescription.getClinician() != null ? prescription.getClinician().getUsername() : "",
                    prescription.getDateIssued() != null ? dateFormat.format(prescription.getDateIssued()) : ""
                };
                prescriptionTableModel.addRow(row);
            }
        };
        
        // Button actions
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Prescription", true);
            dialog.add(new PrescriptionPanel(currentUser));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            refreshPrescriptions.run();
        });
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit prescription functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        deleteButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Delete prescription functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        refreshButton.addActionListener(e -> refreshPrescriptions.run());
        
        // Initial load
        refreshPrescriptions.run();
        
        return panel;
    }

    private JPanel createReferralsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        if (currentUser instanceof Patient) {
            panel.add(createPatientReferralsView((Patient) currentUser), BorderLayout.CENTER);
        } else if (!isPatientUser) {
            // Show full CRUD for clinicians
            panel.add(createReferralsManagementPanel(), BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("Referral management - coming soon");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createReferralsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Referral");
        JButton editButton = new JButton("Edit Referral");
        JButton deleteButton = new JButton("Delete Referral");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"Referral ID", "Patient", "From Clinician", "To Specialist", "Reason", "Status"};
        DefaultTableModel referralTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable referralTable = new JTable(referralTableModel);
        referralTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        referralTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(referralTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh function
        Runnable refreshReferrals = () -> {
            referralTableModel.setRowCount(0);
            List<model.Referral> referrals = dataService.getAllReferrals();
            for (model.Referral referral : referrals) {
                Object[] row = {
                    referral.getReferralId(),
                    referral.getPatient() != null ? referral.getPatient().getFirstName() + " " + referral.getPatient().getLastName() : "",
                    referral.getFromClinician() != null ? referral.getFromClinician().getUsername() : "",
                    referral.getToSpecialist() != null ? referral.getToSpecialist().getUsername() : "",
                    referral.getReason(),
                    referral.getStatus()
                };
                referralTableModel.addRow(row);
            }
        };
        
        // Button actions
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Referral", true);
            dialog.add(new ReferralPanel(currentUser));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            refreshReferrals.run();
        });
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit referral functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        deleteButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Delete referral functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        refreshButton.addActionListener(e -> refreshReferrals.run());
        
        // Initial load
        refreshReferrals.run();
        
        return panel;
    }

    private void refreshPatientTable() {
        tableModel.setRowCount(0);
        List<Patient> patients = dataService.getPatients();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getDateOfBirth() != null ? sdf.format(patient.getDateOfBirth()) : "",
                patient.getNhsNumber()
            };
            tableModel.addRow(row);
        }
        
        // Refresh patient lists in other panels
        refreshOtherPanels();
    }
    
    private void refreshOtherPanels() {
        if (appointmentPanel != null) {
            appointmentPanel.refreshPatientList();
        }
        if (prescriptionPanel != null) {
            prescriptionPanel.refreshPatientList();
        }
        if (referralPanel != null) {
            referralPanel.refreshPatientList();
        }
    }

    private void showAddPatientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Patient", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField nhsNumberField = new JTextField(20);
        JTextField dobField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField medicalHistoryField = new JTextField(20);
        
        dobField.setText("yyyy-MM-dd");
        
        int row = 0;
        addField(dialog, gbc, "First Name:", firstNameField, row++);
        addField(dialog, gbc, "Last Name:", lastNameField, row++);
        addField(dialog, gbc, "Email:", emailField, row++);
        addField(dialog, gbc, "Phone:", phoneField, row++);
        addField(dialog, gbc, "Address:", addressField, row++);
        addField(dialog, gbc, "NHS Number:", nhsNumberField, row++);
        addField(dialog, gbc, "Date of Birth (yyyy-MM-dd):", dobField, row++);
        addField(dialog, gbc, "Username:", usernameField, row++);
        addField(dialog, gbc, "Password:", passwordField, row++);
        addField(dialog, gbc, "Medical History Ref:", medicalHistoryField, row++);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = sdf.parse(dobField.getText().trim());
                
                Patient newPatient = patientController.addPatient(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    nhsNumberField.getText().trim(),
                    dob,
                    medicalHistoryField.getText().trim(),
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword())
                );
                
                JOptionPane.showMessageDialog(dialog, 
                    "Patient added successfully! ID: " + newPatient.getPatientId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshPatientTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error adding patient: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditPatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a patient to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientId = (String) tableModel.getValueAt(selectedRow, 0);
        int userId = Integer.parseInt(patientId.substring(1)); // Remove "P" prefix
        Patient patient = dataService.findPatientById(userId);
        
        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                "Patient not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Patient", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField firstNameField = new JTextField(patient.getFirstName(), 20);
        JTextField lastNameField = new JTextField(patient.getLastName(), 20);
        JTextField emailField = new JTextField(patient.getEmail(), 20);
        JTextField phoneField = new JTextField(patient.getPhone(), 20);
        JTextField addressField = new JTextField(patient.getAddress(), 20);
        JTextField nhsNumberField = new JTextField(patient.getNhsNumber(), 20);
        JTextField dobField = new JTextField(patient.getFormattedDateOfBirth(), 20);
        JTextField medicalHistoryField = new JTextField(patient.getMedicalHistoryRef(), 20);
        
        int row = 0;
        addField(dialog, gbc, "First Name:", firstNameField, row++);
        addField(dialog, gbc, "Last Name:", lastNameField, row++);
        addField(dialog, gbc, "Email:", emailField, row++);
        addField(dialog, gbc, "Phone:", phoneField, row++);
        addField(dialog, gbc, "Address:", addressField, row++);
        addField(dialog, gbc, "NHS Number:", nhsNumberField, row++);
        addField(dialog, gbc, "Date of Birth (yyyy-MM-dd):", dobField, row++);
        addField(dialog, gbc, "Medical History Ref:", medicalHistoryField, row++);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = sdf.parse(dobField.getText().trim());
                
                patient.setFirstName(firstNameField.getText().trim());
                patient.setLastName(lastNameField.getText().trim());
                patient.setEmail(emailField.getText().trim());
                patient.setPhone(phoneField.getText().trim());
                patient.setAddress(addressField.getText().trim());
                patient.setNhsNumber(nhsNumberField.getText().trim());
                patient.setDateOfBirth(dob);
                patient.setMedicalHistoryRef(medicalHistoryField.getText().trim());
                
                if (patientController.updatePatient(patient)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Patient updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshPatientTable();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error updating patient",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating patient: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a patient to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientId = (String) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1) + " " + 
                           (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete patient " + patientId + " (" + patientName + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = Integer.parseInt(patientId.substring(1)); // Remove "P" prefix
            if (patientController.deletePatient(userId)) {
                JOptionPane.showMessageDialog(this,
                    "Patient deleted successfully",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPatientTable();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error deleting patient",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addField(JDialog dialog, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialog.add(field, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
    }

    private JPanel createPatientAppointmentsView(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Appointment ID", "Date & Time", "Clinician", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<model.Appointment> appointments = dataService.getAppointmentsForPatient(patient.getUserId());
        
        for (model.Appointment appointment : appointments) {
            Object[] row = {
                appointment.getAppointmentId(),
                appointment.getDateTime() != null ? dateTimeFormat.format(appointment.getDateTime()) : "",
                appointment.getClinician() != null ? appointment.getClinician().getUsername() : "",
                appointment.getStatus()
            };
            tableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        if (appointments.isEmpty()) {
            JLabel emptyLabel = new JLabel("No appointments found");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private JPanel createPatientPrescriptionsView(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("My Prescriptions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Prescription ID", "Drug", "Dosage", "Condition", "Clinician", "Date Issued"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<model.Prescription> prescriptions = dataService.getPrescriptionsForPatient(patient.getUserId());
        
        for (model.Prescription prescription : prescriptions) {
            Object[] row = {
                prescription.getPrescriptionId(),
                prescription.getDrug() != null ? prescription.getDrug().getName() : "",
                prescription.getDosage() != null ? prescription.getDosage() : "",
                prescription.getCondition(),
                prescription.getClinician() != null ? prescription.getClinician().getUsername() : "",
                prescription.getDateIssued() != null ? dateFormat.format(prescription.getDateIssued()) : ""
            };
            tableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        if (prescriptions.isEmpty()) {
            JLabel emptyLabel = new JLabel("No prescriptions found");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private JPanel createPatientReferralsView(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("My Referrals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Referral ID", "From Clinician", "To Specialist", "Reason", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        List<model.Referral> referrals = dataService.getReferralsForPatient(patient.getUserId());
        
        for (model.Referral referral : referrals) {
            Object[] row = {
                referral.getReferralId(),
                referral.getFromClinician() != null ? referral.getFromClinician().getUsername() : "",
                referral.getToSpecialist() != null ? referral.getToSpecialist().getUsername() : "",
                referral.getReason(),
                referral.getStatus()
            };
            tableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        if (referrals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No referrals found");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private JPanel createCliniciansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Clinician");
        JButton editButton = new JButton("Edit Clinician");
        JButton deleteButton = new JButton("Delete Clinician");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"Clinician ID", "Username", "Role", "Contact Info", "Specialty", "Qualifications"};
        DefaultTableModel clinicianTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable clinicianTable = new JTable(clinicianTableModel);
        clinicianTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clinicianTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        clinicianTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        clinicianTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        clinicianTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        clinicianTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        clinicianTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        clinicianTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(clinicianTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh function
        Runnable refreshClinicians = () -> {
            clinicianTableModel.setRowCount(0);
            List<Clinician> clinicians = dataService.getClinicians();
            for (Clinician c : clinicians) {
                Object[] row = {
                    c.getUserId(),
                    c.getUsername(),
                    c.getRole(),
                    c.getContactInfo(),
                    c.getSpecialty(),
                    c.getQualifications()
                };
                clinicianTableModel.addRow(row);
            }
        };
        
        // Button actions
        addButton.addActionListener(e -> showAddClinicianDialog(refreshClinicians));
        editButton.addActionListener(e -> showEditClinicianDialog(clinicianTable, clinicianTableModel, refreshClinicians));
        deleteButton.addActionListener(e -> deleteSelectedClinician(clinicianTable, clinicianTableModel, refreshClinicians));
        refreshButton.addActionListener(e -> refreshClinicians.run());
        
        // Initial load
        refreshClinicians.run();
        
        return panel;
    }

    private void showAddClinicianDialog(Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Clinician", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"GP", "Nurse", "Specialist", "Clinician"});
        JTextField contactField = new JTextField(20);
        JTextField specialtyField = new JTextField(20);
        JTextField qualificationsField = new JTextField(20);
        JTextField extraField = new JTextField(20);
        JLabel extraLabel = new JLabel("Hospital Department / Shift Schedule:");
        
        int row = 0;
        addField(dialog, gbc, "Username:", usernameField, row++);
        addField(dialog, gbc, "Password:", passwordField, row++);
        addField(dialog, gbc, "Role:", roleCombo, row++);
        addField(dialog, gbc, "Contact Info:", contactField, row++);
        addField(dialog, gbc, "Specialty:", specialtyField, row++);
        addField(dialog, gbc, "Qualifications:", qualificationsField, row++);
        addField(dialog, gbc, extraLabel.getText(), extraField, row++);
        
        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            if ("Nurse".equals(role)) {
                extraLabel.setText("Shift Schedule:");
            } else if ("Specialist".equals(role)) {
                extraLabel.setText("Hospital Department:");
            } else {
                extraLabel.setText("Extra Field:");
            }
        });
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            try {
                String role = (String) roleCombo.getSelectedItem();
                Clinician newClinician = clinicianController.addClinician(
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()),
                    role,
                    contactField.getText().trim(),
                    specialtyField.getText().trim(),
                    qualificationsField.getText().trim(),
                    extraField.getText().trim()
                );
                
                JOptionPane.showMessageDialog(dialog,
                    "Clinician added successfully! ID: " + newClinician.getUserId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCallback.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error adding clinician: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditClinicianDialog(JTable table, DefaultTableModel model, Runnable refreshCallback) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a clinician to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) model.getValueAt(selectedRow, 0);
        Clinician clinician = dataService.findClinicianById(userId);
        
        if (clinician == null) {
            JOptionPane.showMessageDialog(this,
                "Clinician not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Clinician", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField usernameField = new JTextField(clinician.getUsername(), 20);
        JTextField contactField = new JTextField(clinician.getContactInfo(), 20);
        JTextField specialtyField = new JTextField(clinician.getSpecialty(), 20);
        JTextField qualificationsField = new JTextField(clinician.getQualifications(), 20);
        JTextField extraField = new JTextField(20);
        JLabel extraLabel = new JLabel("Extra Field:");
        
        if (clinician instanceof model.Nurse) {
            extraField.setText(((model.Nurse) clinician).getShiftSchedule());
            extraLabel.setText("Shift Schedule:");
        } else if (clinician instanceof model.Specialist) {
            extraField.setText(((model.Specialist) clinician).getHospitalDepartment());
            extraLabel.setText("Hospital Department:");
        }
        
        int row = 0;
        addField(dialog, gbc, "Username:", usernameField, row++);
        addField(dialog, gbc, "Contact Info:", contactField, row++);
        addField(dialog, gbc, "Specialty:", specialtyField, row++);
        addField(dialog, gbc, "Qualifications:", qualificationsField, row++);
        addField(dialog, gbc, extraLabel.getText(), extraField, row++);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            try {
                clinician.setContactInfo(contactField.getText().trim());
                clinician.setSpecialty(specialtyField.getText().trim());
                // Note: Username and qualifications are protected, would need setters
                
                if (clinician instanceof model.Nurse) {
                    ((model.Nurse) clinician).setShiftSchedule(extraField.getText().trim());
                } else if (clinician instanceof model.Specialist) {
                    ((model.Specialist) clinician).setHospitalDepartment(extraField.getText().trim());
                }
                
                if (clinicianController.updateClinician(clinician)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Clinician updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshCallback.run();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error updating clinician",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating clinician: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedClinician(JTable table, DefaultTableModel model, Runnable refreshCallback) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a clinician to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) model.getValueAt(selectedRow, 0);
        String username = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete clinician " + userId + " (" + username + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (clinicianController.deleteClinician(userId)) {
                JOptionPane.showMessageDialog(this,
                    "Clinician deleted successfully",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error deleting clinician",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditAppointmentDialog(JTable table, DefaultTableModel model, Runnable refreshCallback) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Implementation for editing appointments - would need to load appointment and show edit dialog
        JOptionPane.showMessageDialog(this, "Edit appointment functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedAppointment(JTable table, DefaultTableModel model, Runnable refreshCallback) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appointmentId = (Integer) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete appointment " + appointmentId + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from file - would need to implement
            JOptionPane.showMessageDialog(this, "Delete appointment functionality - to be implemented", "Info", JOptionPane.INFORMATION_MESSAGE);
            refreshCallback.run();
        }
    }
}
