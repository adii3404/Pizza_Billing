import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Delete Order Page
class DeletePizzaOrder extends JPanel implements ActionListener {
    JLabel lOrderID;
    JTextField tOrderID;
    JButton delete, back,view;
    Connection cn;
    PreparedStatement prstm;	JScrollPane tableScrollPane;

    public DeletePizzaOrder() {
        setLayout(null);
        setSize(500, 300);

        lOrderID = new JLabel("Order ID");
        tOrderID = new JTextField();
        delete = new JButton("DELETE ORDER");
        back = new JButton("BACK");
		view = new JButton("VIEW");

        add(lOrderID); add(tOrderID); add(delete); add(back); add(view);

        lOrderID.setBounds(50, 50, 100, 25);
        tOrderID.setBounds(150, 50, 170, 25);
        delete.setBounds(50, 100, 150, 30);
        back.setBounds(220, 100, 100, 30);
		view.setBounds(50,150,270,30);
		
		Font customFont = new Font("Arial", Font.BOLD, 14);
		lOrderID.setFont(customFont);	tOrderID.setFont(customFont);
		delete.setFont(customFont);		back.setFont(customFont);	view.setFont(customFont);

        delete.addActionListener(this);
        back.addActionListener(this);
		view.addActionListener(this);

        try {
            cn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delete) {
            try {
                int orderID = Integer.parseInt(tOrderID.getText());
                prstm = cn.prepareStatement("DELETE FROM orders WHERE order_id = ?");
                prstm.setInt(1, orderID);
                int rows = prstm.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Order Deleted Successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Order ID Not Found!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == back) {
            this.setVisible(false);
        }
		else if (e.getSource() == view) {
            updateTable();
        }
    }
	 void updateTable() {
    try {
        // Remove the previous table if it exists
        if (tableScrollPane != null) {
            remove(tableScrollPane);
        }

        // Modify the Statement to use TYPE_SCROLL_INSENSITIVE
        prstm = cn.prepareStatement("SELECT order_id, pizza_id, pizza_name, size, category, quantity, toppings, cold_drinks, total_price FROM orders", 
                                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        ResultSet rs = prstm.executeQuery();

        // Column names for the table
        String[] colHeads = {"Order ID", "Pizza ID", "Pizza Name", "Size", "Category", "Quantity", "Toppings", "Cold Drinks", "Total Price"};

        // Calculate the number of rows in the result set
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();

        // Create a 2D array to store the data from the ResultSet
        String[][] data = new String[rowCount][9];
        int i = 0;
        while (rs.next()) {
            data[i][0] = rs.getString("order_id");
            data[i][1] = rs.getString("pizza_id");
            data[i][2] = rs.getString("pizza_name");
            data[i][3] = rs.getString("size");
            data[i][4] = rs.getString("category");
            data[i][5] = rs.getString("quantity");
            data[i][6] = rs.getString("toppings");
            data[i][7] = rs.getString("cold_drinks");
            data[i][8] = rs.getString("total_price");
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
        tableScrollPane.setBounds(350, 20, 930, 300);  // Adjust the size and position
        add(tableScrollPane);

        // Refresh the panel to display the new table
        revalidate();
        repaint();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
	public static void main(String[] args) {
        JFrame frame = new JFrame("Pizza Order");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 700);
        frame.add(new DeletePizzaOrder());
        frame.setVisible(true);
    }
}


