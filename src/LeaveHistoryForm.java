import javax.swing.*;
import java.sql.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

public class LeaveHistoryForm extends GlassFrame {

    JTable table;
    DefaultTableModel model;
    int userId;

    JFrame previousFrame;

    public LeaveHistoryForm(int userId, JFrame previousFrame) {

        super("Leave History", 750, 420, "src/images/bg.jpg");

        this.userId = userId;
        this.previousFrame = previousFrame;

        mainPanel.setLayout(new BorderLayout(10,10));

        String[] cols = {"ID","From","To","Reason","Status","Reject Reason"};

        model = new DefaultTableModel(cols,0);

        table = new JTable(model){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);

        JButton cancelBtn = new JButton("Cancel Selected Leave");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        bottom.add(cancelBtn);
        bottom.add(backBtn);

        mainPanel.add(scroll,BorderLayout.CENTER);
        mainPanel.add(bottom,BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> cancelLeave());

        // BACK BUTTON
        backBtn.addActionListener(e -> {

            this.dispose();

            previousFrame.setVisible(true);

        });

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