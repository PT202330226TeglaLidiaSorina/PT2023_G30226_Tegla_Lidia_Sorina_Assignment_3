package GUI;

import model.Client;
import dao.DaoClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FrameClient extends JFrame {

    private JTextField idField;
    private JTextField firstNameField;
    private JTextField addressField;
    private JTextField emailField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable clientTable;

    private DaoClient daoClient;

    public FrameClient() {
        daoClient = new DaoClient();

        // Set up GUI components
        idField = new JTextField(10);
        firstNameField = new JTextField(10);
        addressField = new JTextField(10);
        emailField = new JTextField(10);
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateClient();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });

        // Set up client table
        clientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        // Set up layout
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);

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
        setTitle("Client Management System");
        setContentPane(mainPanel);
        pack();
//        setVisible(true);

        // Load client data into table
        loadData();
    }

    private void addClient() {
        int id = Integer.parseInt(idField.getText());
        String firstName = firstNameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();

        Client client = new Client(id, firstName, address, email);
        daoClient.insert(client);

        loadData();
        //clearFields();
    }

    private void updateClient() {
        int id = Integer.parseInt(idField.getText());
        String name = firstNameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();

        Client client = new Client(id, name, address, email);
        daoClient.update(client);

        loadData();
        //clearFields();
    }

    private void deleteClient() {
        int id = Integer.parseInt(idField.getText());
        Client client = new Client(id, "", "", "");
        daoClient.delete(client.getId());

        loadData();
        //  clearFields();
    }

    private void loadData() {
        List<Client> clients = daoClient.findAll();
        String[] columnNames = {"ID", "Name", "Address", "Contact"};
        Object[][] data = new Object[clients.size()][4];
        int row = 0;
        for (Client client : clients) {
            data[row][0] = client.getId();
            data[row][1] = client.getName();
            data[row][2] = client.getAddress();
            data[row][3] = client.getContact();
            row++;
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        clientTable.setModel(model);
    }

    private void clearFields(){

    }
}
