import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class ManagerDashboard extends JFrame implements ActionListener {
    private PizzaOrder pizzaOrder;
    private StockPage stockPage;
    private DailySalesReport dailySalesReport;
    private TopSellingPizzas topSellingPizzas;
    private Connection connection;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private JMenuItem  loggingOut, addOrderItem, dsale, updateOrder, availstock, pizzasale;

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(245, 245, 245)); 

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("DOMINO'S MANAGER DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 100, 145));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu logout = new JMenu("Logout");
        
        loggingOut = new JMenuItem("Logout");
        addOrderItem = new JMenuItem("Order");
		dsale = new JMenuItem("Daily Sale");
		pizzasale =	new JMenuItem("Pizza Sale");
		
		availstock = new JMenuItem("Available Stock");
		

        optionsMenu.add(addOrderItem);
		optionsMenu.add(availstock);
		optionsMenu.add(dsale);
		optionsMenu.add(pizzasale);
		
        logout.add(loggingOut);

        menuBar.add(optionsMenu);
        menuBar.add(logout);
        setJMenuBar(menuBar);

        loggingOut.addActionListener(this);
        addOrderItem.addActionListener(this);
		dsale.addActionListener(this);
		pizzasale.addActionListener(this);
		availstock.addActionListener(this);
		
		 // Establish database connection
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Main Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // Add PizzaOrder Panel
        pizzaOrder = new PizzaOrder();
        contentPanel.add(pizzaOrder, "ORDER");
	
		// Add StockPage Panel
        stockPage = new StockPage(connection);
        contentPanel.add(stockPage, "STOCK");
		
		dailySalesReport = new DailySalesReport();
        contentPanel.add(dailySalesReport, "DAILY_SALE");
		
		topSellingPizzas = new TopSellingPizzas();
        contentPanel.add(topSellingPizzas, "TOP_SELLING");
        
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == loggingOut) {
            new LoginPage();
            dispose();
        } else if (e.getSource() == addOrderItem) {
            cardLayout.show(contentPanel, "ORDER");
        } else if (e.getSource() == dsale) {
			cardLayout.show(contentPanel, "DAILY_SALE");
        } else if (e.getSource() == pizzasale) {
			cardLayout.show(contentPanel, "TOP_SELLING");
        } else if (e.getSource() == availstock) {
			cardLayout.show(contentPanel, "STOCK");
        }
        revalidate();
        repaint();
    }

    public static void main(String args[]) {
        new ManagerDashboard();
    }
}
