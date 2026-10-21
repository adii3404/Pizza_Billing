import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import com.itextpdf.text.Font;

public class GenerateBill extends JFrame {
    private JTextArea billArea;
    private JButton printButton;

    public GenerateBill() {
        setTitle("Bill Receipt");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        billArea = new JTextArea();
        billArea.setFont(new java.awt.Font("Segoe UI Monospaced", java.awt.Font.BOLD, 14));
        billArea.setEditable(false);
        billArea.setBackground(new Color(252, 252, 252));
        billArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(billArea);

        printButton = new JButton("SAVE & PRINT RECEIPT (PDF)");
        printButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        printButton.setBackground(new Color(0, 100, 145));
        printButton.setForeground(Color.WHITE);
        printButton.setPreferredSize(new Dimension(0, 50));
        printButton.addActionListener(e -> generatePDF());

        add(scrollPane, BorderLayout.CENTER);
        add(printButton, BorderLayout.SOUTH);
        
        generateBill();
        setVisible(true);
    }

  private void generateBill() {
    try (Connection con = DBConnection.getConnection()) {
        String orderQuery = "SELECT * FROM Orders ORDER BY order_id DESC LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(orderQuery);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int orderId = rs.getInt("order_id");
                String custName = rs.getString("cust_name");
                double totalPrice = rs.getDouble("total_price");
                String paymentMode = rs.getString("payment_mode");
                Timestamp orderDate = rs.getTimestamp("order_date");

                StringBuilder billText = new StringBuilder();
                billText.append("----- DOMINO'S PIZZA -----\n");
                billText.append("Customer Name: ").append(custName).append("\n");
                billText.append("Order ID: ").append(orderId).append("\n");
                billText.append("Order Date: ").append(orderDate).append("\n");
                billText.append("Payment Mode: ").append(paymentMode).append("\n");
                billText.append("----------------------------------------------------------------------------\n");
                billText.append(String.format("%-15s %-10s %-10s %-10s %-15s %-15s\n", 
                                              "Item", "Size", "Qty", "Price", "Toppings", "Cold Drinks"));
                billText.append("----------------------------------------------------------------------------\n");

                String detailsQuery = "SELECT * FROM Order_Details WHERE order_id = ?";
                
                try (PreparedStatement pstDetails = con.prepareStatement(detailsQuery)) {
                    pstDetails.setInt(1, orderId);
                    try (ResultSet rsDetails = pstDetails.executeQuery()) {
                        while (rsDetails.next()) {
                            billText.append(String.format("%-15s %-10s %-10d %-10.2f %-15s %-15s\n",
                                    rsDetails.getString("pizza_name"),
                                    rsDetails.getString("size"),
                                    rsDetails.getInt("quantity"),
                                    rsDetails.getDouble("total_price"),
                                    (rsDetails.getString("toppings") != null) ? rsDetails.getString("toppings") : "None",
                                    (rsDetails.getString("cold_drinks") != null) ? rsDetails.getString("cold_drinks") : "None"));
                        }
                    }
                }

                billText.append("----------------------------------------------------------------------------\n");
                billText.append("SGST: 2.5%\n");
                billText.append("CGST: 2.5%\n");
                billText.append("Total Price:  ₹").append(totalPrice).append("\n");
                billText.append("----------------------------------------------------------------------------\n");
                billText.append("Thank you for your order!");

                billArea.setText(billText.toString());
            } else {
                billArea.setText("No recent orders found.");
            }
        }
    } catch (SQLException e) {
        billArea.setText("Error fetching bill: " + e.getMessage());
    }
}




   private void generatePDF() {
    try (Connection con = DBConnection.getConnection()) {
        String orderQuery = "SELECT * FROM Orders ORDER BY order_id DESC LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(orderQuery);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int orderId = rs.getInt("order_id");
                String custName = rs.getString("cust_name");
                double totalPrice = rs.getDouble("total_price");
                String paymentMode = rs.getString("payment_mode");
                Timestamp orderDate = rs.getTimestamp("order_date");

                Document document = new Document(PageSize.A4);
                String filePath = "Order_Receipt.pdf";
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
                Paragraph title = new Paragraph("DOMINO'S PIZZA\n\n", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Font contentFont = new Font(Font.FontFamily.HELVETICA, 16);
                document.add(new Paragraph("Customer Name: " + custName, contentFont));
                document.add(new Paragraph("Order ID: " + orderId, contentFont));
                document.add(new Paragraph("Order Date: " + orderDate, contentFont));
                document.add(new Paragraph("Payment Mode: " + paymentMode + "\n\n", contentFont));

                // Updated Table with Toppings & Cold Drinks as separate columns
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setWidths(new float[]{2, 1, 1, 1, 2, 2}); // Adjusted column widths

                Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                String[] headers = {"Item", "Size", "Qty", "Price", "Toppings", "Cold Drinks"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                }

                // Fetch pizza details, toppings, and drinks from Order_Details
                String detailsQuery = "SELECT * FROM Order_Details WHERE order_id = ?";
                
                try (PreparedStatement pstDetails = con.prepareStatement(detailsQuery)) {
                    pstDetails.setInt(1, orderId);
                    try (ResultSet rsDetails = pstDetails.executeQuery()) {
                        while (rsDetails.next()) {
                            table.addCell(rsDetails.getString("pizza_name"));
                            table.addCell(rsDetails.getString("size"));
                            table.addCell(String.valueOf(rsDetails.getInt("quantity")));
                            table.addCell(String.format("₹%.2f", rsDetails.getDouble("total_price")));
                            table.addCell((rsDetails.getString("toppings") != null) ? rsDetails.getString("toppings") : "None");
                            table.addCell((rsDetails.getString("cold_drinks") != null) ? rsDetails.getString("cold_drinks") : "None");
                        }
                    }
                }
                
                document.add(table);

                Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
                document.add(new Paragraph("\nSGST: 2.5%", totalFont));
                document.add(new Paragraph("CGST: 2.5%", totalFont));
                document.add(new Paragraph("\nTotal Price: ₹" + totalPrice, totalFont));
                document.close();
                JOptionPane.showMessageDialog(null, "Order receipt saved as PDF!");
                Desktop.getDesktop().open(new File(filePath));
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage());
    }
}



    public static void main(String[] args) {
        new GenerateBill();
    }
}
