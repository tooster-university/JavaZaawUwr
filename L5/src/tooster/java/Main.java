package tooster.java;

import javax.swing.*;
import java.awt.*;

public class Main {


    public static void main(String[] args) {
        JFrame frame = new JFrame("Crawler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300,70));
        Dimension scrsz = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(scrsz.width / 2, scrsz.height / 2);
        JTextField input = new JTextField();

        frame.setLayout(new BorderLayout());
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));


        frame.getContentPane().add(BorderLayout.WEST, input );
        frame.pack();
        frame.setVisible(true);
    }

}
