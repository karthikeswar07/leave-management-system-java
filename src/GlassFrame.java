import javax.swing.*;
import java.awt.*;

public class GlassFrame extends JFrame {

    protected JPanel mainPanel;

    public GlassFrame(String title, int width, int height, String imagePath) {

        setTitle(title);

        // Full screen window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background image panel
        BackgroundPanel bg = new BackgroundPanel(imagePath);
        bg.setLayout(new GridBagLayout()); // centers panel

        // Transparent glass panel
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(width, height));

        // transparency
        mainPanel.setBackground(new Color(255,255,255,200));

        // smoother UI
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        bg.add(mainPanel);

        setContentPane(bg);

        
    }
}