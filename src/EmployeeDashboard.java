import javax.swing.*;
import java.sql.*;

public class EmployeeDashboard extends JFrame {

    int userId;
    JLabel balanceLabel = new JLabel();

    public EmployeeDashboard(int userId) {

        this.userId = userId;

        setTitle("Employee Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Welcome Label
        JLabel welcome = new JLabel("Welcome Employee ID: " + userId);
        welcome.setBounds(30, 10, 300, 20);
        add(welcome);

        // Leave Balance Label
        balanceLabel.setBounds(30, 30, 300, 20);
        add(balanceLabel);

        // Buttons
        JButton applyLeaveBtn = new JButton("Apply for Leave");
        JButton viewHistoryBtn = new JButton("View Leave History");
        JButton logoutBtn = new JButton("Logout");

        applyLeaveBtn.setBounds(100, 70, 180, 30);
        viewHistoryBtn.setBounds(100, 120, 180, 30);
        logoutBtn.setBounds(100, 170, 180, 30);

        // Button Actions
        applyLeaveBtn.addActionListener(e -> new ApplyLeaveForm(userId));
        viewHistoryBtn.addActionListener(e -> new LeaveHistoryForm(userId));

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
        });

        add(applyLeaveBtn);
        add(viewHistoryBtn);
        add(logoutBtn);

        // Load Leave Balance
        loadLeaveBalance();

        setVisible(true);
    }

    // METHOD TO LOAD LEAVE BALANCE
    void loadLeaveBalance() {

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT leave_balance FROM users WHERE id=?");

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int balance = rs.getInt("leave_balance");

                balanceLabel.setText("Remaining Leave Balance: " + balance);

                // OPTIONAL PRO FEATURE (Color warning)
                if (balance <= 3) {
                    balanceLabel.setForeground(java.awt.Color.RED);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 