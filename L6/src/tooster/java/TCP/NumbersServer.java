package tooster.java.TCP;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class NumbersServer {

    private static class WorkerThread implements Runnable {

        static ServerSocket serverSocket;
        static Socket clientSocket;

        static void updateLabels(NumbersProtocol np){
            turns.setText("TURNS USED: " + np.getRequestsCnt());
            number.setText("NUMBER: " + np.getCurrentNumber());
        }
        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(20194)) {
                serverSocket = ss;
                System.err.println("Numbers server started on port 20194.");

                NumbersProtocol np = new NumbersProtocol();

                try (Socket cs = ss.accept();
                     PrintWriter out = new PrintWriter(cs.getOutputStream(), false);
                     BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()))) {
                    System.err.println("Numbers server connection established.");
                    clientSocket = cs;
                    updateLabels(np);
                    String received;
                    while (!Thread.interrupted() && (received = in.readLine()) != null) {
                        String response = np.processInput(received);
                        updateLabels(np);
                        out.println(response);
                        out.flush();
                    }
                }

            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }

            turns.setText("SERVER OFF");
            number.setText("SERVER OFF");
            System.err.println("Server stopped.");
        }
    }

    static Thread workerThread;
    static JLabel turns;
    static JLabel number;

    private static void stopServer() {
        try {
            workerThread.interrupt();
            NumbersServer.WorkerThread.serverSocket.close();
            NumbersServer.WorkerThread.clientSocket.close();
        } catch (Exception ignored) {
        }
        workerThread = null;
        turns.setText("SERVER OFF");
        number.setText("SERVER OFF");

    }

    private static void startServer() {
        if (workerThread != null) {
            System.err.println("Server already running");
            return;
        }
        workerThread = new Thread(new NumbersServer.WorkerThread());
        workerThread.start();
        turns.setText("SERVER ON");
        number.setText("SERVER ON");
    }


    public static void main(String[] args) {

        JFrame panel = new JFrame();
        JButton start = new JButton("START");
        JButton stop = new JButton("STOP");
        turns = new JLabel("SERVER OFF");
        number = new JLabel("SERVER OFF");

        panel.add(start);
        panel.add(stop);
        panel.add(turns);
        panel.add(number);
        panel.setLayout(new GridLayout(2, 2));
        panel.setVisible(true);
        panel.setSize(300, 120);
        panel.setResizable(false);
        panel.setLocation(400, 400);

        panel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        start.addActionListener(e -> startServer());

        stop.addActionListener(e -> stopServer());

    }
}
