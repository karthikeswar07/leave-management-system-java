import javax.swing.*;
import java.sql.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

public class LeaveHistoryForm extends JFrame {

    JTable table;
    DefaultTableModel model;
    int userId;

    public LeaveHistoryForm(int userId) {

        this.userId = userId;

        setTitle("Leave History");
        setSize(700,350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] cols = {"ID","From","To","Reason","Status","Reject Reason"};

        model = new DefaultTableModel(cols,0);

        table = new JTable(model){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll,BorderLayout.CENTER);

        JButton cancelBtn = new JButton("Cancel Selected Leave");

        JPanel bottom = new JPanel();
        bottom.add(cancelBtn);

        add(bottom,BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> cancelLeave());

        loadLeaves();

        setVisible(true);
    }

    void loadLeaves(){

        model.setRowCount(0);

        try(Connection con = DB.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM leaves WHERE user_id=?");

            ps.setInt(1,userId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                String rejectReason = rs.getString("reject_reason");

                if(rejectReason == null){
                    rejectReason = "-";
                }

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("from_date"),
                        rs.getString("to_date"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rejectReason
                });

            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    void cancelLeave(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select a leave first");
            return;
        }

        int leaveId = (int) model.getValueAt(row,0);
        String fromDate = model.getValueAt(row,1).toString();
        String status = model.getValueAt(row,4).toString();

        LocalDate today = LocalDate.now();
        LocalDate leaveDate = LocalDate.parse(fromDate);

        if(!status.equals("Pending")){
            JOptionPane.showMessageDialog(this,"Only Pending leave can be cancelled");
            return;
        }

        if(!today.isBefore(leaveDate)){
            JOptionPane.showMessageDialog(this,"Leave cannot be cancelled after it starts");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this leave?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION){
            return;
        }

        try(Connection con = DB.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE leaves SET status='Cancelled' WHERE id=?");

            ps.setInt(1,leaveId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Leave Cancelled Successfully");

            loadLeaves();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}