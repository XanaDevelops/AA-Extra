package model;

import controlador.Comunicar;
import controlador.Main;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

public class RSA implements Comunicar, Runnable{

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private BigInteger P, Q, N, FN, E, D;

    private int id;
    private int n;
    private String nom;
    private PrimoProbable prim = new PrimoProbable();

    private boolean aturar = false;


    public RSA(int id, int n, String nom){
        this.id = id;
        this.n = n;
        this.nom = nom;
    }

    @Override
    public void run(){
        Main.getInstance().getFinestra().arrancar(id);
        try {
            generate();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        save();
        Main.getInstance().finalitzar(id);
    }

    public void generate() throws ExecutionException, InterruptedException {
        Main.getInstance().getFinestra().arrancar(id);
        List<Future<BigInteger>> calls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            calls.add(executor.submit(() -> prim.otroProbablePrimo(new BigInteger("1" + "0".repeat(n)))));
        }
        P = calls.get(0).get();
        Q = calls.get(1).get();
        E = calls.get(2).get();

        if(aturar){
            Main.getInstance().getFinestra().finalitzar(id);
            return;
        }

        calls.add(executor.submit(() -> P.multiply(Q)));
        calls.add(executor.submit(() -> P.add(new BigInteger("-1")).multiply(Q.add(new BigInteger("-1")))));
        N = calls.get(3).get();
        FN = calls.get(4).get();

        if (E.compareTo(FN) > -1){
            throw new RuntimeException("E is greater than FN");
        }

        D = E.modInverse(FN);

        Main.getInstance().getFinestra().finalitzar(id);

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

    public void save(){
        if(aturar){
            return;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        try (BufferedWriter out = new BufferedWriter(Files.newBufferedWriter(new File(Dades.KEYSTORE_PATH + "\\" + nom + ".pub").toPath()))) {
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
        try (BufferedWriter out = new BufferedWriter(Files.newBufferedWriter(new File(Dades.KEYSTORE_PATH + "\\" + nom + ".key").toPath()))) {
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

    public static RSA fromFile(String nom){
        RSA rsa = new RSA(-1,-1, nom);
        Base64.Decoder decoder = Base64.getDecoder();
        //TODO: hacer concurrente
        try (BufferedReader in = new BufferedReader(Files.newBufferedReader(new File(Dades.KEYSTORE_PATH + "\\" + nom + ".pub").toPath()))) {
            in.readLine();
            in.readLine();
            rsa.E = new BigInteger(decoder.decode(in.readLine()));
            rsa.N = new BigInteger(decoder.decode(in.readLine()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader in = new BufferedReader(Files.newBufferedReader(new File(Dades.KEYSTORE_PATH + "\\" + nom + ".key").toPath()))) {
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

    public String getNom(){
        return nom;
    }

    public int keyLength(){
        return N.toString().length();
    }

    @Override
    public void comunicar(String args) {

    }

    @Override
    public void aturar(int id){
        aturar = true;
    }

    public static String getChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] getChecksumByte(File file) throws IOException, NoSuchAlgorithmException {
        String checksum = RSA.getChecksum(file);
        byte[] checksumB = new byte[checksum.length()/2];
        for (int i = 0; i < checksum.length() / 2; i++) {
            checksumB[i] = (byte) (short) Short.parseShort(checksum.substring(i * 2, i * 2 + 2), 16);
        }

        return checksumB;
    }

    public String getChecksum() throws IOException, NoSuchAlgorithmException {
        return RSA.getChecksum(new File(Dades.KEYSTORE_PATH+"/"+this.nom+".key"));
    }

    public byte[] getChecksumByte() throws IOException, NoSuchAlgorithmException {
        return RSA.getChecksumByte(new File(Dades.KEYSTORE_PATH+"/"+this.nom+".key"));
    }
}
