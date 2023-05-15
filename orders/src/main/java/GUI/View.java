package GUI;
import GUI.FrameClient;
import GUI.FrameOrder;
import GUI.FrameProduct;

import javax.swing.*;
import java.awt.*;
public class View {

    private static void createAndShowGUI() {
        FrameClient clientGUI = new FrameClient();
        FrameProduct orderGUI = new FrameProduct();
        FrameOrder productGUI = new FrameOrder();

        JFrame frame = new JFrame("Order Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3));
        frame.add(clientGUI.getContentPane());
        frame.add(orderGUI.getContentPane());
        frame.add(productGUI.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(View::createAndShowGUI);
    }

}