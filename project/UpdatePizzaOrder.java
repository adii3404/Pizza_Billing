import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdatePizzaOrder extends JPanel implements ActionListener {
    JLabel lorderID, lpizzaID, lname, lcategory, lsize, lquantity, ltoppings, lcoldDrinks, ltotalPrice, lcgst, lsgst, lcess;
    JTextField torderID, tpizzaID, tname, tquantity, ttotalPrice, tcgst, tsgst, tcess;
    JComboBox<String> cbCategory, cbSize;
    JCheckBox cbChilli, cbCheese, cbTomato, cbOnion, cbCocaCola, cbSpirit;
    JButton fetch, update, clr, view, back;
    Connection cn;
    PreparedStatement prstm;
    JScrollPane tableScrollPane;

    public UpdatePizzaOrder() {
        setLayout(null);
        setSize(1500, 700);

        // Labels
        lorderID = new JLabel("Order ID:");
        lpizzaID = new JLabel("Pizza ID:");
        lname = new JLabel("Name:");
        lcategory = new JLabel("Category:");
        lsize = new JLabel("Size:");
        lquantity = new JLabel("Quantity:");
        ltoppings = new JLabel("Toppings:");
        lcoldDrinks = new JLabel("Cold Drinks:");
        ltotalPrice = new JLabel("Total Price (Incl. Taxes):");
        lcgst = new JLabel("CGST (2.5%):");
        lsgst = new JLabel("SGST (2.5%):");
        lcess = new JLabel("Cess (12%):");

        // Text Fields
        torderID = new JTextField();
        tpizzaID = new JTextField();
        tname = new JTextField();
        tquantity = new JTextField();
        ttotalPrice = new JTextField();
        tcgst = new JTextField();
        tsgst = new JTextField();
        tcess = new JTextField();
        
        tname.setEditable(false);
        ttotalPrice.setEditable(false);
        tcgst.setEditable(false);
        tsgst.setEditable(false);
        tcess.setEditable(false);

        // Dropdowns
        cbCategory = new JComboBox<>();
        cbSize = new JComboBox<>(new String[]{"Small", "Medium", "Large", "XL"});

        // Checkboxes
        cbChilli = new JCheckBox("Chilli (+40)");
        cbCheese = new JCheckBox("Cheese (+40)");
        cbTomato = new JCheckBox("Tomato (+40)");
        cbOnion = new JCheckBox("Onion (+40)");
        cbCocaCola = new JCheckBox("Coca Cola (+50)");
        cbSpirit = new JCheckBox("Spirit (+50)");

        // Buttons
        fetch = new JButton("FETCH ORDER");
        update = new JButton("UPDATE ORDER");
        clr = new JButton("CLEAR");
        view = new JButton("VIEW");
        back = new JButton("BACK");

        // Add Components
        add(lorderID); add(torderID); add(fetch);
        add(lpizzaID); add(tpizzaID); add(lcategory); add(cbCategory); add(lname);
        add(tname); add(lsize); add(cbSize); add(lquantity); add(tquantity);
        add(ltoppings); add(cbChilli); add(cbCheese); add(cbTomato); add(cbOnion);
        add(lcoldDrinks); add(cbCocaCola); add(cbSpirit);
        add(lcgst); add(tcgst); add(lsgst); add(tsgst); add(lcess); add(tcess);
        add(ltotalPrice); add(ttotalPrice); add(update); add(clr); add(view); add(back);

        // Set Bounds
        lorderID.setBounds(20, 20, 120, 25);
        torderID.setBounds(150, 20, 180, 25);
        fetch.setBounds(340, 20, 150, 25);
        
        lpizzaID.setBounds(20, 55, 120, 25);
        tpizzaID.setBounds(150, 55, 180, 25);
        lname.setBounds(20, 90, 120, 25);
        tname.setBounds(150, 90, 180, 25);
        
        lquantity.setBounds(20, 125, 120, 25);
        tquantity.setBounds(150, 125, 180, 25);
        
        update.setBounds(20, 470, 150, 25);
        clr.setBounds(180, 470, 150, 25);
        view.setBounds(20, 500, 150, 25);
        back.setBounds(180, 500, 150, 25);

        // Database Connection
        try {
           cn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Action Listeners
        fetch.addActionListener(this);
        update.addActionListener(this);
        clr.addActionListener(this);
        view.addActionListener(this);
        back.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fetch) {
            fetchOrderDetails();
        } else if (e.getSource() == update) {
            updateOrder();
        } else if (e.getSource() == view) {
            updateTable();
        } else if (e.getSource() == back) {
            this.setVisible(false);
        } else if (e.getSource() == clr) {
            clearAll();
        }
    }

    private void fetchOrderDetails() {
    try {
        String orderIdText = torderID.getText().trim();
        if (orderIdText.isEmpty()) {
            return; // Stop execution if no Order ID is entered
        }

        int orderId = Integer.parseInt(orderIdText);
        prstm = cn.prepareStatement("SELECT pizza_id, pizza_name, size, category, quantity, toppings, cold_drinks FROM orders WHERE order_id = ?");
        prstm.setInt(1, orderId);
        ResultSet rs = prstm.executeQuery();

        if (rs.next()) {
            tpizzaID.setText(rs.getString("pizza_id"));
            tname.setText(rs.getString("pizza_name"));
            cbSize.setSelectedItem(rs.getString("size"));
            cbCategory.setSelectedItem(rs.getString("category"));
            tquantity.setText(rs.getString("quantity"));

            // Set toppings checkboxes
            String toppings = rs.getString("toppings");
            cbChilli.setSelected(toppings.contains("Chilli"));
            cbCheese.setSelected(toppings.contains("Cheese"));
            cbTomato.setSelected(toppings.contains("Tomato"));
            cbOnion.setSelected(toppings.contains("Onion"));

            // Set cold drinks checkboxes
            String drinks = rs.getString("cold_drinks");
            cbCocaCola.setSelected(drinks.contains("Coca Cola"));
            cbSpirit.setSelected(drinks.contains("Spirit"));
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Order ID", "Error", JOptionPane.ERROR_MESSAGE);
            clearAll();
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Please enter a valid Order ID", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private void updateTable() {
        // Update the order details in database
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

    private void updateOrder() {
        // Refresh the order table
    }

    private void clearAll() {
        torderID.setText("");
        tpizzaID.setText("");
        tname.setText("");
        tquantity.setText("");
        ttotalPrice.setText("");
        tcgst.setText("");
        tsgst.setText("");
        tcess.setText("");
    }
}
