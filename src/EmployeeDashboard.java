import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EmployeeDashboard extends GlassFrame {

    int userId;
    JLabel balanceLabel = new JLabel();

    public EmployeeDashboard(int userId) {

        super("Employee Dashboard", 420, 320, "src/images/bg.jpg");

        this.userId = userId;

        mainPanel.setLayout(new GridLayout(5,1,10,10));

        JLabel welcome = new JLabel("Welcome Employee ID: " + userId, SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton applyLeaveBtn = new JButton("Apply for Leave");
        JButton viewHistoryBtn = new JButton("View Leave History");
        JButton logoutBtn = new JButton("Logout");

        JPanel btn1 = new JPanel();
        btn1.setOpaque(false);
        btn1.add(applyLeaveBtn);

        JPanel btn2 = new JPanel();
        btn2.setOpaque(false);
        btn2.add(viewHistoryBtn);

        JPanel btn3 = new JPanel();
        btn3.setOpaque(false);
        btn3.add(logoutBtn);

        mainPanel.add(welcome);
        mainPanel.add(balanceLabel);
        mainPanel.add(btn1);
        mainPanel.add(btn2);
        mainPanel.add(btn3);

        // OPEN APPLY LEAVE SCREEN
        applyLeaveBtn.addActionListener(e -> {
            this.setVisible(false);   // hide dashboard
            new ApplyLeaveForm(userId, this); // pass dashboard reference
        });

        // OPEN LEAVE HISTORY SCREEN
        viewHistoryBtn.addActionListener(e -> {
            this.setVisible(false);   // hide dashboard
            new LeaveHistoryForm(userId, this); // pass dashboard reference
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
        });

        loadLeaveBalance();
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
                        balanceLabel.setForeground(Color.RED);
                    } else {
                        balanceLabel.setForeground(Color.BLACK);
                    }

                } else {

                    balanceLabel.setText("Leave Deficit: " + Math.abs(balance) + " days");
                    balanceLabel.setForeground(Color.RED);

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