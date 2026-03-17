import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchEmployeeLeaveForm extends GlassFrame {

    JTable table;
    DefaultTableModel model;
    JTextField userIdField;

    JFrame previousFrame;

    public SearchEmployeeLeaveForm(JFrame previousFrame) {

        super("Search Employee Leave History", 850, 450, "src/images/bg.jpg");

        this.previousFrame = previousFrame;

        mainPanel.setLayout(new BorderLayout(10,10));

        // TOP SEARCH PANEL
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);

        topPanel.add(new JLabel("Enter Employee ID:"));

        userIdField = new JTextField(10);

        JButton searchBtn = new JButton("Search");
        JButton backBtn = new JButton("Back");

        topPanel.add(userIdField);
        topPanel.add(searchBtn);
        topPanel.add(backBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(new String[]{
                "Leave ID", "User ID", "From Date", "To Date", "Reason", "Status"
        }, 0);

        table = new JTable(model);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);

        mainPanel.add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadLeaveData());

        // BACK BUTTON
        backBtn.addActionListener(e -> {

            this.dispose();
            previousFrame.setVisible(true);

        });

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

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM leaves WHERE user_id=?");

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

                    JOptionPane.showMessageDialog(this,
                            "No leave records found for employee ID " + userId);
                }

            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this,
                    "Employee ID must be a number.");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}