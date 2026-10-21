import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class AdminDashboard extends JFrame implements ActionListener {
    private AddManagerPanel addManagerPanel;
    private product product;
    private PizzaOrder pizzaOrder; // Declare pizzaOrder as an instance variable
    private DeletePizzaOrder deletePizzaOrder;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private StockPage stockPage;
    private DailySalesReport dailySalesReport;
    private MonthlySalesReport monthlySalesReport;
    private TopSellingPizzas topSellingPizzas;
    private Connection connection;
    
	private JMenuItem addManagerItem, addProductItem, loggingOut, addOrderItem, availstock, dsale, msale, pizzasale;
	
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(245, 245, 245));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("DOMINO'S ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 100, 145));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
		JMenu Report = new JMenu("reports");
        JMenu logout = new JMenu("Logout");
        addManagerItem = new JMenuItem("Add Manager");
        addProductItem = new JMenuItem("Add Product");
        loggingOut = new JMenuItem("Logout");
        addOrderItem = new JMenuItem("Order");
		
		
		availstock = new JMenuItem("Available Stock");
		dsale = new JMenuItem("Daily sale");
		msale = new JMenuItem("Monthly sale");
		pizzasale = new JMenuItem("pizza sold");

        optionsMenu.add(addManagerItem);
        optionsMenu.add(addProductItem);
        optionsMenu.add(addOrderItem);
		
		
		Report.add(availstock);
		
		Report.add(dsale);
		Report.add(msale);
		Report.add(pizzasale);
		
        logout.add(loggingOut);

        menuBar.add(optionsMenu);
		menuBar.add(Report);
        menuBar.add(logout);
        setJMenuBar(menuBar);

        loggingOut.addActionListener(this);
        addManagerItem.addActionListener(this);
        addProductItem.addActionListener(this);
        addOrderItem.addActionListener(this);
		
		
		availstock.addActionListener(this);
		dsale.addActionListener(this);
		msale.addActionListener(this);
		pizzasale.addActionListener(this);
		
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

        // Add Manager Panel
        addManagerPanel = new AddManagerPanel();
        contentPanel.add(addManagerPanel, "MANAGER");

        // Add Product Panel
        product = new product();
        contentPanel.add(product, "PRODUCT");
        
        // Add PizzaOrder Panel
        pizzaOrder = new PizzaOrder();
        contentPanel.add(pizzaOrder, "ORDER");
		
		// Add DeletePizzaOrder Panel
        deletePizzaOrder = new DeletePizzaOrder();
        contentPanel.add(deletePizzaOrder, "DELETE_ORDER");
		
		// Add StockPage Panel
        stockPage = new StockPage(connection);
        contentPanel.add(stockPage, "STOCK");
		
		// Add DailySalesReport Panel
        dailySalesReport = new DailySalesReport();
        contentPanel.add(dailySalesReport, "DAILY_SALE");
		
		// Add MonthlySalesReport Panel
        monthlySalesReport = new MonthlySalesReport();
        contentPanel.add(monthlySalesReport, "MONTHLY_SALE");
		
		// Add TopSellingPizzas Panel
        topSellingPizzas = new TopSellingPizzas();
        contentPanel.add(topSellingPizzas, "TOP_SELLING");
		
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addManagerItem) {
            cardLayout.show(contentPanel, "MANAGER");
        } else if (e.getSource() == addProductItem) {
            cardLayout.show(contentPanel, "PRODUCT");
        } else if (e.getSource() == loggingOut) {
            new LoginPage();
            dispose();
        } else if (e.getSource() == addOrderItem) {
            cardLayout.show(contentPanel, "ORDER");
        } else if (e.getSource() == availstock) {
			cardLayout.show(contentPanel, "STOCK");
        } else if (e.getSource() == dsale) {
			cardLayout.show(contentPanel, "DAILY_SALE");
        } else if (e.getSource() == msale) {
			cardLayout.show(contentPanel, "MONTHLY_SALE");
        } else if (e.getSource() == pizzasale) {
			cardLayout.show(contentPanel, "TOP_SELLING");
        }
		
        revalidate();
        repaint();
    }

    public static void main(String args[]) {
        new AdminDashboard();
    }
}
