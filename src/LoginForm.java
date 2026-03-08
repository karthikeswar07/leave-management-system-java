import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {

    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register");

    public LoginForm() {
        setTitle("Login - Employee Leave Management");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("User Login", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(100, 10, 200, 30);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 60, 100, 25);
        add(userLabel);

        usernameField.setBounds(150, 60, 180, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 25);
        add(passLabel);

        passwordField.setBounds(150, 100, 180, 25);
        add(passwordField);

        loginBtn.setBounds(70, 150, 100, 30);
        registerBtn.setBounds(210, 150, 100, 30);
        add(loginBtn);
        add(registerBtn);

        loginBtn.addActionListener(e -> loginUser());
        registerBtn.addActionListener(e -> {
            this.dispose();
            new RegisterForm();
        });

        setVisible(true);
    }

    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // 🧠 Input validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in both username and password.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DB.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error connecting to database:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
