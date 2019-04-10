package src.tooster.java.server;

import src.tooster.java.common.PrimerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends Primer {
    static Registry registry;
    public Server() {}

    public static void main(String[] args) {
        try{
            Primer primer = new Primer();

            PrimerInterface stub = (PrimerInterface) UnicastRemoteObject.exportObject(primer, 0);

            Registry registry = LocateRegistry.getRegistry();

            registry.rebind("primer", stub);
            System.err.println("Server ready");
        } catch (Exception e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
