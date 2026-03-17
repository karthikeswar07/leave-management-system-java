import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends GlassFrame {

    public AdminDashboard() {

        super("Admin Dashboard", 450, 350, "src/images/bg.jpg");

        mainPanel.setLayout(new GridLayout(5,1,10,10));

        JLabel label = new JLabel("Welcome Admin", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        JButton approveBtn = new JButton("Pending Leave Approval");
        JButton viewAllHistoryBtn = new JButton("View All Leave History");
        JButton viewSpecificHistoryBtn = new JButton("View Employee Leave History");
        JButton logoutBtn = new JButton("Logout");

        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setOpaque(false);
        buttonPanel1.add(approveBtn);

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setOpaque(false);
        buttonPanel2.add(viewAllHistoryBtn);

        JPanel buttonPanel3 = new JPanel();
        buttonPanel3.setOpaque(false);
        buttonPanel3.add(viewSpecificHistoryBtn);

        JPanel buttonPanel4 = new JPanel();
        buttonPanel4.setOpaque(false);
        buttonPanel4.add(logoutBtn);

        mainPanel.add(label);
        mainPanel.add(buttonPanel1);
        mainPanel.add(buttonPanel2);
        mainPanel.add(buttonPanel3);
        mainPanel.add(buttonPanel4);

        // OPEN ADMIN APPROVAL SCREEN
        approveBtn.addActionListener(e -> {
            this.setVisible(false);
            new AdminApprovalForm(this);
        });

        // OPEN ALL LEAVE HISTORY
        viewAllHistoryBtn.addActionListener(e -> {
            this.setVisible(false);
            new AllLeaveHistoryForm(this);
        });

        // OPEN SEARCH EMPLOYEE HISTORY
        viewSpecificHistoryBtn.addActionListener(e -> {
            this.setVisible(false);
            new SearchEmployeeLeaveForm(this);
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
        });

        setVisible(true);
    }
}