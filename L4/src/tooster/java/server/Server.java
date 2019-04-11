package src.tooster.java.server;

import src.tooster.java.common.PrimerFactoryInterface;
import src.tooster.java.common.PrimerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements PrimerFactoryInterface {
    static Registry registry;

    public Server() {}

    public static void main(String[] args) {
        try {
            Server server = new Server();
            PrimerFactoryInterface stub =
                    (PrimerFactoryInterface) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.getRegistry();

            registry.rebind("primerFactory", stub);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public synchronized PrimerInterface getPrimer() throws RemoteException { return new Primer(); }
}
