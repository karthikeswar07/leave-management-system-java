import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdminApprovalForm extends GlassFrame {

    JTable table;
    DefaultTableModel model;

    JButton approveBtn, rejectBtn, refreshBtn, backBtn;

    JFrame previousFrame;

    public AdminApprovalForm(JFrame previousFrame) {

        super("Admin Leave Approval", 800, 450, "src/images/bg.jpg");

        this.previousFrame = previousFrame;

        mainPanel.setLayout(new BorderLayout(10,10));

        model = new DefaultTableModel();
        table = new JTable(model);
       

        model.setColumnIdentifiers(new String[]{
                "Leave ID","User ID","From Date","To Date","Reason","Status"
        });

        table.setRowHeight(25);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        JScrollPane pane = new JScrollPane(table);

        approveBtn = new JButton("Approve");
        rejectBtn = new JButton("Reject");
        refreshBtn = new JButton("Refresh");
        backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        bottom.add(approveBtn);
        bottom.add(rejectBtn);
        bottom.add(refreshBtn);
        bottom.add(backBtn);

        mainPanel.add(pane, BorderLayout.CENTER);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> approveLeave());
        rejectBtn.addActionListener(e -> rejectLeave());
        refreshBtn.addActionListener(e -> loadLeaves());

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

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT id,user_id,from_date,to_date,reason,status FROM leaves WHERE status='Pending'"
            );

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDate("from_date"),
                        rs.getDate("to_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                });

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void approveLeave(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select a leave request");
            return;
        }

        int leaveId = (int) model.getValueAt(row,0);

        try(Connection con = DB.getConnection()){

            PreparedStatement getLeave = con.prepareStatement(
                    "SELECT user_id, from_date, to_date FROM leaves WHERE id=?"
            );

            getLeave.setInt(1,leaveId);

            ResultSet rs = getLeave.executeQuery();

            if(rs.next()){

                int userId = rs.getInt("user_id");

                LocalDate from = rs.getDate("from_date").toLocalDate();
                LocalDate to = rs.getDate("to_date").toLocalDate();

                long days = ChronoUnit.DAYS.between(from,to) + 1;

                PreparedStatement reduceBalance = con.prepareStatement(
                        "UPDATE users SET leave_balance = leave_balance - ? WHERE id=?"
                );

                reduceBalance.setLong(1,days);
                reduceBalance.setInt(2,userId);

                reduceBalance.executeUpdate();
            }

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE leaves SET status='Approved', reject_reason=NULL WHERE id=?"
            );

            ps.setInt(1,leaveId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Leave Approved");

            loadLeaves();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void rejectLeave(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select a leave request");
            return;
        }

        int leaveId = (int) model.getValueAt(row,0);

        String reason = JOptionPane.showInputDialog(this,"Enter rejection reason:");

        if(reason == null || reason.trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Rejection reason required");
            return;
        }

        try(Connection con = DB.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE leaves SET status='Rejected', reject_reason=? WHERE id=?"
            );

            ps.setString(1,reason);
            ps.setInt(2,leaveId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Leave Rejected");

            loadLeaves();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}