import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TopSellingPizzas extends JPanel {
    private JScrollPane tableScrollPane;
    private Connection cn;
    private PreparedStatement prstm;

    public TopSellingPizzas() {
        // Establish database connection
        try {
            cn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        setLayout(null);
		setBackground(new Color(135, 206, 235));
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
		exitButton.setBackground(new Color(0, 100, 145));
		exitButton.setForeground(Color.WHITE);
    }

    void updateTable() {
        try {
            // Remove the previous table if it exists
            if (tableScrollPane != null) {
                remove(tableScrollPane);
            }

            // SQL query to get top-selling pizzas
            String query = "SELECT pizza_name, SUM(quantity) AS total_sold " +
                           "FROM Order_Details GROUP BY pizza_name ORDER BY total_sold DESC";
            prstm = cn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = prstm.executeQuery();

            // Column names for the table
            String[] colHeads = {"Pizza Name", "Total Sold"};

            // Calculate the number of rows in the result set
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            // Create a 2D array to store the data from the ResultSet
            String[][] data = new String[rowCount][2];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("pizza_name");
                data[i][1] = rs.getString("total_sold");
                i++;
            }

            // Create a JTable with the data and column names
            JTable table = new JTable(data, colHeads);
            table.setEnabled(false);  // Make the table non-editable
            table.setFont(new Font("Arial", Font.PLAIN, 14));

            // Table header styling
            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Arial", Font.BOLD, 14));

            // Wrap the table in a JScrollPane
            tableScrollPane = new JScrollPane(table);
            tableScrollPane.setBounds(400, 50, 500, 300);  // Adjust the size and position
            add(tableScrollPane);

            // Refresh the panel to display the new table
            revalidate();
            repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve sales data!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create and display the JFrame
        JFrame frame = new JFrame("Top Selling Pizzas Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.add(new TopSellingPizzas());
        frame.setVisible(true);
    }
}
