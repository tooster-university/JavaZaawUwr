package tooster.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Testing class for date, echo and totient servers
public class Client1 {
    public static void main(String[] args) {
        String hostName = args[0];

        try (
                Socket dateSocket = new Socket(hostName, 20191);
                BufferedReader in = new BufferedReader(new InputStreamReader(dateSocket.getInputStream()))
        ) {
            System.out.println("date returned by server: " + in.readLine());
        } catch (IOException ioe) {
            System.err.println("Client date error: " + ioe.getMessage());
            ioe.printStackTrace();
            System.exit(-1);
        }

    }
}
