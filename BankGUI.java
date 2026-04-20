import java.awt.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class BankGUI extends JFrame {

    public BankGUI() {
        setTitle("Bank Management System");
        setLayout(null);
        getContentPane().setBackground(new Color(255, 253, 208)); 

        JLabel title = new JLabel("BANK MANAGEMENT SYSTEM", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(60,10,300,30);
        add(title);

        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(null);
        accountPanel.setBounds(20,50,400,150);

        accountPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY,2,true),
        "Account Information",
        TitledBorder.LEFT,
        TitledBorder.TOP
        ));

        add(accountPanel);

        JPanel  servicesPanel= new JPanel();
        servicesPanel.setLayout(null);
        servicesPanel.setBounds(20,200,400,150);

        servicesPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY,2,true),
        "Services",
        TitledBorder.LEFT,
        TitledBorder.TOP
        ));

        add(servicesPanel);
        
        JLabel newlabel1 = new JLabel("Enter Name");
        newlabel1.setBounds(32, 40, 200, 20 );
        accountPanel.add(newlabel1);
        
        JTextField nameField = new JTextField();
        nameField.setBounds(160, 40, 200, 25);
        accountPanel.add(nameField);
        
        JLabel blabel = new JLabel("Enter Initial Balance");
        blabel.setBounds(32 , 70 , 200, 25);
        accountPanel.add(blabel);
        
        JTextField balfield = new JTextField();
        balfield.setBounds(160, 70, 200, 25);
        accountPanel.add(balfield);

        JButton createButton = new JButton("Create Account");
        createButton.setBackground(new Color(0,120,215));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorder(BorderFactory.createLineBorder(new Color(0,120,215),2,true));
        createButton.setBounds(32, 110, 330, 25);
        accountPanel.add(createButton);


        //CREATE BUTTON WORKING
        createButton.addActionListener(e -> {
            try {
            String name=nameField.getText();
            String bal=balfield.getText();
            double balance = Double.parseDouble(bal);
            String password=JOptionPane.showInputDialog(this,"Create Password");
            BankAccount account=new BankAccount(name, balance, password);
            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO accounts ( name, balance , password) VALUES (?, ? ,?)";

            String hashedPassword = hashPassword(password);

            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getName());
            ps.setDouble(2, account.getBalance());
            ps.setString(3,hashedPassword);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long accNo = rs.getLong(1); 
                JOptionPane.showMessageDialog(this, "✅ Account Created!\nAcc No: " + accNo); }
            }

            catch(Exception ex) {
                ex.printStackTrace();
                // JOptionPane.showMessageDialog(this,"ERROR!");
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }

            nameField.setText("");
            balfield.setText("");
        });
        

        
        JButton depo = new JButton("Deposit Money");
        JButton withdraw = new JButton("Withdraw Money");
        JButton display = new JButton("Display Details");
        depo.setBounds(25, 30, 170, 50);
        depo.setBackground(new Color(0,153,51));
        depo.setForeground(Color.WHITE);
        withdraw.setBounds(205, 30, 170, 50);
        withdraw.setBackground(new Color(255,140,0));
        withdraw.setForeground(Color.WHITE);  
        display.setBounds(25, 90, 352, 25);  
        display.setBackground(new Color(0,120,215));
        display.setForeground(Color.WHITE);
        servicesPanel.add(depo);
        servicesPanel.add(withdraw);
        servicesPanel.add(display);

       //DEPOSIT BUTTON WORKING
        depo.addActionListener(e -> {
            try {
                long accNo = Long.parseLong( JOptionPane.showInputDialog(this, "Enter Account Number"));
                String inputPassword = JOptionPane.showInputDialog(this, "Enter Password");
                String hashedInput = hashPassword(inputPassword);
                Connection con = DBConnection.getConnection();
                String checkQuery = "SELECT * FROM accounts WHERE account_number = ? AND password = ?";
                PreparedStatement checkPs = con.prepareStatement(checkQuery);
                checkPs.setLong(1, accNo);
                checkPs.setString(2, hashedInput);

                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    double amount = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Amount to Deposit"));
                    String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement ps = con.prepareStatement(updateQuery);
                    ps.setDouble(1, amount);
                    ps.setLong(2, accNo);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "✅ Deposit Successful!");
                } 
                else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Account or Password!");
                } }
                catch (Exception ex) {
                    ex.printStackTrace();
            }
        });


        //WITHDRAW BUTTON WORKING
        withdraw.addActionListener(e -> {
            try {
                long accNo=Long.parseLong(JOptionPane.showInputDialog(this,"Enter Account Number"));
                String inputPassword = JOptionPane.showInputDialog(this, "Enter Password");
                String hashedInput = hashPassword(inputPassword); 

                Connection con = DBConnection.getConnection();

                String checkQuery = "SELECT balance FROM accounts WHERE account_number = ? AND password = ?";
                PreparedStatement checkPs = con.prepareStatement(checkQuery);

                checkPs.setLong(1, accNo);
                checkPs.setString(2, hashedInput);

                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    double amount = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Amount to Withdraw"));

                    if (currentBalance >= amount) {
                        String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement ps = con.prepareStatement(updateQuery);

                        ps.setDouble(1, amount);
                        ps.setLong(2, accNo);

                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(this, "✅ Withdrawal Successful!");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Insufficient Balance!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Account or Password!");
                }
            } 
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //DISPLAY BUTTON WORKING=
        display.addActionListener(e -> {
            try {
                long accNo = Long.parseLong(JOptionPane.showInputDialog(this, "Enter Account Number"));
                String password = JOptionPane.showInputDialog(this, "Enter Password");
                String hashedInput = hashPassword(password); 
                Connection con = DBConnection.getConnection();
                String query = "SELECT name, balance FROM accounts WHERE account_number = ? AND password = ?";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setLong(1, accNo);
                ps.setString(2, hashedInput);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    double balance = rs.getDouble("balance");

                    JOptionPane.showMessageDialog(this,
                    "User Name: " + name +
                    "\nAccount Number: " + accNo +
                    "\nBalance: " + String.format("%.2f", balance));
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Account or Password!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        setSize(450, 420);
        setResizable(false);
        setLocationRelativeTo(null);

        // addComponentListener(new ComponentAdapter() {
        //     public void componentResized(ComponentEvent e) {
        //         int maxWidth = 430;
        //         int maxHeight = 400;

        //         int width = getWidth();
        //         int height = getHeight();

        //         if (width > maxWidth || height > maxHeight) {
        //             setSize(Math.min(width, maxWidth), Math.min(height, maxHeight));
        //         }
        //     }
        // });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    

    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {

 

    new BankGUI();
    }
}

