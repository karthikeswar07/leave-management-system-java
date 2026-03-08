import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchEmployeeLeaveForm extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField userIdField;

    public SearchEmployeeLeaveForm() {
        setTitle("Search Employee Leave History");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with input
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter Employee ID:"));
        userIdField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        topPanel.add(userIdField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // Table for results
        model = new DefaultTableModel(new String[]{
            "Leave ID", "User ID", "From Date", "To Date", "Reason", "Status"
        }, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadLeaveData());

        setVisible(true);
    }

    void loadLeaveData() {
        model.setRowCount(0);
        String input = userIdField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an employee ID.");
            return;
        }

        try {
            int userId = Integer.parseInt(input);
            try (Connection con = DB.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM leaves WHERE user_id=?");
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("from_date"),
                        rs.getString("to_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                    });
                }
                if (!hasData) {
                    JOptionPane.showMessageDialog(this, "No leave records found for employee ID " + userId);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
