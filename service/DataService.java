package service;

import model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataService {
    private static DataService instance;
    private List<Patient> patients;
    private List<Clinician> clinicians;
    private List<Drug> drugs;

    private DataService() {
        patients = new ArrayList<>();
        clinicians = new ArrayList<>();
        drugs = new ArrayList<>();
        loadData();
    }

    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    private void loadData() {
        loadPatients();
        loadClinicians();
        loadDrugs();
    }

    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/patients.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int userId = Integer.parseInt(parts[0].trim());
                    String username = parts[1].trim();
                    String passwordHash = parts[2].trim();
                    String role = parts[3].trim();
                    String contactInfo = parts[4].trim();
                    String nhsNumber = parts[5].trim();
                    Date dateOfBirth = parseDate(parts.length > 6 ? parts[6].trim() : "");
                    String medicalHistoryRef = parts.length > 7 ? parts[7].trim() : "";
                    
                    // Handle both old format (6+ fields) and new format (11+ fields)
                    if (parts.length >= 11) {
                        // New format with firstName, lastName, email, phone, address
                        String firstName = parts[8].trim();
                        String lastName = parts[9].trim();
                        String email = parts[10].trim();
                        String phone = parts.length > 11 ? parts[11].trim() : "";
                        String address = parts.length > 12 ? parts[12].trim() : "";
                        
                        patients.add(new Patient(userId, username, passwordHash, role, contactInfo,
                                firstName, lastName, email, phone, address,
                                nhsNumber, dateOfBirth, medicalHistoryRef));
                    } else {
                        // Old format - create with defaults
                        Patient p = new Patient(userId, username, passwordHash, role, contactInfo,
                                nhsNumber, dateOfBirth, medicalHistoryRef);
                        p.setEmail(contactInfo);
                        patients.add(p);
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read - create default data
        }
        // If no patients were loaded, create default data
        if (patients.isEmpty()) {
            createDefaultPatients();
        }
    }

    private void loadClinicians() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/clinicians.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    int userId = Integer.parseInt(parts[0].trim());
                    String username = parts[1].trim();
                    String passwordHash = parts[2].trim();
                    String role = parts[3].trim();
                    String contactInfo = parts[4].trim();
                    String specialty = parts[5].trim();
                    String qualifications = parts[6].trim();
                    
                    if ("GP".equalsIgnoreCase(role)) {
                        clinicians.add(new GP(userId, username, passwordHash, role, contactInfo,
                                specialty, qualifications));
                    } else if ("Nurse".equalsIgnoreCase(role)) {
                        String shiftSchedule = parts.length > 7 ? parts[7].trim() : "";
                        clinicians.add(new Nurse(userId, username, passwordHash, role, contactInfo,
                                specialty, qualifications, shiftSchedule));
                    } else if ("Specialist".equalsIgnoreCase(role)) {
                        String hospitalDepartment = parts.length > 7 ? parts[7].trim() : "";
                        clinicians.add(new Specialist(userId, username, passwordHash, role, contactInfo,
                                specialty, qualifications, hospitalDepartment));
                    } else {
                        clinicians.add(new Clinician(userId, username, passwordHash, role, contactInfo,
                                specialty, qualifications));
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read - create default data
        }
        // If no clinicians were loaded, create default data
        if (clinicians.isEmpty()) {
            createDefaultClinicians();
        }
    }

    private void loadDrugs() {
        // Default drugs if file doesn't exist
        if (drugs.isEmpty()) {
            drugs.add(new Drug(1, "Paracetamol", "Tablet", "500mg twice daily"));
            drugs.add(new Drug(2, "Ibuprofen", "Tablet", "400mg three times daily"));
            drugs.add(new Drug(3, "Amoxicillin", "Capsule", "500mg three times daily"));
        }
    }

    private void createDefaultPatients() {
        try {
            Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
            patients.add(new Patient(1, "patient1", "pass1", "Patient", "patient1@email.com",
                    "John", "Doe", "patient1@email.com", "07123456789", "123 Main Street, Birmingham",
                    "NHS123456", dob, "HIST001"));
        } catch (Exception e) {
            patients.add(new Patient(1, "patient1", "pass1", "Patient", "patient1@email.com",
                    "John", "Doe", "patient1@email.com", "07123456789", "123 Main Street, Birmingham",
                    "NHS123456", new Date(), "HIST001"));
        }
    }

    private void createDefaultClinicians() {
        clinicians.add(new GP(101, "gp1", "pass1", "GP", "gp1@email.com",
                "General Practice", "MBBS"));
        clinicians.add(new Specialist(201, "specialist1", "pass1", "Specialist", "specialist1@email.com",
                "Cardiology", "MD Cardiology", "Cardiology Department"));
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public List<Clinician> getClinicians() {
        return new ArrayList<>(clinicians);
    }

    public List<Drug> getDrugs() {
        return new ArrayList<>(drugs);
    }

    public Patient findPatientByUsername(String username) {
        return patients.stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public Clinician findClinicianByUsername(String username) {
        return clinicians.stream()
                .filter(c -> c.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User findUserByUsername(String username) {
        User user = findPatientByUsername(username);
        if (user == null) {
            user = findClinicianByUsername(username);
        }
        return user;
    }

    public List<GP> getGPs() {
        List<GP> gps = new ArrayList<>();
        for (Clinician c : clinicians) {
            if (c instanceof GP) {
                gps.add((GP) c);
            }
        }
        return gps;
    }

    public List<Specialist> getSpecialists() {
        List<Specialist> specialists = new ArrayList<>();
        for (Clinician c : clinicians) {
            if (c instanceof Specialist) {
                specialists.add((Specialist) c);
            }
        }
        return specialists;
    }

    // Patient CRUD operations
    public void addPatient(Patient patient) {
        patients.add(patient);
        savePatients();
    }

    public void updatePatient(Patient updatedPatient) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getUserId() == updatedPatient.getUserId()) {
                patients.set(i, updatedPatient);
                savePatients();
                return;
            }
        }
    }

    public boolean deletePatient(int userId) {
        boolean removed = patients.removeIf(p -> p.getUserId() == userId);
        if (removed) {
            savePatients();
        }
        return removed;
    }

    public Patient findPatientById(int userId) {
        return patients.stream()
                .filter(p -> p.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    private void savePatients() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/patients.txt"))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Patient p : patients) {
                bw.write(p.getUserId() + "," +
                        p.getUsername() + "," +
                        p.getPasswordHash() + "," +
                        p.getRole() + "," +
                        p.getContactInfo() + "," +
                        p.getNhsNumber() + "," +
                        (p.getDateOfBirth() != null ? sdf.format(p.getDateOfBirth()) : "") + "," +
                        p.getMedicalHistoryRef() + "," +
                        p.getFirstName() + "," +
                        p.getLastName() + "," +
                        p.getEmail() + "," +
                        p.getPhone() + "," +
                        p.getAddress() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextPatientId() {
        return patients.stream().mapToInt(p -> p.getUserId()).max().orElse(0) + 1;
    }

    // Load appointments for a specific patient
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> patientAppointments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/appointments.txt"))) {
            String line;
            SimpleDateFormat[] dateFormats = {
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm"),
                new SimpleDateFormat("yyyy-MM-dd")
            };
            
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int appointmentId = Integer.parseInt(parts[0].trim());
                        int patId = Integer.parseInt(parts[1].trim());
                        int clinicianId = Integer.parseInt(parts[2].trim());
                        
                        if (patId == patientId) {
                            Date dateTime = null;
                            String dateStr = parts[3].trim();
                            
                            // Try different date formats
                            for (SimpleDateFormat format : dateFormats) {
                                try {
                                    dateTime = format.parse(dateStr);
                                    break;
                                } catch (Exception e) {
                                    // Try next format
                                }
                            }
                            
                            if (dateTime != null) {
                                String status = parts.length > 4 ? parts[4].trim() : "SCHEDULED";
                                
                                Patient patient = findPatientById(patId);
                                Clinician clinician = findClinicianById(clinicianId);
                                
                                if (patient != null && clinician != null) {
                                    Appointment appointment = new Appointment(patient, clinician, dateTime);
                                    appointment.setAppointmentId(appointmentId);
                                    appointment.setStatus(status);
                                    patientAppointments.add(appointment);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return patientAppointments;
    }

    // Load prescriptions for a specific patient
    public List<Prescription> getPrescriptionsForPatient(int patientId) {
        List<Prescription> patientPrescriptions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/prescriptions.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int prescriptionId = Integer.parseInt(parts[0].trim());
                    int patId = Integer.parseInt(parts[1].trim());
                    int clinicianId = Integer.parseInt(parts[2].trim());
                    
                    if (patId == patientId) {
                        String drugName = parts[3].trim();
                        String dosage = parts.length > 4 ? parts[4].trim() : "";
                        String condition = parts.length > 5 ? parts[5].trim() : "";
                        
                        Patient patient = findPatientById(patId);
                        Clinician clinician = findClinicianById(clinicianId);
                        Drug drug = findDrugByName(drugName);
                        
                        if (patient != null && clinician != null && drug != null) {
                            Prescription prescription = new Prescription(patient, clinician, drug, condition);
                            prescription.setPrescriptionId(prescriptionId);
                            if (!dosage.isEmpty()) {
                                // Prescription already sets dosage from drug, but we can override if needed
                            }
                            patientPrescriptions.add(prescription);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return patientPrescriptions;
    }

    // Load referrals for a specific patient
    public List<Referral> getReferralsForPatient(int patientId) {
        List<Referral> patientReferrals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/referrals.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int referralId = Integer.parseInt(parts[0].trim());
                    int fromClinicianId = Integer.parseInt(parts[1].trim());
                    int toSpecialistId = Integer.parseInt(parts[2].trim());
                    int patId = Integer.parseInt(parts[3].trim());
                    
                    if (patId == patientId) {
                        String reason = parts[4].trim();
                        String status = parts[5].trim();
                        
                        Patient patient = findPatientById(patId);
                        Clinician fromClinician = findClinicianById(fromClinicianId);
                        Specialist specialist = (Specialist) findClinicianById(toSpecialistId);
                        
                        if (patient != null && fromClinician != null && specialist != null) {
                            Referral referral = new Referral(fromClinician, specialist, patient, reason);
                            referral.setStatus(status);
                            patientReferrals.add(referral);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return patientReferrals;
    }

    public Clinician findClinicianById(int userId) {
        return clinicians.stream()
                .filter(c -> c.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    private Drug findDrugByName(String name) {
        return drugs.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // Load all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> allAppointments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/appointments.txt"))) {
            String line;
            SimpleDateFormat[] dateFormats = {
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm"),
                new SimpleDateFormat("yyyy-MM-dd")
            };
            
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int appointmentId = Integer.parseInt(parts[0].trim());
                        int patId = Integer.parseInt(parts[1].trim());
                        int clinicianId = Integer.parseInt(parts[2].trim());
                        
                        Date dateTime = null;
                        String dateStr = parts[3].trim();
                        
                        for (SimpleDateFormat format : dateFormats) {
                            try {
                                dateTime = format.parse(dateStr);
                                break;
                            } catch (Exception e) {
                                // Try next format
                            }
                        }
                        
                        if (dateTime != null) {
                            String status = parts.length > 4 ? parts[4].trim() : "SCHEDULED";
                            
                            Patient patient = findPatientById(patId);
                            Clinician clinician = findClinicianById(clinicianId);
                            
                            if (patient != null && clinician != null) {
                                Appointment appointment = new Appointment(patient, clinician, dateTime);
                                appointment.setAppointmentId(appointmentId);
                                appointment.setStatus(status);
                                allAppointments.add(appointment);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return allAppointments;
    }

    // Load all prescriptions
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> allPrescriptions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/prescriptions.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        int prescriptionId = Integer.parseInt(parts[0].trim());
                        int patId = Integer.parseInt(parts[1].trim());
                        int clinicianId = Integer.parseInt(parts[2].trim());
                        String drugName = parts[3].trim();
                        String dosage = parts.length > 4 ? parts[4].trim() : "";
                        String condition = parts.length > 5 ? parts[5].trim() : "";
                        
                        Patient patient = findPatientById(patId);
                        Clinician clinician = findClinicianById(clinicianId);
                        Drug drug = findDrugByName(drugName);
                        
                        if (patient != null && clinician != null && drug != null) {
                            Prescription prescription = new Prescription(patient, clinician, drug, condition);
                            prescription.setPrescriptionId(prescriptionId);
                            allPrescriptions.add(prescription);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return allPrescriptions;
    }

    // Load all referrals
    public List<Referral> getAllReferrals() {
        List<Referral> allReferrals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/referrals.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        int referralId = Integer.parseInt(parts[0].trim());
                        int fromClinicianId = Integer.parseInt(parts[1].trim());
                        int toSpecialistId = Integer.parseInt(parts[2].trim());
                        int patId = Integer.parseInt(parts[3].trim());
                        String reason = parts[4].trim();
                        String status = parts[5].trim();
                        
                        Patient patient = findPatientById(patId);
                        Clinician fromClinician = findClinicianById(fromClinicianId);
                        Specialist specialist = (Specialist) findClinicianById(toSpecialistId);
                        
                        if (patient != null && fromClinician != null && specialist != null) {
                            Referral referral = new Referral(fromClinician, specialist, patient, reason);
                            referral.setStatus(status);
                            allReferrals.add(referral);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        return allReferrals;
    }

    // Clinician CRUD operations
    public void addClinician(Clinician clinician) {
        clinicians.add(clinician);
        saveClinicians();
    }

    public void updateClinician(Clinician updatedClinician) {
        for (int i = 0; i < clinicians.size(); i++) {
            if (clinicians.get(i).getUserId() == updatedClinician.getUserId()) {
                clinicians.set(i, updatedClinician);
                saveClinicians();
                return;
            }
        }
    }

    public boolean deleteClinician(int userId) {
        boolean removed = clinicians.removeIf(c -> c.getUserId() == userId);
        if (removed) {
            saveClinicians();
        }
        return removed;
    }

    private void saveClinicians() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/clinicians.txt"))) {
            for (Clinician c : clinicians) {
                if (c instanceof GP) {
                    bw.write(c.getUserId() + "," +
                            c.getUsername() + "," +
                            c.getPasswordHash() + "," +
                            c.getRole() + "," +
                            c.getContactInfo() + "," +
                            c.getSpecialty() + "," +
                            c.getQualifications() + "\n");
                } else if (c instanceof Nurse) {
                    Nurse n = (Nurse) c;
                    bw.write(n.getUserId() + "," +
                            n.getUsername() + "," +
                            n.getPasswordHash() + "," +
                            n.getRole() + "," +
                            n.getContactInfo() + "," +
                            n.getSpecialty() + "," +
                            n.getQualifications() + "," +
                            n.getShiftSchedule() + "\n");
                } else if (c instanceof Specialist) {
                    Specialist s = (Specialist) c;
                    bw.write(s.getUserId() + "," +
                            s.getUsername() + "," +
                            s.getPasswordHash() + "," +
                            s.getRole() + "," +
                            s.getContactInfo() + "," +
                            s.getSpecialty() + "," +
                            s.getQualifications() + "," +
                            s.getHospitalDepartment() + "\n");
                } else {
                    bw.write(c.getUserId() + "," +
                            c.getUsername() + "," +
                            c.getPasswordHash() + "," +
                            c.getRole() + "," +
                            c.getContactInfo() + "," +
                            c.getSpecialty() + "," +
                            c.getQualifications() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextClinicianId() {
        return clinicians.stream().mapToInt(c -> c.getUserId()).max().orElse(100) + 1;
    }
}

