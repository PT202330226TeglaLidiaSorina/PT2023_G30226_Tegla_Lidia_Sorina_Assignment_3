package GUI;
import dao.*;
import model.*;
import model.Orders;
import dao.DaoOrder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FrameOrder extends JFrame {

    private DaoProduct daoProduct;
    private final JTextField idField;
    private final JTextField clientIdField;
    private final JTextField productIdField;
    private final JTextField quantityField;
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private JTable orderTable;

    private DaoOrder daoOrder;

    public FrameOrder() {
        daoOrder = new DaoOrder();
        daoProduct=new DaoProduct();
        idField = new JTextField(10);
        clientIdField = new JTextField(10);
        productIdField = new JTextField(10);
        quantityField = new JTextField(10);
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOrder();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

        // Set up order table
        orderTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        // Set up layout
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Client ID:"));
        inputPanel.add(clientIdField);
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Order Management System");
        setContentPane(mainPanel);
        pack();
     //   setVisible(true);

        // Load order data into table
        loadData();
    }

public void addOrder() {
    int id = Integer.parseInt(idField.getText());
    int clientId = Integer.parseInt(clientIdField.getText());
    int productId = Integer.parseInt(productIdField.getText());
    int quantity = Integer.parseInt(quantityField.getText());

    Orders order = new Orders(id, clientId, productId, quantity);
    daoOrder.insert(order);

    // Update the quantity of the product associated with the order
    Product product = daoProduct.findById(String.valueOf(productId));
    int newQuantity = product.getQuantity() - quantity;
    product.setQuantity(newQuantity);
    daoProduct.update(product);

    loadData();
}

    public void updateOrder() {
        int id = Integer.parseInt(idField.getText());
        int clientId = Integer.parseInt(clientIdField.getText());
        int productId = Integer.parseInt(productIdField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        Orders order = new Orders(id, clientId, productId, quantity);
        daoOrder.update(order);

        loadData();
    }

public void deleteOrder() {
    int id = Integer.parseInt(idField.getText());
    Orders order = daoOrder.findById(String.valueOf(id));
    daoOrder.delete(order.getId());

    // Update the quantity of the product associated with the order
    Product product = daoProduct.findById(String.valueOf(order.getProductId()));
    int newQuantity = product.getQuantity() + order.getQuantity();
    product.setQuantity(newQuantity);
    daoProduct.update(product);

    loadData();
}

    public void loadData() {
        List<Orders> orders = daoOrder.findAll();
        String[] columnNames = {"ID", "Client ID","Product ID","Quantity"};
        Object[][] data = new Object[orders.size()][4];
        int row = 0;
        for (Orders order : orders) {
            data[row][0] = order.getId();
            data[row][1] = order.getClientId();
            data[row][2] = order.getProductId();
            data[row][3] = order.getQuantity();
            row++;
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        orderTable.setModel(model);
    }
}