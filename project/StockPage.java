import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StockPage extends JPanel {
    private JScrollPane tableScrollPane;
    private Connection cn;
    private PreparedStatement prstm;

    public StockPage(Connection connection) {
        this.cn = connection;
        setLayout(null);
		setBackground(new Color(135, 206, 235));
        updateTable();

        // Exit button inside StockPage constructor
		JButton exitButton = new JButton("Exit");
		exitButton.setBounds(600, 370, 100, 30);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StockPage.this.setVisible(false); // Fix: Referencing the enclosing class
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

            // Prepare statement with TYPE_SCROLL_INSENSITIVE
            prstm = cn.prepareStatement("SELECT pizza_id, name, category, small_price, medium_price, large_price, extra_large_price, quantity FROM Pizzas", 
                                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = prstm.executeQuery();

            // Column names for the table
            String[] colHeads = {"Pizza ID", "Name", "Category", "Small Price", "Medium Price", "Large Price", "Extra Large Price", "Quantity"};

            // Calculate the number of rows in the result set
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            // Create a 2D array to store the data from the ResultSet
            String[][] data = new String[rowCount][8];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("pizza_id");
                data[i][1] = rs.getString("name");
                data[i][2] = rs.getString("category");
                data[i][3] = rs.getString("small_price");
                data[i][4] = rs.getString("medium_price");
                data[i][5] = rs.getString("large_price");
                data[i][6] = rs.getString("extra_large_price");
                data[i][7] = rs.getString("quantity");
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
            tableScrollPane.setBounds(200, 50, 900, 300);  // Adjust the size and position
            add(tableScrollPane);

            // Refresh the panel to display the new table
            revalidate();
            repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Establish database connection
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create and display the JFrame
        JFrame frame = new JFrame("Available Stock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.add(new StockPage(connection));
        frame.setVisible(true);

    }
}
