import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JComboBox<String> roleBox = new JComboBox<>(new String[]{"Employee", "Admin"});
    JButton registerBtn = new JButton("Register");
    JButton backToLoginBtn = new JButton("Back to Login");

    public RegisterForm() {
        setTitle("User Registration");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("Register Account", JLabel.CENTER);
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

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 140, 100, 25);
        add(roleLabel);
        roleBox.setBounds(150, 140, 180, 25);
        add(roleBox);

        registerBtn.setBounds(80, 190, 110, 30);
        backToLoginBtn.setBounds(210, 190, 110, 30);
        add(registerBtn);
        add(backToLoginBtn);

        registerBtn.addActionListener(e -> registerUser());
        backToLoginBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
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
            // Check for duplicate username
            PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE username=?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose another.",
                    "Duplicate Username",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(username, password, role) VALUES(?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginForm();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error during registration:\n" + ex.getMessage(),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
