import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String status = value.toString();

        if(status.equalsIgnoreCase("Approved")){
            c.setForeground(new Color(0,150,0));
        }
        else if(status.equalsIgnoreCase("Rejected")){
            c.setForeground(Color.RED);
        }
        else if(status.equalsIgnoreCase("Pending")){
            c.setForeground(new Color(255,140,0));
        }
        else if(status.equalsIgnoreCase("Cancelled")){
            c.setForeground(Color.GRAY);
        }
        else{
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}