package tooster.java.TCP;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// TCP, port 20914
public class NumbersClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Launch program with ip to connect as the first argument.");
            System.exit(-1);
        }

        JFrame frame = new JFrame();
        JTextField numberInput = new JTextField("input number or type 'forfeit'");
        JButton submitButton = new JButton("SEND");
        JLabel statusLabel = new JLabel("Yet to play...");

        frame.setLayout(new BorderLayout());

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));
        main.add(numberInput);
        main.add(submitButton);

        frame.add(main, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocation(800, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Thread receiver = new Thread(() -> {
            try (
                    Socket socket = new Socket(args[0], 20194);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), false)
            ) {
                submitButton.addActionListener(e -> {
                    out.println(numberInput.getText());
                    out.flush();
                });
                String response;
                while ((response = in.readLine()) != null)
                    statusLabel.setText(response);
                System.err.println("Connection closed by server.");
                System.exit(0); // that's not an error
            } catch (IOException e) {
                System.err.println("Couldn't connect to server:\n" + e.getMessage());
                System.exit(-1);
            }
        });
        receiver.start();


    }
}
