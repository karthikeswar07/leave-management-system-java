import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ApplyLeaveForm extends GlassFrame {

    JTextField fromDate = new JTextField();
    JTextField toDate = new JTextField();
    JTextArea reasonArea = new JTextArea();

    JButton submitBtn = new JButton("Submit");
    JButton viewHolidayBtn = new JButton("View Holidays");
    JButton backBtn = new JButton("Back");

    JFrame previousFrame;
    int userId;

    public ApplyLeaveForm(int userId, JFrame previousFrame) {

        super("Apply for Leave", 500, 380, "src/images/bg.jpg");

        this.previousFrame = previousFrame;
        this.userId = userId;

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Apply Leave Form", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("From Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        mainPanel.add(fromDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("To Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        mainPanel.add(toDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Reason:"), gbc);

        gbc.gridx = 1;
        reasonArea.setRows(3);
        JScrollPane scroll = new JScrollPane(reasonArea);
        mainPanel.add(scroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(submitBtn, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewHolidayBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(backBtn, gbc);

        // VIEW HOLIDAYS WINDOW
        viewHolidayBtn.addActionListener(e -> new HolidayListForm());

        // BACK BUTTON
        backBtn.addActionListener(e -> {

            this.dispose();

            if(previousFrame instanceof EmployeeDashboard){
                ((EmployeeDashboard) previousFrame).loadLeaveBalance();
            }

            previousFrame.setVisible(true);
        });

        submitBtn.addActionListener(e -> {

            try (Connection con = DB.getConnection()) {

                String from = fromDate.getText().trim();
                String to = toDate.getText().trim();

                java.time.LocalDate fromD = java.time.LocalDate.parse(from);
                java.time.LocalDate toD = java.time.LocalDate.parse(to);

                long requestedDays =
                        java.time.temporal.ChronoUnit.DAYS.between(fromD, toD) + 1;

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

                PreparedStatement checkBalance = con.prepareStatement(
                        "SELECT leave_balance FROM users WHERE id=?");

                checkBalance.setInt(1, userId);

                ResultSet rs = checkBalance.executeQuery();

                if (rs.next()) {

                    int balance = rs.getInt("leave_balance");

                    if (requestedDays > balance) {

                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Requested leave exceeds your balance by "
                                        + (requestedDays - balance) +
                                        " days.\n\nDo you want to continue?",
                                "Leave Balance Warning",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                }

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

                if(previousFrame instanceof EmployeeDashboard){
                    ((EmployeeDashboard) previousFrame).loadLeaveBalance();
                }

                previousFrame.setVisible(true);

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        "Error submitting leave request.");
            }
        });

        setVisible(true);
    }
}