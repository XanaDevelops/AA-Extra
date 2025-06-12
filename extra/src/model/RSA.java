package model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RSA {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private BigInteger P, Q, N, FN, E, D;

    private int n;
    private PrimoProbable prim = new PrimoProbable();

    public RSA(int n){
        this.n = n;
    }

    public void generate() throws ExecutionException, InterruptedException {
        List<Future<BigInteger>> calls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            calls.add(executor.submit(() -> prim.otroProbablePrimo(new BigInteger("1" + "0".repeat(n)))));
        }
        P = calls.get(0).get();
        Q = calls.get(1).get();
        E = calls.get(2).get();

        calls.add(executor.submit(() -> P.multiply(Q)));
        calls.add(executor.submit(() -> P.add(new BigInteger("-1")).multiply(Q.add(new BigInteger("-1")))));
        N = calls.get(3).get();
        FN = calls.get(4).get();

        if (E.compareTo(FN) > -1){
            throw new RuntimeException("E is greater than FN");
        }

        D = E.modInverse(FN);

    }

    public BigInteger encript(BigInteger in) {
        return in.modPow(E, N);
    }
    public BigInteger decript(BigInteger in) {
        return in.modPow(D, N);
    }


    public BigInteger[] getPublicKey() {
        BigInteger[] res = new BigInteger[2];
        res[0] = E;
        res[1] = N;
        return res;
    }

    public BigInteger[] getPrivateKey() {
        BigInteger[] res = new BigInteger[2];
        res[0] = D;
        res[1] = N;
        return res;
    }
}
