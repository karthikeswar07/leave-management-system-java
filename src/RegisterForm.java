import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterForm extends GlassFrame {

    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JComboBox<String> roleBox = new JComboBox<>(new String[]{"Employee", "Admin"});
    JButton registerBtn = new JButton("Register");
    JButton backToLoginBtn = new JButton("Back to Login");

    JFrame previousFrame;

    public RegisterForm(JFrame previousFrame) {

        super("User Registration", 450, 320, "src/images/bg.jpg");

        this.previousFrame = previousFrame;

        mainPanel.setLayout(new GridLayout(6,1,10,10));

        JLabel title = new JLabel("Register Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);
        passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);

        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setOpaque(false);
        rolePanel.add(new JLabel("Role:"), BorderLayout.WEST);
        rolePanel.add(roleBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerBtn);
        buttonPanel.add(backToLoginBtn);

        mainPanel.add(title);
        mainPanel.add(userPanel);
        mainPanel.add(passPanel);
        mainPanel.add(rolePanel);
        mainPanel.add(new JLabel());
        mainPanel.add(buttonPanel);

        registerBtn.addActionListener(e -> registerUser());

        backToLoginBtn.addActionListener(e -> {
            this.dispose();
            previousFrame.setVisible(true);
        });

        setVisible(true);
    }

    private void registerUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DB.getConnection()) {

            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM users WHERE username=?");

            check.setString(1, username);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose another.",
                        "Duplicate Username",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(username, password, role) VALUES(?, ?, ?)");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Registration successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            this.dispose();
            previousFrame.setVisible(true);

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Error during registration:\n" + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}