import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserAuthSystem {
    public static void main(String[] args) {
        new LoginPage();
    }
}

class LoginPage extends JFrame implements ActionListener {
	//declaration
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, clearButton, exitButton;
    private JLabel usernameLabel, passwordLabel, roleLabel, titleLabel;
    private JComboBox<String> roleComboBox;

    public LoginPage() {
        setTitle("Domino's Delivery POS - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use GridBagLayout for centering the login card
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245)); // Light background
        
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(500, 500));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        // Add shadow effect (simulated with border)
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createMatteBorder(0, 0, 5, 5, new Color(200, 200, 200))
        ));

        titleLabel = new JLabel("DOMINO'S POS");
        titleLabel.setForeground(new Color(0, 100, 145)); // Domino's Blue
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 40, 400, 50);
        card.add(titleLabel);

        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameLabel.setBounds(100, 120, 300, 20);
        card.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        usernameField.setBounds(100, 145, 300, 40);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(usernameField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setBounds(100, 200, 300, 20);
        card.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setBounds(100, 225, 300, 40);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(passwordField);

        roleLabel = new JLabel("Select Role");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setBounds(100, 280, 300, 20);
        card.add(roleLabel);

        roleComboBox = new JComboBox<>(new String[] {"admin", "manager"});
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleComboBox.setBounds(100, 305, 300, 40);
        roleComboBox.setBackground(Color.WHITE);
        card.add(roleComboBox);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setBackground(new Color(0, 100, 145));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(100, 370, 300, 45);
        loginButton.setFocusPainted(false);
        card.add(loginButton);

        exitButton = new JButton("EXIT");
        exitButton.setBackground(new Color(227, 24, 55));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBounds(100, 425, 300, 30);
        exitButton.setBorderPainted(false);
        card.add(exitButton);

        // GridBagConstraints for centering the card
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);

        // add listener
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        
        ensureAdminExists();
        setVisible(true);
    }

    private void ensureAdminExists() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE role = 'admin'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Prompt user for admin credentials only if no admin exists
                String adminUsername = JOptionPane.showInputDialog(null, "Enter Admin Username:");
                String adminPassword = JOptionPane.showInputDialog(null, "Enter Admin Password:");

                if (adminUsername != null && adminPassword != null && !adminUsername.trim().isEmpty() && !adminPassword.trim().isEmpty()) {
                    String insertAdmin = "INSERT INTO users (username, password, role) VALUES (?, ?, 'admin')";
                    PreparedStatement insertPst = con.prepareStatement(insertAdmin);
                    insertPst.setString(1, adminUsername);
                    insertPst.setString(2, adminPassword);
                    insertPst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Admin account created successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Admin credentials cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            loginUser();
        } else if (e.getSource() == clearButton) {
            usernameField.setText("");
            passwordField.setText("");
            roleComboBox.setSelectedIndex(0);
			usernameField.requestFocus();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT role FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                dispose(); // Close login page
                if (role.equals("admin")) {
                    new AdminDashboard();
                } else {
                    new ManagerDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
