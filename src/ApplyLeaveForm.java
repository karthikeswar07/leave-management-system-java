import javax.swing.*;
import java.sql.*;

public class ApplyLeaveForm extends JFrame {

    JTextField fromDate = new JTextField();
    JTextField toDate = new JTextField();
    JTextArea reasonArea = new JTextArea();

    JButton submitBtn = new JButton("Submit");
    JButton viewHolidayBtn = new JButton("View Holidays");

    public ApplyLeaveForm(int userId) {

        setTitle("Apply for Leave");
        setSize(350, 330);
        setLayout(null);
        setLocationRelativeTo(null);

        add(new JLabel("From Date (YYYY-MM-DD):")).setBounds(20, 20, 200, 20);
        add(fromDate).setBounds(180, 20, 120, 20);

        add(new JLabel("To Date (YYYY-MM-DD):")).setBounds(20, 60, 200, 20);
        add(toDate).setBounds(180, 60, 120, 20);

        add(new JLabel("Reason:")).setBounds(20, 100, 80, 20);
        add(reasonArea).setBounds(100, 100, 200, 80);

        add(submitBtn).setBounds(50, 210, 100, 30);
        add(viewHolidayBtn).setBounds(170, 210, 120, 30);

        // VIEW HOLIDAYS WINDOW
        viewHolidayBtn.addActionListener(e -> new HolidayListForm());

        submitBtn.addActionListener(e -> {

            try (Connection con = DB.getConnection()) {

                String from = fromDate.getText().trim();
                String to = toDate.getText().trim();

                // Convert to LocalDate
                java.time.LocalDate fromD = java.time.LocalDate.parse(from);
                java.time.LocalDate toD = java.time.LocalDate.parse(to);

                long requestedDays =
                        java.time.temporal.ChronoUnit.DAYS.between(fromD, toD) + 1;

                // CHECK OVERLAPPING LEAVES
                PreparedStatement overlapCheck = con.prepareStatement(
                        "SELECT * FROM leaves WHERE user_id=? AND status IN ('Pending','Approved') " +
                        "AND ((? BETWEEN from_date AND to_date) OR (? BETWEEN from_date AND to_date) OR (from_date BETWEEN ? AND ?))");

                overlapCheck.setInt(1, userId);
                overlapCheck.setString(2, from);
                overlapCheck.setString(3, to);
                overlapCheck.setString(4, from);
                overlapCheck.setString(5, to);

                ResultSet overlapRs = overlapCheck.executeQuery();

                if (overlapRs.next()) {

                    JOptionPane.showMessageDialog(this,
                            "Leave request overlaps with an existing leave!");

                    return;
                }

                // CHECK LEAVE BALANCE
                PreparedStatement checkBalance = con.prepareStatement(
                        "SELECT leave_balance FROM users WHERE id=?");

                checkBalance.setInt(1, userId);

                ResultSet rs = checkBalance.executeQuery();

                if (rs.next()) {

                    int balance = rs.getInt("leave_balance");

                    // WARNING IF LEAVE EXCEEDS BALANCE
                    if (requestedDays > balance) {

                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Requested leave exceeds your balance by "
                                        + (requestedDays - balance) +
                                        " days.\n\nDo you want to continue and send request to admin?",
                                "Leave Balance Warning",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                }

                // INSERT LEAVE REQUEST
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO leaves(user_id, from_date, to_date, reason) VALUES(?, ?, ?, ?)");

                ps.setInt(1, userId);
                ps.setString(2, from);
                ps.setString(3, to);
                ps.setString(4, reasonArea.getText().trim());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Leave request submitted successfully!");

                this.dispose();

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        "Error submitting leave request.");

            }

        });

        setVisible(true);
    }
}