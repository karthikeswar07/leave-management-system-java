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

        // CHECK REJECTED LEAVE NOTIFICATIONS
        checkNotifications();

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

                if(balance >= 0){

                    balanceLabel.setText("Remaining Leave Balance: " + balance);

                    if (balance <= 3) {
                        balanceLabel.setForeground(java.awt.Color.RED);
                    } else {
                        balanceLabel.setForeground(java.awt.Color.BLACK);
                    }

                } else {

                    balanceLabel.setText("Leave Deficit: " + Math.abs(balance) + " days");
                    balanceLabel.setForeground(java.awt.Color.RED);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // METHOD TO SHOW REJECTED LEAVE NOTIFICATION
    void checkNotifications() {

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, from_date, to_date, reject_reason FROM leaves WHERE user_id=? AND status='Rejected' AND notified=false");

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int leaveId = rs.getInt("id");
                String from = rs.getString("from_date");
                String to = rs.getString("to_date");
                String reason = rs.getString("reject_reason");

                JOptionPane.showMessageDialog(this,
                        "Leave Request Update\n\nYour leave from " + from + " to " + to +
                        " was rejected.\nReason: " + reason);

                PreparedStatement update = con.prepareStatement(
                        "UPDATE leaves SET notified=true WHERE id=?");

                update.setInt(1, leaveId);
                update.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}