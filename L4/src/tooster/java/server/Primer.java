package src.tooster.java.server;

import src.tooster.java.common.PrimerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Primer extends UnicastRemoteObject implements PrimerInterface {
    private static final int LIMIT = 1 << 24; // ~ 64 mb
    private static final int[] sieve;

    // initialize sieve
    static {
        sieve = new int[LIMIT];
        sieve[1] = 1;
        for (int i = 2; i < LIMIT; i++)
            for (int j = i; j < LIMIT; j += i)
                if (sieve[j] == 0)
                    sieve[j] = i;
    }

    protected Primer() throws RemoteException {
    }


    @Override
    public boolean isPrime(long x) {
        if (x < 2) return false;
        if (x < LIMIT) return sieve[(int) x] == x; // fast escape from small numbers
        else {
            if (x % 2 == 0) return false;
            for (long i = 3; i * i > 0 && i * i <= x; i += 2) // upto square of x
                if (x % i == 0)
                    return false;
        }
        return true;
    }

    @Override
    public Long[] factorize(long x) {
        ArrayList<Long> factors = new ArrayList<>();
        // two special cases:
        if (x == -1 || x == 0 || x == 1) { // -1,0,1
            factors.add(x);
            return factors.toArray(new Long[0]);
        } else if (x == Long.MIN_VALUE) { // ll_min handled separately: long.min*(-1) cannot be converted to positive
            factors.add((long) -1);
            while (x < -1) {
                factors.add((long) 2);
                x /= 2;
            }
            return factors.toArray(new Long[0]);
        }

        if (x < 0) { // convert negatives to positives
            factors.add((long) -1);
            x *= -1;
        }

        if (isPrime(x)) { // if prime return it
            factors.add(x);
            return factors.toArray(new Long[0]);
        }

        // factorization
        if (x < LIMIT) { // case: x < LIMIT
            while (x > 1) {
                factors.add((long) sieve[(int) x]);
                x /= sieve[(int) x];
            }
        } else {
            while (x % 2 == 0) { // speeds up whole thing 2 times
                factors.add((long) 2);
                x /= 2;
            }
            for (long i = 3; x > 1 && i * i > 0 && i * i <= x + 1;
                 i += 2) {
                while (
                        (i < LIMIT && sieve[(int) i] == i && x % i == 0) ||
                                (i > LIMIT && x % i == 0)
                ) {
                    factors.add(i);
                    x /= i;
                }
            }
        }
        if (x > 1)
            factors.add(x);
        return factors.toArray(new Long[0]);
    }
}
