package view;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(User user) {
        setLayout(new BorderLayout());
        
        // Use PatientViewPanel for all users as it has the tabbed interface
        // PatientViewPanel handles role-based access internally
        add(new PatientViewPanel(user), BorderLayout.CENTER);
    }
}

