package model;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
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

    public void save(String name){
        Base64.Encoder encoder = Base64.getEncoder();
        try (BufferedWriter out = new BufferedWriter(Files.newBufferedWriter(new File(Dades.KEYSTORE_PATH + "\\" + name + ".pub").toPath()))) {
            out.write("----BEGIN PUBLIC KEY----");out.newLine();
            out.newLine();
            out.write(encoder.encodeToString(getPublicKey()[0].toByteArray()));
            out.newLine();
            out.write(encoder.encodeToString(getPublicKey()[1].toByteArray()));
            out.newLine();out.newLine();
            out.write("----END PUBLIC KEY----");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter out = new BufferedWriter(Files.newBufferedWriter(new File(Dades.KEYSTORE_PATH + "\\" + name + ".key").toPath()))) {
            out.write("----BEGIN PRIVATE KEY----");
            out.newLine();out.newLine();
            out.write(encoder.encodeToString(getPrivateKey()[0].toByteArray()));
            out.newLine();
            out.write(encoder.encodeToString(getPrivateKey()[1].toByteArray()));
            out.newLine();out.newLine();
            out.write("----END PRIVATE KEY----");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static RSA fromFile(String pub, String priv){
        RSA rsa = new RSA(-1);
        Base64.Decoder decoder = Base64.getDecoder();
        try (BufferedReader in = new BufferedReader(Files.newBufferedReader(new File(pub).toPath()))) {
            in.readLine();
            in.readLine();
            rsa.E = new BigInteger(decoder.decode(in.readLine()));
            rsa.N = new BigInteger(decoder.decode(in.readLine()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader in = new BufferedReader(Files.newBufferedReader(new File(priv).toPath()))) {
            in.readLine();
            in.readLine();
            rsa.D = new BigInteger(decoder.decode(in.readLine()));
            BigInteger aux = new BigInteger(decoder.decode(in.readLine()));
            if (aux.compareTo(rsa.N) != 0){
                throw new RuntimeException("N no es igual!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rsa;
    }
}
