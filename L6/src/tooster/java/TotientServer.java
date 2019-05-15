package tooster.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

// TCP, port 20193
public class TotientServer {


    private static class WorkerThread implements Runnable {

        static ServerSocket serverSocket;
        static Socket clientSocket;

        private boolean coprime(int a, int b) {
            while (b != 0) {
                int c = b;
                b = a % b;
                a = c;
            }
            return a == 1;
        }

        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(20193)) {
                serverSocket = ss;
                System.err.println("Totient server started on " + ss.getLocalSocketAddress().toString());

                try (Socket cs = ss.accept();
                     PrintWriter out = new PrintWriter(cs.getOutputStream(), false);
                     BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()))) {
                    System.err.println("Totient server connection established.");
                    clientSocket = cs;

                    String s;
                    while (!(s = in.readLine()).equals(".")) {
                        try {
                            int x = Integer.parseInt(s); // NumberFormatException
                            for (int i = 1; i < x && !Thread.interrupted(); i++)
                                if (coprime(i, x))
                                    out.print(i + " ");
                            out.println();
                            out.flush();

                        } catch (NumberFormatException e) {
                            out.println("Invalid number given.");
                            out.flush();
                        }
                    }

                }

            } catch (SocketException se) {
                //
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.err.println("Server stopped.");
        }
    }

    public static void main(String[] args) {

        Thread workerThread = new Thread(new WorkerThread());
        workerThread.start();

        Scanner scanner = new Scanner(System.in);
        do
            System.err.println("Type 'quit' to close the server.");
        while (!scanner.next().equals("quit"));

        try {
            WorkerThread.serverSocket.close();
            WorkerThread.clientSocket.close();
        } catch (Exception ignored) {}
        workerThread.interrupt();
    }
}
