import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllLeaveHistoryForm extends GlassFrame {

    JTable table;
    DefaultTableModel model;

    JFrame previousFrame;

    public AllLeaveHistoryForm(JFrame previousFrame) {

        super("All Employee Leave History", 850, 450, "src/images/bg.jpg");

        this.previousFrame = previousFrame;

        mainPanel.setLayout(new BorderLayout(10,10));

        model = new DefaultTableModel(new String[]{
                "Leave ID", "User ID", "From Date", "To Date", "Reason", "Status"
        }, 0);

        table = new JTable(model);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(backBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        // BACK BUTTON
        backBtn.addActionListener(e -> {

            this.dispose();
            previousFrame.setVisible(true);

        });

        loadAllLeaveData();

        setVisible(true);
    }

    void loadAllLeaveData() {

        model.setRowCount(0);

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM leaves ORDER BY id DESC");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("from_date"),
                        rs.getString("to_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                });

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}