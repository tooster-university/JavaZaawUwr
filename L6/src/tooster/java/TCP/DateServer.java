package tooster.java.TCP;


import sun.print.PrinterJobWrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

// TCP, port 20191
// after connection established, returns datetime to client, close connection and wait for next client
public class DateServer {

    public static void main(String[] args) {

        try (ServerSocket s = new ServerSocket(20191)) {
            System.err.println("Date server started on " + s.getLocalSocketAddress().toString());

            // noinspection InfiniteLoopStatement
            for (; ; ) {
                Socket clientSocket = s.accept(); // block on client
                System.err.println("Date Server connection established.");
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                Date date = new Date();
                out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date));
            }

        } catch (IOException ioe) {
            System.err.println("Server socket error.");
            ioe.printStackTrace();
            System.exit(-1);
        }

        System.err.println("Server stopped.");
    }
}
