import javax.swing.*;
import java.sql.*;

public class ApplyLeaveForm extends JFrame {
    JTextField fromDate = new JTextField();
    JTextField toDate = new JTextField();
    JTextArea reasonArea = new JTextArea();
    JButton submitBtn = new JButton("Submit");

    public ApplyLeaveForm(int userId) {
        setTitle("Apply for Leave");
        setSize(350, 300);
        setLayout(null);
        setLocationRelativeTo(null); // Center the frame

        add(new JLabel("From Date (YYYY-MM-DD):")).setBounds(20, 20, 200, 20);
        add(fromDate).setBounds(180, 20, 120, 20);
        add(new JLabel("To Date (YYYY-MM-DD):")).setBounds(20, 60, 200, 20);
        add(toDate).setBounds(180, 60, 120, 20);
        add(new JLabel("Reason:")).setBounds(20, 100, 80, 20);
        add(reasonArea).setBounds(100, 100, 200, 80);
        add(submitBtn).setBounds(120, 200, 100, 30);

        submitBtn.addActionListener(e -> {
            try (Connection con = DB.getConnection()) {

                String from = fromDate.getText().trim();
                String to = toDate.getText().trim();

                // Calculate number of leave days
                java.time.LocalDate fromD = java.time.LocalDate.parse(from);
                java.time.LocalDate toD = java.time.LocalDate.parse(to);

                long requestedDays = java.time.temporal.ChronoUnit.DAYS.between(fromD, toD) + 1;

                // STEP 1: Check leave balance
                PreparedStatement checkBalance = con.prepareStatement(
                    "SELECT leave_balance FROM users WHERE id=?");

                checkBalance.setInt(1, userId);

                ResultSet rs = checkBalance.executeQuery();

                if(rs.next()) {

                    int balance = rs.getInt("leave_balance");

                    // If requested leaves exceed balance
                    if(requestedDays > balance) {
                        JOptionPane.showMessageDialog(this,
                                "Not enough leave balance! Remaining: " + balance);
                        return;
                    }
                }

                // STEP 2: Insert leave request
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO leaves(user_id, from_date, to_date, reason) VALUES(?, ?, ?, ?)");

                ps.setInt(1, userId);
                ps.setString(2, from);
                ps.setString(3, to);
                ps.setString(4, reasonArea.getText().trim());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Leave Applied Successfully!");
                this.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error submitting leave request.");
            }
        });


        setVisible(true);
    }
}
