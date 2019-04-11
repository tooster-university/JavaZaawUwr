package src.tooster.java.client;

import src.tooster.java.common.PrimerFactoryInterface;
import src.tooster.java.common.PrimerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public Client() {}

    public static void main(String[] args) {
        try{
            Registry registry = LocateRegistry.getRegistry(null); // localhost

            PrimerFactoryInterface primerFactory =
                    (PrimerFactoryInterface) registry.lookup("primerFactory");
            PrimerInterface primer = primerFactory.getPrimer();

            Scanner scanner = new Scanner(System.in);
            long x = scanner.nextLong();
            if(primer.isPrime(x)){
                System.out.println(x + " is prime");
            } else {
                System.out.println(Arrays.toString(primer.factorize(x)));
            }

        } catch (Exception e){
            System.err.println("Client exception: "+ e.toString());
            e.printStackTrace();
        }
    }
}
