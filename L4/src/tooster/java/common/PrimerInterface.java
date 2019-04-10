package src.tooster.java.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrimerInterface extends Remote {
    public boolean isPrime(long x) throws RemoteException;

    public Long[] factorize(long x) throws RemoteException;
}
