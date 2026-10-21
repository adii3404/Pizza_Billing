import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

class JTableDB2Panel extends JFrame implements ActionListener
{
    JLabel lpizzaID, lname, lcategory, lsmall, lmedium, llarge, lxl, lcustom;
    JTextField tpizzaID, tname, tsmall, tmedium, tlarge, txl, tcustom;
    JComboBox<String> cbCategory;
    JButton clr, add, del, updt, view, ext;

    Connection cn;
    Statement stm;
    ResultSet rs;
    String sql;
    PreparedStatement prstm;

    JTableDB2Panel()
    {
        super("Pizza Menu Management");
        setLayout(null);
        setSize(500, 500);
        setVisible(true);
        setLocation(350,200);

        lpizzaID = new JLabel("Pizza ID");
        lname = new JLabel("Name");
        lcategory = new JLabel("Category");
        lsmall = new JLabel("Small Price");
        lmedium = new JLabel("Medium Price");
        llarge = new JLabel("Large Price");
        lxl = new JLabel("XL Price");
        lcustom = new JLabel("Customizations");

        tpizzaID = new JTextField("");
        tname = new JTextField("");
        tsmall = new JTextField("");
        tmedium = new JTextField("");
        tlarge = new JTextField("");
        txl = new JTextField("");
        tcustom = new JTextField("");

        String categories[] = {"Classic", "Specialty", "Build Your Own"};
        cbCategory = new JComboBox<>(categories);

        clr = new JButton("CLEAR");
        add = new JButton("ADD");
        del = new JButton("DELETE");
        updt = new JButton("UPDATE");
        view = new JButton("VIEW");
        ext = new JButton("EXIT");

        add(lpizzaID); add(tpizzaID);
        add(lname); add(tname);
        add(lcategory); add(cbCategory);
        add(lsmall); add(tsmall);
        add(lmedium); add(tmedium);
        add(llarge); add(tlarge);
        add(lxl); add(txl);
        add(lcustom); add(tcustom);
        add(clr); add(add); add(del); add(updt); add(view); add(ext);

        clr.addActionListener(this);
        add.addActionListener(this);
        updt.addActionListener(this);
        del.addActionListener(this);
        view.addActionListener(this);
        ext.addActionListener(this);

        lpizzaID.setBounds(10,10,100,20);
        tpizzaID.setBounds(120,10,100,20);
        lname.setBounds(10,40,100,20);
        tname.setBounds(120,40,100,20);
        lcategory.setBounds(10,70,100,20);
        cbCategory.setBounds(120,70,120,20);
        lsmall.setBounds(10,100,100,20);
        tsmall.setBounds(120,100,100,20);
        lmedium.setBounds(10,130,100,20);
        tmedium.setBounds(120,130,100,20);
        llarge.setBounds(10,160,100,20);
        tlarge.setBounds(120,160,100,20);
        lxl.setBounds(10,190,100,20);
        txl.setBounds(120,190,100,20);
        lcustom.setBounds(10,220,100,20);
        tcustom.setBounds(120,220,150,20);

        clr.setBounds(10,260,80,20);
        add.setBounds(100,260,80,20);
        del.setBounds(190,260,80,20);
        updt.setBounds(10,290,80,20);
        view.setBounds(100,290,80,20);
        ext.setBounds(190,290,80,20);

        try
        {
            cn = DBConnection.getConnection();
            stm = cn.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        tpizzaID.requestFocus();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if (e.getSource() == clr)
            {
                clearAll();
            }

            if (e.getSource() == add)
            {
                sql = "INSERT INTO Pizzas (name, category, small_price, medium_price, large_price, extra_large_price, customization_options) VALUES (?, ?, ?, ?, ?, ?, ?)";
                prstm = cn.prepareStatement(sql);
                prstm.setString(1, tname.getText());
                prstm.setString(2, cbCategory.getSelectedItem().toString());
                prstm.setDouble(3, Double.parseDouble(tsmall.getText()));
                prstm.setDouble(4, Double.parseDouble(tmedium.getText()));
                prstm.setDouble(5, Double.parseDouble(tlarge.getText()));
                prstm.setDouble(6, Double.parseDouble(txl.getText()));
                prstm.setString(7, tcustom.getText());
                prstm.execute();
                prstm.close();
                JOptionPane.showMessageDialog(null, "Pizza Added Successfully!");
                updateTable();
                clearAll();
            }

            if (e.getSource() == del)
            {
                sql = "DELETE FROM Pizzas WHERE pizza_id = ?";
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
                sql = "UPDATE Pizzas SET name=?, category=?, small_price=?, medium_price=?, large_price=?, extra_large_price=?, customization_options=? WHERE pizza_id=?";
                prstm = cn.prepareStatement(sql);
                prstm.setString(1, tname.getText());
                prstm.setString(2, cbCategory.getSelectedItem().toString());
                prstm.setDouble(3, Double.parseDouble(tsmall.getText()));
                prstm.setDouble(4, Double.parseDouble(tmedium.getText()));
                prstm.setDouble(5, Double.parseDouble(tlarge.getText()));
                prstm.setDouble(6, Double.parseDouble(txl.getText()));
                prstm.setString(7, tcustom.getText());
                prstm.setInt(8, Integer.parseInt(tpizzaID.getText()));
                prstm.execute();
                prstm.close();
                JOptionPane.showMessageDialog(null, "Pizza Updated Successfully!");
                updateTable();
                clearAll();
            }

            if (e.getSource() == view)
            {
                updateTable();
                clearAll();
            }

            if (e.getSource() == ext)
            {
                System.exit(0);
            }
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    void clearAll()
    {
        tpizzaID.setText("");
        tname.setText("");
        tsmall.setText("");
        tmedium.setText("");
        tlarge.setText("");
        txl.setText("");
        tcustom.setText("");
        tpizzaID.requestFocus();
    }

    void updateTable() {
        try {
            stm = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    
            // Count rows
            rs = stm.executeQuery("SELECT COUNT(*) FROM Pizzas");
            rs.next(); // Use .next() instead of .first()
            int rowcnt = rs.getInt(1);
    
            // Define table columns
            String colHeads[] = { "Pizza ID", "Name", "Category", "Small Price", "Medium Price", "Large Price", "XL Price" };
            String data[][] = new String[rowcnt][7];
    
            // Fetch data
            rs = stm.executeQuery("SELECT pizza_id, name, category, small_price, medium_price, large_price, extra_large_price FROM Pizzas");
    
            int i = 0;
            while (rs.next()) { // Use while loop instead of first()
                data[i][0] = rs.getString(1);
                data[i][1] = rs.getString(2);
                data[i][2] = rs.getString(3);
                data[i][3] = rs.getString(4);
                data[i][4] = rs.getString(5);
                data[i][5] = rs.getString(6);
                data[i][6] = rs.getString(7);
                i++;
            }
    
            // Create table
            JTable table = new JTable(data, colHeads);
            table.setEnabled(false);
    
            // Add table to scroll pane
            JScrollPane jsp = new JScrollPane(table);
            jsp.setBounds(10, 50, 450, 150);
    
            // Remove previous table and refresh UI
            getContentPane().removeAll();
            repaint();
    
            // Re-add components and table
           
            add(jsp);
    
            revalidate(); // Refresh frame layout
        } 
        catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    
    

    public static void main(String args[])
    {
        new JTableDB2Panel();
    }
}
