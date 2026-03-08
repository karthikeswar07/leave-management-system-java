import javax.swing.*;
import java.sql.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class LeaveHistoryForm extends JFrame {
    public LeaveHistoryForm(int userId) {
        setTitle("Leave History");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] cols = {"ID", "From", "To", "Reason", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setEnabled(false);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        try (Connection con = DB.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM leaves WHERE user_id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("from_date"),
                    rs.getString("to_date"),
                    rs.getString("reason"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setVisible(true);
    }
}
