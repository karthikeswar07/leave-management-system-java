import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends GlassFrame {

    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register");

    public LoginForm() {

        super("Login - Employee Leave Management", 420, 300, "src/images/bg.jpg");

        mainPanel.setLayout(new GridLayout(5,1,10,10));

        JLabel title = new JLabel("User Login", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);
        passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(title);
        mainPanel.add(userPanel);
        mainPanel.add(passPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        loginBtn.addActionListener(e -> loginUser());

        registerBtn.addActionListener(e -> {
            this.setVisible(false);
            new RegisterForm(this);
        });
        setVisible(true);
    }

    private void loginUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in both username and password.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?");

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role = rs.getString("role");
                int userId = rs.getInt("id");

                JOptionPane.showMessageDialog(this,
                        "Login successful!",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);

                this.dispose();

                if ("Admin".equalsIgnoreCase(role)) {

                    new AdminDashboard();

                } else {

                    new EmployeeDashboard(userId);

                }

            } else {

                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Error connecting to database:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }
    }
}