import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DailySalesReport extends JPanel {
    private JScrollPane tableScrollPane;
    private Connection cn;
    private PreparedStatement prstm;

    public DailySalesReport() {
        // Establish database connection
        try {
            cn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setLayout(null);
		setBackground(new Color(240, 240, 240)); // Professional Light Gray
        updateTable();

        // Add Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(600, 370, 100, 30);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(exitButton);
		exitButton.setBackground(new Color(227, 24, 55)); // Domino's Red
		exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
    }

    void updateTable() {
        try {
            // Remove the previous table if it exists
            if (tableScrollPane != null) {
                remove(tableScrollPane);
            }

            String query = "SELECT DATE(order_date) AS order_day, payment_mode, SUM(total_price) AS total_earnings FROM Orders GROUP BY order_day, payment_mode ORDER BY order_day DESC";
            prstm = cn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = prstm.executeQuery();

            // Column names for the table
            String[] colHeads = {"Order Date", "Payment Mode", "Total Earnings"};

            // Calculate the number of rows in the result set
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            // Create a 2D array to store the data from the ResultSet
            String[][] data = new String[rowCount][3];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("order_day");
                data[i][1] = rs.getString("payment_mode");
                data[i][2] = String.format("₹%.2f", rs.getDouble("total_earnings"));
                i++;
            }

            // Create a JTable with the data and column names
            JTable table = new JTable(data, colHeads);
            table.setEnabled(false);  // Make the table non-editable
            Font headerFont = new Font("Arial", Font.BOLD, 14);
            JTableHeader header = table.getTableHeader();
            header.setFont(headerFont);

            // Wrap the table in a JScrollPane
            tableScrollPane = new JScrollPane(table);
            tableScrollPane.setBounds(400, 50, 500, 300);  // Adjust the size and position
            add(tableScrollPane);

            // Refresh the panel to display the new table
            revalidate();
            repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create and display the JFrame
        JFrame frame = new JFrame("Daily Sales Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.add(new DailySalesReport());
        frame.setVisible(true);
    }
}
