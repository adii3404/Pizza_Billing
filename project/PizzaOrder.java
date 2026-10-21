import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PizzaOrder extends JPanel implements ActionListener{
    private JTextField tCustomerName, tTotalPrice, tCGST, tSGST, tDiscount, tUPIId;
    private JLabel lUPIId, qrLabel;
    private JComboBox<String> cbPaymentMode;
    private JPanel toppingsPanel, drinksPanel, selectedPizzasPanel;
    private JButton btnAddOrder,clr,ext,GenerateBill;
    private JList<String> pizzaList;
    private DefaultListModel<String> pizzaListModel;
    private HashMap<String, String> pizzaCategories = new HashMap<>();
    private ArrayList<PizzaSelection> selectedPizzas = new ArrayList<>();
    private final String[] toppings = {"Chilli", "Onion", "Tomato", "Cheese"};
    private final String[] drinks = {"Coca Cola", "Sprite"};
    private final ArrayList<JCheckBox> toppingCheckboxes = new ArrayList<>();
    private final ArrayList<JCheckBox> drinkCheckboxes = new ArrayList<>();
    private static final double TOPPING_PRICE = 40.0;
    private static final double DRINK_PRICE = 50.0;
    private static final double TAX_RATE = 0.025;
	

    public PizzaOrder() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // LEFT PANEL: Selection Area
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lHeader = new JLabel("SELECT PIZZA & TOPPINGS");
        lHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lHeader.setForeground(new Color(0, 100, 145));
        lHeader.setBounds(20, 20, 300, 30);
        leftPanel.add(lHeader);

        // Customer Name
        JLabel lCustomerName = new JLabel("Customer Name:");
        lCustomerName.setBounds(20, 70, 120, 25);
        leftPanel.add(lCustomerName);
        tCustomerName = new JTextField();
        tCustomerName.setBounds(140, 70, 300, 30);
        tCustomerName.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        leftPanel.add(tCustomerName);

        // Pizza Grid Dashboard
        JLabel lPizzas = new JLabel("PIZZA MENU");
        lPizzas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lPizzas.setBounds(20, 110, 200, 25);
        leftPanel.add(lPizzas);

        JPanel pizzaGridContainer = new JPanel(new GridLayout(0, 3, 15, 15));
        pizzaGridContainer.setBackground(Color.WHITE);
        pizzaGridContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane pizzaScrollPane = new JScrollPane(pizzaGridContainer);
        pizzaScrollPane.setBounds(20, 140, 500, 310);
        pizzaScrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
        leftPanel.add(pizzaScrollPane);

        loadPizzaCards(pizzaGridContainer);

        // Toppings
        JLabel lToppings = new JLabel("Select Toppings:");
        lToppings.setBounds(540, 110, 150, 25);
        leftPanel.add(lToppings);
        toppingsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        toppingsPanel.setBackground(Color.WHITE);
        JScrollPane toppingsScrollPane = new JScrollPane(toppingsPanel);
        toppingsScrollPane.setBounds(540, 140, 240, 120);
        leftPanel.add(toppingsScrollPane);
        loadToppings();

        // Drinks
        JLabel lDrinks = new JLabel("Select Drinks:");
        lDrinks.setBounds(540, 270, 150, 25);
        leftPanel.add(lDrinks);
        drinksPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        drinksPanel.setBackground(Color.WHITE);
        JScrollPane drinksScrollPane = new JScrollPane(drinksPanel);
        drinksScrollPane.setBounds(540, 300, 240, 90);
        leftPanel.add(drinksScrollPane);
        loadDrinks();

        // Selected Items List
        selectedPizzasPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        selectedPizzasPanel.setBackground(new Color(250, 250, 250));
        JScrollPane selectedScrollPane = new JScrollPane(selectedPizzasPanel);
        selectedScrollPane.setBounds(20, 460, 760, 180);
        selectedScrollPane.setBorder(BorderFactory.createTitledBorder("Orders Prepared for Billing"));
        leftPanel.add(selectedScrollPane);

        // RIGHT PANEL: Billing Sidebar with Margin
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setOpaque(false);
        sidebarWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // 20px right margin

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(new Color(0, 75, 110)); 
        rightPanel.setPreferredSize(new Dimension(300, 700));

        JLabel lBillHeader = new JLabel("BILLING");
        lBillHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lBillHeader.setForeground(Color.WHITE);
        lBillHeader.setBounds(20, 30, 260, 30);
        rightPanel.add(lBillHeader);

        // Totals
        JLabel lTotalPrice = new JLabel("Subtotal:");
        lTotalPrice.setForeground(new Color(220, 220, 220));
        lTotalPrice.setBounds(20, 80, 100, 25);
        rightPanel.add(lTotalPrice);
        tTotalPrice = new JTextField();
        tTotalPrice.setBounds(120, 80, 160, 35);
        tTotalPrice.setBackground(new Color(0, 60, 90));
        tTotalPrice.setForeground(Color.WHITE);
        tTotalPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tTotalPrice.setEditable(false);
        rightPanel.add(tTotalPrice);

        JLabel lCGST = new JLabel("CGST (2.5%):");
        lCGST.setForeground(new Color(220, 220, 220));
        lCGST.setBounds(20, 125, 100, 25);
        rightPanel.add(lCGST);
        tCGST = new JTextField();
        tCGST.setBounds(120, 125, 160, 30);
        tCGST.setBackground(new Color(0, 60, 90));
        tCGST.setForeground(Color.WHITE);
        tCGST.setEditable(false);
        rightPanel.add(tCGST);

        JLabel lSGST = new JLabel("SGST (2.5%):");
        lSGST.setForeground(new Color(220, 220, 220));
        lSGST.setBounds(20, 165, 100, 25);
        rightPanel.add(lSGST);
        tSGST = new JTextField();
        tSGST.setBounds(120, 165, 160, 30);
        tSGST.setBackground(new Color(0, 60, 90));
        tSGST.setForeground(Color.WHITE);
        tSGST.setEditable(false);
        rightPanel.add(tSGST);

        JLabel lPaymentMode = new JLabel("Payment:");
        lPaymentMode.setForeground(Color.WHITE);
        lPaymentMode.setBounds(20, 210, 100, 25);
        rightPanel.add(lPaymentMode);
        cbPaymentMode = new JComboBox<>(new String[]{"Cash", "Online", "UPI"});
        cbPaymentMode.setBounds(120, 210, 160, 30);
        cbPaymentMode.addActionListener(e -> toggleUPIField());
        rightPanel.add(cbPaymentMode);

        // UPI ID Field (Hidden by default)
        lUPIId = new JLabel("UPI ID:");
        lUPIId.setForeground(Color.WHITE);
        lUPIId.setBounds(20, 255, 100, 25);
        lUPIId.setVisible(false);
        rightPanel.add(lUPIId);
        tUPIId = new JTextField();
        tUPIId.setBounds(120, 255, 160, 30);
        tUPIId.setVisible(false);
        rightPanel.add(tUPIId);

        // QR Code Label (Hidden by default)
        qrLabel = new JLabel();
        qrLabel.setBounds(75, 430, 150, 150);
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon("upi_qr.png");
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            qrLabel.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            qrLabel.setText("QR Code Error");
        }
        qrLabel.setVisible(false);
        rightPanel.add(qrLabel);

        JLabel lDiscount = new JLabel("Disc %:");
        lDiscount.setForeground(Color.WHITE);
        lDiscount.setBounds(20, 295, 100, 25);
        rightPanel.add(lDiscount);
        tDiscount = new JTextField("0");
        tDiscount.setBounds(120, 295, 160, 30);
        rightPanel.add(tDiscount);

        // Action Buttons
        btnAddOrder = new JButton("FINALIZE ORDER");
        btnAddOrder.setBounds(20, 340, 260, 50);
        btnAddOrder.setBackground(new Color(227, 24, 55));
        btnAddOrder.setForeground(Color.WHITE);
        btnAddOrder.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAddOrder.addActionListener(e -> calculateTotalPrice());
        rightPanel.add(btnAddOrder);

        GenerateBill = new JButton("GENERATE PDF");
        GenerateBill.setBounds(20, 400, 260, 40);
        GenerateBill.setBackground(new Color(34, 139, 34));
        GenerateBill.setForeground(Color.WHITE);
        GenerateBill.addActionListener(this);
        rightPanel.add(GenerateBill);

        clr = new JButton("CLEAR ALL");
        clr.setBounds(20, 600, 125, 30);
        clr.addActionListener(this);
        rightPanel.add(clr);

        ext = new JButton("CLOSE POS");
        ext.setBounds(155, 600, 125, 30);
        ext.addActionListener(this);
        rightPanel.add(ext);

        sidebarWrapper.add(rightPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);
        add(sidebarWrapper, BorderLayout.EAST);
    }
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==clr)
		{
			clearAll();
		}
		else if(e.getSource()==ext)
		{
			this.setVisible(false);
		}
		else if(e.getSource()==GenerateBill)
		{
			new GenerateBill();
		}
	}

    private void toggleUPIField() {
        boolean isUPI = "UPI".equals(cbPaymentMode.getSelectedItem());
        lUPIId.setVisible(isUPI);
        tUPIId.setVisible(isUPI);
        qrLabel.setVisible(isUPI);
        revalidate();
        repaint();
    }
    private void loadPizzaCards(JPanel container) {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, category, image_path, small_price FROM Pizzas")) {

            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("small_price");

                pizzaCategories.put(name, category);
                
                JPanel card = createPizzaCard(name, category, imagePath, price);
                container.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createPizzaCard(String name, String category, String imagePath, double price) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Image
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            if (icon.getIconWidth() == -1) icon = new ImageIcon("default_pizza.png"); // fallback
            Image img = icon.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imgLabel.setText("No Image");
        }
        card.add(imgLabel, BorderLayout.CENTER);

        // Info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel catLabel = new JLabel(category);
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catLabel.setForeground(Color.GRAY);
        JLabel priceLabel = new JLabel("Starts ₹" + price);
        priceLabel.setForeground(new Color(0, 100, 145));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        infoPanel.add(nameLabel);
        infoPanel.add(catLabel);
        infoPanel.add(priceLabel);
        card.add(infoPanel, BorderLayout.SOUTH);

        // Click Logic
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addSinglePizzaToOrder(name, category);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 100, 145), 2),
                    BorderFactory.createEmptyBorder(9, 9, 9, 9)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        return card;
    }

    private void addSinglePizzaToOrder(String pizzaName, String category) {
        PizzaSelection selection = new PizzaSelection(pizzaName, category);
        selectedPizzas.add(selection);
        selectedPizzasPanel.add(selection);
        selectedPizzasPanel.revalidate();
        selectedPizzasPanel.repaint();
    }

    private void loadToppings() {
        for (String topping : toppings) {
            JCheckBox cb = new JCheckBox(topping);
            toppingCheckboxes.add(cb);
            toppingsPanel.add(cb);
        }
    }

    private void loadDrinks() {
        for (String drink : drinks) {
            JCheckBox cb = new JCheckBox(drink);
            drinkCheckboxes.add(cb);
            drinksPanel.add(cb);
        }
    }

	
	private int getPizzaId(Connection con, String pizzaName, String category) throws SQLException {
		String query = "SELECT pizza_id FROM Pizzas WHERE name = ? AND category = ?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, pizzaName);
			pst.setString(2, category);
			ResultSet rs = pst.executeQuery();
			return rs.next() ? rs.getInt("pizza_id") : -1;
		}
	}
	private int getAvailableStock(Connection con, String pizzaName, String category) throws SQLException {
    String query = "SELECT quantity FROM Pizzas WHERE name = ? AND category = ?";
    try (PreparedStatement pst = con.prepareStatement(query)) {
        pst.setString(1, pizzaName);
        pst.setString(2, category);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt("quantity") : 0; // Return available stock
    }
}


	private String getSelectedToppings() {
		StringBuilder toppings = new StringBuilder();
		for (JCheckBox cb : toppingCheckboxes) {
			if (cb.isSelected()) toppings.append(cb.getText()).append(", ");
		}
		return toppings.length() > 0 ? toppings.substring(0, toppings.length() - 2) : "";
	}

	private String getSelectedDrinks() {
		StringBuilder drinks = new StringBuilder();
		for (JCheckBox cb : drinkCheckboxes) {
			if (cb.isSelected()) drinks.append(cb.getText()).append(", ");
		}
		return drinks.length() > 0 ? drinks.substring(0, drinks.length() - 2) : "";
	}


	private void calculateTotalPrice() {
    if (tCustomerName.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Customer name cannot be empty!");
        return;
    }
    
    // Check if any pizzas are selected
    if (selectedPizzas.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select at least one pizza!");
        return;
    }

    String customerName = tCustomerName.getText().trim();
    double totalPrice = 0;
    int orderId = -1;

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizzacafe", "root", "root")) {
        
        // Calculate the total price for the selected pizzas
        for (PizzaSelection selection : selectedPizzas) {
            String size = selection.cbSize.getSelectedItem().toString();
            String pizzaName = selection.pizzaName;
            String category = selection.category;
            int quantity = Integer.parseInt(selection.tQuantity.getText().trim());
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid quantity for " + selection.pizzaName + ". Must be positive!");
                return;
            }
			
			int availableStock = getAvailableStock(con, pizzaName, category);

            if (availableStock < quantity) {
                JOptionPane.showMessageDialog(this, "Sorry! " + pizzaName + " is out of stock or insufficient stock available.");
                return;
            }

			
            double pizzaPrice = getPizzaPrice(con, pizzaName, category, size);
            totalPrice += pizzaPrice * quantity;
        }

        // Add toppings & drinks price
        long selectedToppingsCount = toppingCheckboxes.stream().filter(JCheckBox::isSelected).count();
        totalPrice += selectedToppingsCount * TOPPING_PRICE;
        long selectedDrinksCount = drinkCheckboxes.stream().filter(JCheckBox::isSelected).count();
        totalPrice += selectedDrinksCount * DRINK_PRICE;

        // Apply Discount
        double discountPercent = 0;
        try {
            discountPercent = Double.parseDouble(tDiscount.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount value!");
            return;
        }
        totalPrice -= (totalPrice * (discountPercent / 100));

        // Calculate taxes (CGST & SGST)
        double cgst = totalPrice * TAX_RATE;
        double sgst = totalPrice * TAX_RATE;
        totalPrice += (cgst + sgst);

        // Insert Order into Orders Table
        String paymentMode = cbPaymentMode.getSelectedItem().toString();
        String insertOrderQuery = "INSERT INTO Orders (cust_name, total_price, payment_mode, order_date) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement orderPst = con.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
            orderPst.setString(1, customerName);
            orderPst.setDouble(2, totalPrice); 
            orderPst.setString(3, paymentMode);
            orderPst.executeUpdate();

            ResultSet rs = orderPst.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
        }

        if (orderId == -1) {
            JOptionPane.showMessageDialog(this, "Failed to create order. Please try again.");
            return;
        }

        // Insert Order Details into Order_Details Table
        String insertDetailsQuery = "INSERT INTO Order_Details (order_id, pizza_id, pizza_name, size, category, quantity, toppings, cold_drinks, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement detailsPst = con.prepareStatement(insertDetailsQuery)) {
            for (PizzaSelection selection : selectedPizzas) {
                String size = selection.cbSize.getSelectedItem().toString();
                String pizzaName = selection.pizzaName;
                String category = selection.category;
                int quantity = Integer.parseInt(selection.tQuantity.getText().trim());

                int pizzaId = getPizzaId(con, pizzaName, category);
                double pizzaPrice = getPizzaPrice(con, pizzaName, category, size);
                double itemTotal = pizzaPrice * quantity;

                // Get selected toppings and drinks
                String selectedToppings = getSelectedToppings();
                String selectedDrinks = getSelectedDrinks();

                // Set values for each pizza item in the order details
                detailsPst.setInt(1, orderId);
                detailsPst.setInt(2, pizzaId);
                detailsPst.setString(3, pizzaName);
                detailsPst.setString(4, size);
                detailsPst.setString(5, category);
                detailsPst.setInt(6, quantity);
                detailsPst.setString(7, selectedToppings);
                detailsPst.setString(8, selectedDrinks);
                detailsPst.setDouble(9, itemTotal);
                detailsPst.addBatch();
            }
            detailsPst.executeBatch();
        }

        // Confirm Order and Update UI
        JOptionPane.showMessageDialog(this, "Order placed successfully! Order ID: " + orderId);
        tTotalPrice.setText(String.format("%.2f", totalPrice));
        tCGST.setText(String.format("%.2f", cgst));
        tSGST.setText(String.format("%.2f", sgst));

        // Update Pizza Stock After Placing Order
        String updateStockSQL = "UPDATE Pizzas SET quantity = quantity - ? WHERE pizza_id = ?";
        try (PreparedStatement updateStockStmt = con.prepareStatement(updateStockSQL)) {
            for (PizzaSelection selection : selectedPizzas) {
                String pizzaName = selection.pizzaName;
                String category = selection.category;
                int quantity = Integer.parseInt(selection.tQuantity.getText().trim());

                int pizzaId = getPizzaId(con, pizzaName, category);
                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setInt(2, pizzaId);
                updateStockStmt.addBatch();
            }
            updateStockStmt.executeBatch();
        }
        
        // AUTOMATICALLY OPEN BILLING SECTION (Receipt)
        new GenerateBill();
        
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
    }
}


	private void clearAll() {
		// Clear customer name field
		tCustomerName.setText("");

		// Clear selected pizzas
		selectedPizzas.clear();
		selectedPizzasPanel.removeAll();
		selectedPizzasPanel.revalidate();
		selectedPizzasPanel.repaint();

		// Clear toppings selections
		for (JCheckBox cb : toppingCheckboxes) {
			cb.setSelected(false);
		}

		// Clear drinks selections
		for (JCheckBox cb : drinkCheckboxes) {
			cb.setSelected(false);
		}

		// Reset total price fields
		tTotalPrice.setText("");
		tCGST.setText("");
		tSGST.setText("");
	}


	
	private double getPizzaPrice(Connection con, String pizzaName, String category, String size) {
    double price = 0.0;
    String column = "";

    // Determine the correct column based on the selected size
    switch (size) {
        case "Small":
            column = "small_price";
            break;
        case "Medium":
            column = "medium_price";
            break;
        case "Large":
            column = "large_price";
            break;
        case "XL":
            column = "extra_large_price";
            break;
        default:
            return 0.0; // Invalid size
    }

		// Query to dynamically fetch price based on the size column
		String query = "SELECT " + column + " FROM Pizzas WHERE name = ? AND category = ?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, pizzaName);
			pst.setString(2, category);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				price = rs.getDouble(column); // Fetch the correct price column
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}
	class PizzaSelection extends JPanel {
        JComboBox<String> cbSize;
        JTextField tQuantity;
        JLabel lItemPrice;
        String pizzaName, category;

        public PizzaSelection(String pizzaName, String category) {
            this.pizzaName = pizzaName;
            this.category = category;
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
            setBackground(new Color(250, 250, 250));

            JLabel nameLbl = new JLabel(pizzaName + " (" + category + ")");
            nameLbl.setPreferredSize(new Dimension(250, 25));
            add(nameLbl);

            cbSize = new JComboBox<>(new String[]{"Small", "Medium", "Large", "XL"});
            cbSize.addActionListener(e -> updatePrice());
            add(cbSize);

            tQuantity = new JTextField("1", 3);
            add(new JLabel("Qty:"));
            add(tQuantity);

            lItemPrice = new JLabel("Price: ₹0.00");
            lItemPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lItemPrice.setForeground(new Color(0, 100, 145));
            add(lItemPrice);

            updatePrice();
        }

        private void updatePrice() {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizzacafe", "root", "root")) {
                double price = getPizzaPrice(con, pizzaName, category, cbSize.getSelectedItem().toString());
                lItemPrice.setText("Price: ₹" + String.format("%.2f", price));
            } catch (SQLException ex) {
                lItemPrice.setText("Price Error");
            }
        }
    }

     public static void main(String[] args) {
        JFrame frame = new JFrame("Pizza Order");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
		frame.add(new PizzaOrder());
        frame.setVisible(true);
    }
	
	
}
