package src.tooster.java.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;

public interface PrimerFactoryInterface extends Remote{
    public PrimerInterface getPrimer() throws RemoteException;
}
