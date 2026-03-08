import javax.swing.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel label = new JLabel("Welcome Admin");
        label.setBounds(130, 20, 200, 30);
        add(label);

        JButton approveBtn = new JButton("Pending Leave Approval");
        JButton viewAllHistoryBtn = new JButton("View All Leave History");
        JButton viewSpecificHistoryBtn = new JButton("View Employee Leave History");
        JButton logoutBtn = new JButton("Logout");

        approveBtn.addActionListener(e -> new AdminApprovalForm().setVisible(true));
        viewAllHistoryBtn.addActionListener(e -> new AllLeaveHistoryForm().setVisible(true));
        viewSpecificHistoryBtn.addActionListener(e -> new SearchEmployeeLeaveForm().setVisible(true));

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
        });

        approveBtn.setBounds(100, 70, 200, 30);
        viewAllHistoryBtn.setBounds(100, 120, 200, 30);
        viewSpecificHistoryBtn.setBounds(100, 170, 200, 30);
        logoutBtn.setBounds(100, 220, 200, 30);

        add(approveBtn);
        add(viewAllHistoryBtn);
        add(viewSpecificHistoryBtn);
        add(logoutBtn);

        setVisible(true);
    }
}