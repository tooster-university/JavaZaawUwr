package tooster.java;

import com.sun.corba.se.pept.transport.ReaderThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

// TCP, port 20193
public class TotientServer {

    private static boolean comprime(int a, int b) {
        while (b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a == 1;
    }

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static Thread mainThread;

    // FIXME: exchange thread work -> main to read console, worker to compute
    public static void main(String[] args) {

        mainThread = Thread.currentThread();
        try (ServerSocket ss = new ServerSocket(20193)) {
            serverSocket = ss;
            System.err.println("Totient server started on " + serverSocket.getLocalSocketAddress().toString());

            // operator thread
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                do
                    System.err.println("Type 'quit' to close the server.");
                while (scanner.next().equals("quit"));

                try {
                    serverSocket.close();
                    if (clientSocket != null) clientSocket.close();
                    mainThread.interrupt();
                } catch (IOException ioe) {
                    throw new RuntimeException("Error occurred while closing server.");
                }
            }).start();


            try (Socket cs = serverSocket.accept();
                 PrintWriter out = new PrintWriter(cs.getOutputStream(), false);
                 BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()))) {
                clientSocket = cs;
                System.err.println("Totient server connection established.");

                String s;
                while (!(s = in.readLine()).equals(".")) {
                    try {
                        if (!s.matches("^\\d+$"))
                            throw new NumberFormatException();
                        int x = Integer.parseInt(s);
                        for (int i = 1; i < x; i++)
                            if (comprime(i, x))
                                out.print(i + " ");
                        out.println();
                        out.flush();

                    } catch (NumberFormatException e) {
                        out.println("Invalid number given.");
                        out.flush();
                    }
                }

            }
        } catch (
                SocketException se) {
            // server stopped while blocking on accept()
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        System.err.println("Server stopped.");

    }
}
