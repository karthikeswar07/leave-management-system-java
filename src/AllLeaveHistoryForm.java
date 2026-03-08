import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllLeaveHistoryForm extends JFrame {

    JTable table;
    DefaultTableModel model;

    public AllLeaveHistoryForm() {
        setTitle("All Employee Leave History");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
            "Leave ID", "User ID", "From Date", "To Date", "Reason", "Status"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadAllLeaveData();
        setVisible(true);
    }

    void loadAllLeaveData() {
        model.setRowCount(0);
        try (Connection con = DB.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM leaves ORDER BY id DESC");
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
