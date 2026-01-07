package view;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Healthcare Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add login panel
        mainPanel.add(new LoginPanel(this), "LOGIN");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showMainPanel(User user) {
        // Remove existing main panel if any
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof ReferralPanel || comp instanceof MainMenuPanel) {
                mainPanel.remove(comp);
            }
        }
        
        // Add appropriate panel based on user role
        if ("GP".equals(user.getRole()) || "Specialist".equals(user.getRole()) || "Nurse".equals(user.getRole())) {
            mainPanel.add(new MainMenuPanel(user), "MAIN");
            cardLayout.show(mainPanel, "MAIN");
        } else {
            // For patients, show a simpler view
            mainPanel.add(new MainMenuPanel(user), "MAIN");
            cardLayout.show(mainPanel, "MAIN");
        }
    }
}
