package src.tooster.java.client;

import src.tooster.java.common.PrimerFactoryInterface;
import src.tooster.java.common.PrimerInterface;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;

public class Client {
    public Client() {}

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null); // localhost

            PrimerFactoryInterface primerFactory =
                    (PrimerFactoryInterface) registry.lookup("primerFactory");
            PrimerInterface primer = primerFactory.getPrimer();


// -------- CLI
            if (args.length == 1 && args[0].equals("--cli")) {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLong()) {
                    long x = scanner.nextLong();
                    if (primer.isPrime(x)) {
                        System.out.println(x + " is prime");
                    } else {
                        System.out.println(Arrays.toString(primer.factorize(x)));
                    }
                }
            }
// -------- GUI
            JFrame frame = new JFrame("Primer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(300,70));
            Dimension scrsz = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(scrsz.width / 2, scrsz.height / 2);
            JFormattedTextField input = new JFormattedTextField(NumberFormat.getIntegerInstance());
            input.setValue(0L);
            input.setColumns(20);
            input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JButton submit = new JButton(" = ");
            JLabel returnedValue = new JLabel("Please, input number in 'Long' range");
            submit.addActionListener(e -> {
                try {
                    long x = (Long) input.getValue();
                    if (primer.isPrime(x))
                        returnedValue.setText("prime");
                    else
                        returnedValue.setText(Arrays.toString(primer.factorize(x)));
                } catch (RemoteException e1) {
                    returnedValue.setText("Remote exception, see stderr.");
                    System.err.println("Client exception: ");
                    e1.printStackTrace();
                } catch (ClassCastException cce){
                    returnedValue.setText("Invalid input.");
                }
            });

            frame.getContentPane().add(BorderLayout.WEST, submit);
            frame.getContentPane().add(BorderLayout.CENTER, input);
            frame.getContentPane().add(BorderLayout.SOUTH, returnedValue);


//            BoxLayout boxLayout = new BoxLayout(frame, BoxLayout.Y_AXIS);
//            frame.setLayout(boxLayout);
            frame.pack();
            frame.setVisible(true);


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
