package GUI;

import model.Client;
import model.Product;
import dao.DaoProduct;
import data.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class FrameProduct extends JFrame {
    private static final String DBURL = "jdbc:mysql://localhost:3306/schooldb";
    private static final String USER = "Sorina";
    private static final String PASS = "JustinBieber28";
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JTable productTable;

    private DaoProduct daoProduct;

    public FrameProduct (){
        daoProduct = new DaoProduct();

        // Set up GUI components
        idField = new JTextField(10);
        nameField = new JTextField(10);
        priceField = new JTextField(10);
        quantityField = new JTextField(10);
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton =new JButton("Refresh");

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        // Set up product table
        productTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        // Set up layout
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Product Management System");
        setContentPane(mainPanel);
        pack();
      //  setVisible(true);

        // Load product data into table
        loadData();
    }

    public void addProduct() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        Product product = new Product(id, name, quantity, price);
        daoProduct.insert(product);

        loadData();
    }

    public void updateProduct() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        Product product = new Product(id, name, quantity, price);
        daoProduct.update(product);

        loadData();
    }

    public void deleteProduct() {
        int id = Integer.parseInt(idField.getText());
        Product product = new Product(id, "", 0, 0.0);
        daoProduct.delete(product.getId());

        loadData();
    }

    public void loadData() {
        List<Product> products = daoProduct.findAll();
        String[] columnNames = {"ID", "Name", "Quantity", "Price"};
        Object[][] data = new Object[products.size()][4];
        int row = 0;
        for (Product product : products) {
            data[row][0] = product.getId();
            data[row][1] = product.getName();
            data[row][2] = product.getQuantity();
            data[row][3] = product.getPrice();
            row++;
        }
        DefaultTableModel model1 = new DefaultTableModel(data, columnNames);
        productTable.setModel(model1);
    }


}
