import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class AddManagerPanel extends JPanel implements ActionListener {
    private JTextField managerUsername, managerIdField;
    private JPasswordField managerPassword;
    private JButton addManagerButton, clearButton, deleteManagerButton, exitButton,viewManagerButton;

    public AddManagerPanel() {
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("Manage Managers"));
		setBackground(new Color(135, 206, 235));
		

        // Labels
        JLabel usernameLabel = new JLabel("Username:");
        managerUsername = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        managerPassword = new JPasswordField(15);
        JLabel deleteLabel = new JLabel("Delete Manager ID:");
        managerIdField = new JTextField(15);

        // Buttons
        addManagerButton = new JButton("Submit");
        clearButton = new JButton("Clear");
        deleteManagerButton = new JButton("Delete");
        exitButton = new JButton("Exit");
		viewManagerButton = new JButton("View Managers");

        // Set Fonts
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        managerUsername.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        managerPassword.setFont(new Font("Arial", Font.PLAIN, 18));
        deleteLabel.setFont(new Font("Arial", Font.BOLD, 18));
        managerIdField.setFont(new Font("Arial", Font.PLAIN, 18));
        addManagerButton.setFont(new Font("Arial", Font.BOLD, 18));
        clearButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteManagerButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
		viewManagerButton.setFont(new Font("Arial", Font.BOLD, 18));
		
		addManagerButton.setBackground(new Color(0, 100, 145)); 
        addManagerButton.setForeground(Color.WHITE);
		clearButton.setBackground(new Color(0, 100, 145)); 
        clearButton.setForeground(Color.WHITE);
		deleteManagerButton.setBackground(new Color(0, 100, 145)); 
        deleteManagerButton.setForeground(Color.WHITE);
		addManagerButton.setBackground(new Color(0, 100, 145)); 
        addManagerButton.setForeground(Color.WHITE);
		exitButton.setBackground(new Color(0, 100, 145)); 
        exitButton.setForeground(Color.WHITE);
		viewManagerButton.setBackground(new Color(0, 100, 145)); 
        viewManagerButton.setForeground(Color.WHITE);
		
		// Add components
        add(usernameLabel);
        add(managerUsername);
        add(passwordLabel);
        add(managerPassword);
        add(addManagerButton);
        add(clearButton);
        add(deleteLabel);
        add(managerIdField);
        add(deleteManagerButton);
        add(exitButton);
		add(viewManagerButton);

		// Set Bounds
		usernameLabel.setBounds(50, 30, 200, 30);
		managerUsername.setBounds(250, 30, 200, 30);
		passwordLabel.setBounds(50, 80, 200, 30);
		managerPassword.setBounds(250, 80, 200, 30);
		addManagerButton.setBounds(50, 130, 190, 40); 
		clearButton.setBounds(250, 130, 200, 40); 
		deleteLabel.setBounds(50, 190, 200, 30);
		managerIdField.setBounds(250, 190, 200, 30);
		deleteManagerButton.setBounds(50, 240, 190, 40);
		exitButton.setBounds(250, 240, 200, 40); 
		viewManagerButton.setBounds(50, 290, 400, 40);

        // Add action listeners
        addManagerButton.addActionListener(this);
        clearButton.addActionListener(this);
        deleteManagerButton.addActionListener(this);
        exitButton.addActionListener(this);
		viewManagerButton.addActionListener(this);
		

        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addManagerButton) {
            addManager();
        } else if (e.getSource() == clearButton) {
            managerUsername.setText("");
            managerPassword.setText("");
            managerIdField.setText("");
			managerUsername.requestFocus();
        } else if (e.getSource() == deleteManagerButton) {
            deleteManager();
        } else if (e.getSource() == exitButton) {
            this.setVisible(false); // Disable the panel
        }
		else if (e.getSource() == viewManagerButton) {
            viewManager();
		}
    }

    private void addManager() {
        String username = managerUsername.getText();
        String password = new String(managerPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            // Check if username exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ? AND role = 'manager'";
            PreparedStatement checkPst = con.prepareStatement(checkQuery);
            checkPst.setString(1, username);
            ResultSet rs = checkPst.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Manager with this username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                con.close();
                return;
            }

            // Insert new manager
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'manager')";
            PreparedStatement pst = con.prepareStatement(insertQuery);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Manager Added Successfully!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteManager() {
        String managerId = managerIdField.getText();

        if (managerId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Manager ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String deleteQuery = "DELETE FROM users WHERE id = ? AND role = 'manager'";
            PreparedStatement pst = con.prepareStatement(deleteQuery);
            pst.setInt(1, Integer.parseInt(managerId));

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Manager Deleted Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Manager ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	private void viewManager() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizzacafe", "root", "root");
        String query = "SELECT id, username FROM users WHERE role = 'manager'";  // Query to get managers
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        StringBuilder managers = new StringBuilder("Managers List:\n\n");

        // Check if any manager is found and append to the StringBuilder
        boolean foundManager = false;
        while (rs.next()) {
            foundManager = true;
            managers.append("ID: ").append(rs.getInt("id"))
                    .append(" | Username: ").append(rs.getString("username"))
                    .append("\n");
        }

        // If no managers are found, show a message
        if (!foundManager) {
            JOptionPane.showMessageDialog(this, "No managers found.", "View Managers", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, managers.toString(), "View Managers", JOptionPane.INFORMATION_MESSAGE);
        }

        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
