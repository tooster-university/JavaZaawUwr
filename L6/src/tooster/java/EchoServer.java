package tooster.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

// TCP, port 20192
public class EchoServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(20192)) {

            serverSocket.setSoTimeout(30 * 1000);
            System.err.println("Echo server started on " + serverSocket.getLocalSocketAddress().toString() +
                    ". Inactivity for 30 seconds will result in timeout.");

            try (Socket clientSocket = serverSocket.accept(); // block on client
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                System.err.println("Echo server connection established.");
                String s;
                while (!(s = in.readLine()).equals("."))
                    out.println(s);

                System.err.println("Connection closed by client.");
            }

        } catch (SocketTimeoutException ste) { // timeout
            System.err.println("Timeout reached.");
        } catch (IOException ioe) { // socket error
            System.err.println("Server socket error.");
            ioe.printStackTrace();
            System.exit(-1);
        }

        System.err.println("Server stopped.");

    }
}
