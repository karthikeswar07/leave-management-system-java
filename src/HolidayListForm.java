import javax.swing.*;
import java.sql.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class HolidayListForm extends JFrame {

    public HolidayListForm() {

        setTitle("Company Holidays");
        setSize(400,300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] cols = {"Date","Holiday Name"};

        DefaultTableModel model = new DefaultTableModel(cols,0);

        JTable table = new JTable(model){
            public boolean isCellEditable(int r,int c){
                return false;
            }
        };

        JScrollPane scroll = new JScrollPane(table);
        add(scroll,BorderLayout.CENTER);

        try(Connection con = DB.getConnection()){

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
            "SELECT holiday_date, holiday_name FROM holidays ORDER BY holiday_date");

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getString("holiday_date"),
                        rs.getString("holiday_name")
                });

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        setVisible(true);
    }
}