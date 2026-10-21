import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class product extends JPanel implements ActionListener {
    JLabel lpizzaID, lname, lcategory, lsmall, lmedium, llarge, lxl, lcustom, limage;
    JTextField tpizzaID, tname, tsmall, tmedium, tlarge, txl, tcustom, timage;
    JComboBox<String> cbCategory;
    JButton clr, add, del, updt, view, ext, b1, b2, browse;
    Connection cn; 
    Statement stm;
    ResultSet rs;
    PreparedStatement prstm;
    JScrollPane tableScrollPane;

    public product() {
        setLayout(null);
        setSize(1500, 700);
		setBackground(Color.WHITE);

        // Labels
        lpizzaID = new JLabel("Pizza ID");
        lname = new JLabel("Name");
        lcategory = new JLabel("Category");
        lsmall = new JLabel("Small Price");
        lmedium = new JLabel("Medium Price");
        llarge = new JLabel("Large Price");
        lxl = new JLabel("XL Price");
        lcustom = new JLabel("quantity");
        limage = new JLabel("Image Path");

        // Text Fields
        tpizzaID = new JTextField();
        tname = new JTextField();
        tsmall = new JTextField();
        tmedium = new JTextField();
        tlarge = new JTextField();
        txl = new JTextField();
        tcustom = new JTextField();
        timage = new JTextField();

        // Category Dropdown
        String categories[] = {"Classic Pizza", "Pan Pizza", "Oil Pizza"};
        cbCategory = new JComboBox<>(categories);

        // Buttons
        clr = new JButton("CLEAR");
        add = new JButton("ADD");
        del = new JButton("DELETE");
        updt = new JButton("UPDATE");
        view = new JButton("VIEW");
        ext = new JButton("EXIT");
		b1 = new JButton(">>");  // Next
        b2 = new JButton("<<");  
        browse = new JButton("...");
		
		clr.setBackground(new Color(0, 100, 145));
        clr.setForeground(Color.WHITE);
        add.setBackground(new Color(0, 100, 145));
		add.setForeground(Color.WHITE);
        del.setBackground(new Color(0, 100, 145));
		del.setForeground(Color.WHITE);
        updt.setBackground(new Color(0, 100, 145));
		updt.setForeground(Color.WHITE);
        view.setBackground(new Color(0, 100, 145));
		view.setForeground(Color.WHITE);
        ext.setBackground(new Color(0, 100, 145));
		ext.setForeground(Color.WHITE);
		b1.setBackground(new Color(0, 100, 145));
		b1.setForeground(Color.WHITE);
        b2.setBackground(new Color(0, 100, 145));
		b2.setForeground(Color.WHITE);
		

        // Add components 
        add(lpizzaID);	add(tpizzaID);	add(lcategory);		add(lsmall);	add(lmedium);
		add(lname);		add(tname);		add(cbCategory);	add(tsmall);	add(tmedium);
		add(llarge); add(tlarge); add(lxl);	add(txl);	add(lcustom);	add(clr); add(b2);
		add(add);	add(del);	add(updt); add(tcustom);	add(view);	add(ext);	add(b1);
        add(limage); add(timage); add(browse);
		//setBounds
        lpizzaID.setBounds(20, 20, 120, 25);
        tpizzaID.setBounds(150, 20, 180, 25);
        lname.setBounds(20, 55, 120, 25);
        tname.setBounds(150, 55, 180, 25);
        lcategory.setBounds(20, 90, 120, 25);
        cbCategory.setBounds(150, 90, 180, 25);
        lsmall.setBounds(20, 125, 120, 25);
        tsmall.setBounds(150, 125, 180, 25);
        lmedium.setBounds(20, 160, 120, 25);
        tmedium.setBounds(150, 160, 180, 25);
        llarge.setBounds(20, 195, 120, 25);
        tlarge.setBounds(150, 195, 180, 25);
        lxl.setBounds(20, 230, 120, 25);
        txl.setBounds(150, 230, 180, 25);
        lcustom.setBounds(20, 265, 120, 25);
        tcustom.setBounds(150, 265, 180, 25);
        clr.setBounds(20, 300, 100, 25);
        add.setBounds(130, 300, 100, 25);
        del.setBounds(240, 300, 100, 25);
        updt.setBounds(20, 335, 100, 25);
        view.setBounds(130, 335, 100, 25);
        ext.setBounds(240, 335, 100, 25);
		b1.setBounds(190, 370, 150, 25);
		b2.setBounds(20, 370, 150, 25); 
        limage.setBounds(20, 405, 120, 25);
        timage.setBounds(150, 405, 150, 25);
        browse.setBounds(305, 405, 30, 25);
        
		
		//setFont
		Font customFont = new Font("Arial", Font.BOLD, 14);
		lpizzaID.setFont(customFont);	lname.setFont(customFont);
        lcategory.setFont(customFont);	lsmall.setFont(customFont);
		lmedium.setFont(customFont);	llarge.setFont(customFont);
        lxl.setFont(customFont);		lcustom.setFont(customFont);
        tpizzaID.setFont(customFont);	tname.setFont(customFont);
        tsmall.setFont(customFont);		tmedium.setFont(customFont);
        tlarge.setFont(customFont);		txl.setFont(customFont);
        tcustom.setFont(customFont);	cbCategory.setFont(customFont);
		clr.setFont(customFont);		add.setFont(customFont);
        del.setFont(customFont);		updt.setFont(customFont);
        view.setFont(customFont);		ext.setFont(customFont);
		b1.setFont(customFont);			b2.setFont(customFont);
        limage.setFont(customFont);     timage.setFont(customFont);     browse.setFont(customFont);

        // Action listeners
        clr.addActionListener(this);
        add.addActionListener(this);
        updt.addActionListener(this);
        del.addActionListener(this);
        view.addActionListener(this);
        ext.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);

        // Connect to database
        try {
            cn = DBConnection.getConnection();
            stm = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery("SELECT * FROM pizzas ORDER BY pizza_id");
            if (rs.next()) {
                rs.first();
                display();
            } else {
                JOptionPane.showMessageDialog(null, "No pizzas found in the database. Please add some products.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        browse.addActionListener(e -> {
            new GalleryDialog(timage);
        });
    }

    // Inner class for Visual Gallery
    class GalleryDialog extends JDialog {
        public GalleryDialog(JTextField targetField) {
            setTitle("Product Photo Gallery");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel grid = new JPanel(new GridLayout(0, 4, 10, 10));
            grid.setBackground(Color.WHITE);
            grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            File dir = new File(".");
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));

            if (files != null) {
                for (File f : files) {
                    if (f.getName().equals("upi_qr.png")) continue; // Skip payment QR
                    
                    JButton btn = new JButton(f.getName());
                    try {
                        ImageIcon icon = new ImageIcon(f.getName());
                        Image img = icon.getImage().getScaledInstance(120, 90, Image.SCALE_SMOOTH);
                        btn.setIcon(new ImageIcon(img));
                        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                        btn.setHorizontalTextPosition(SwingConstants.CENTER);
                        btn.setBackground(Color.WHITE);
                        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
                        
                        btn.addActionListener(e -> {
                            targetField.setText(f.getName());
                            dispose();
                        });
                        grid.add(btn);
                    } catch (Exception ex) {}
                }
            }

            add(new JScrollPane(grid), BorderLayout.CENTER);
            setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == clr) {
                clearAll();
            } else if (e.getSource() == add) {
                String sql = "INSERT INTO pizzas (name, category, small_price, medium_price, large_price, extra_large_price, quantity, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                prstm = cn.prepareStatement(sql);
                prstm.setString(1, tname.getText());
                prstm.setString(2, cbCategory.getSelectedItem().toString());
                prstm.setDouble(3, parseDouble(tsmall.getText()));
                prstm.setDouble(4, parseDouble(tmedium.getText()));
                prstm.setDouble(5, parseDouble(tlarge.getText()));
                prstm.setDouble(6, parseDouble(txl.getText()));
                prstm.setString(7, tcustom.getText());
                prstm.setString(8, timage.getText().isEmpty() ? "default_pizza.png" : timage.getText());
                prstm.execute();
                prstm.close();
                JOptionPane.showMessageDialog(null, "Pizza Added Successfully!");
                clearAll();
            }else if (e.getSource() == view) {
                updateTable();
                clearAll();
            } else if (e.getSource() == ext) {
                this.setVisible(false);
            }
			if (e.getSource() == del)
            {
               String sql = "DELETE FROM pizzas WHERE pizza_id = ?";
                prstm = cn.prepareStatement(sql);
                prstm.setInt(1, Integer.parseInt(tpizzaID.getText()));
                prstm.execute();
                prstm.close();
                JOptionPane.showMessageDialog(null, "Pizza Deleted Successfully!");
                updateTable();
                clearAll();
            }

            if (e.getSource() == updt)
            {
                String sql = "UPDATE pizzas SET name=?, category=?, small_price=?, medium_price=?, large_price=?, extra_large_price=?, quantity=?, image_path=? WHERE pizza_id=?";
                prstm = cn.prepareStatement(sql);
                prstm.setString(1, tname.getText());
                prstm.setString(2, cbCategory.getSelectedItem().toString());
                prstm.setDouble(3, parseDouble(tsmall.getText()));
                prstm.setDouble(4, parseDouble(tmedium.getText()));
                prstm.setDouble(5, parseDouble(tlarge.getText()));
                prstm.setDouble(6, parseDouble(txl.getText()));
                prstm.setString(7, tcustom.getText());
                prstm.setString(8, timage.getText().isEmpty() ? "default_pizza.png" : timage.getText());
                prstm.setInt(9, Integer.parseInt(tpizzaID.getText()));
                prstm.execute();
                prstm.close();
                JOptionPane.showMessageDialog(null, "Pizza Updated Successfully!");
                updateTable();
                clearAll();
            }
			else if (e.getSource() == b1) { // Next Record
                if (rs.next()) {
                    display();
                } else {
                    rs.last();
                }
            } else if (e.getSource() == b2) { // Previous Record
                if (rs.previous()) {
                    display();
                } else {
                    rs.first();
                }
            }
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(null, "Error: Please enter valid numbers for prices and quantity.");
        }
    }
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }
	void display() {
        try {
            tpizzaID.setText(rs.getString("pizza_id"));
            tname.setText(rs.getString("name"));
            cbCategory.setSelectedItem(rs.getString("category"));
            tsmall.setText(rs.getString("small_price"));
            tmedium.setText(rs.getString("medium_price"));
            tlarge.setText(rs.getString("large_price"));
            txl.setText(rs.getString("extra_large_price"));
            tcustom.setText(rs.getString("quantity"));
            timage.setText(rs.getString("image_path"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void clearAll() {
        tpizzaID.setText("");
        tname.setText("");
        tsmall.setText("");
        tmedium.setText("");
        tlarge.setText("");
        txl.setText("");
        tcustom.setText("");
        timage.setText("");
        tpizzaID.requestFocus();
    }

   void updateTable() {
    try {
        if (tableScrollPane != null) {
            remove(tableScrollPane);
        }

        rs = stm.executeQuery("SELECT pizza_id, name, category, small_price, medium_price, large_price, extra_large_price, quantity, image_path FROM pizzas");

        String[] colHeads = {"Pizza ID", "Name", "Category", "Small Price", "Medium Price", "Large Price", "XL Price", "quantity", "Image"};

        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();

        String[][] data = new String[rowCount][9];
        int i = 0;
        while (rs.next()) {
            for (int j = 0; j < 9; j++) {  
                data[i][j] = rs.getString(j + 1);
            }
            i++;
        }

        JTable table = new JTable(data, colHeads);
        table.setEnabled(false);
        Font headerFont = new Font("Arial", Font.BOLD, 14);
        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);

        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBounds(350, 20, 930, 300);
        add(tableScrollPane);

        revalidate();
        repaint();
    } catch (Exception exp) {
        exp.printStackTrace();
    }
}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pizza Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 700);
        frame.add(new product());
        frame.setVisible(true);
    }
}
