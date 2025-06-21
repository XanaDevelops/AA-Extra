package model;

import controlador.Comunicar;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class EncriptadorDesencriptador implements Runnable, Comunicar {

    private final int N_THREADS = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(N_THREADS);
    private ArrayList<Future<?>> runnables = new ArrayList<>();
    private ArrayList<Byte>[] fileChunksOut = new ArrayList[N_THREADS];

    private RSA rsa;

    int nBytes;


    public EncriptadorDesencriptador(String keyName) {
        rsa = RSA.fromFile(keyName);
        nBytes = (rsa.keyLength()+2)/2;

    }


    @Override
    public void comunicar(String args) {

    }



    @Override
    public void encriptar(int id, String filePath, String outPath, boolean comprimir) {
        if(comprimir) {
            System.err.println("TODO: comprimir");
        }

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)))){
            byte[] fileIn = bis.readAllBytes();
            int _step = fileIn.length / N_THREADS;
            final int step = _step + 1 - (_step % N_THREADS);
            for (int i = 0; i < N_THREADS -1; i++) {
                final int j = i;
                addConcurrent(() -> {
                    encriptar(fileIn, j, j * step, (j+1) * step);
                });
            }
            addConcurrent(() -> {
                encriptar(fileIn, N_THREADS-1, (N_THREADS-1) * step, fileIn.length);
            });

            waitAll();
            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(outPath)))){
                byte[] header = CryptHeader.createHeader(rsa, comprimir);
                bos.write(header);
                for (ArrayList<Byte> chunk : fileChunksOut) {
                    for(byte b : chunk) {
                        bos.write(b);
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void encriptar(byte[] file, int chunck, int ini, int fi){
        ArrayList<Byte> auxArr = new ArrayList<>();
        for (int i = ini; i < fi; i++) {
            byte b = file[i];
            int bb = b;
            if(bb < 0){
                bb = b +256;
            }
            BigInteger aux = new BigInteger(String.valueOf(bb));

            byte[] encript = rsa.encript(aux).toByteArray();
            for (int j = encript.length; j < nBytes; j++) {
                auxArr.add((byte) 0);
            }
            for(byte e: encript){

                auxArr.add(e);
            }
        }
        fileChunksOut[chunck] = auxArr;
    }

    @Override
    public void desencriptar(int id, String filePath, String outPath) throws CryptHeader.InvalidKeyHeader {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)))){
            byte[] fileIn = bis.readAllBytes();
            //check header
            if(!CryptHeader.checkHeader(rsa, fileIn)){
                throw new CryptHeader.InvalidKeyHeader();
            }

            int _split = ((fileIn.length-CryptHeader.tam)/nBytes)/(N_THREADS);
            final int split = _split*nBytes;
            for (int i = 0; i < N_THREADS-1; i++) {
                int j = i;
                addConcurrent(() -> {
                    desencriptar(fileIn, j, j * split, (j+1) * split);
                });
            }
            addConcurrent(() -> {
                desencriptar(fileIn, N_THREADS-1, (N_THREADS-1) * split, fileIn.length - CryptHeader.tam);
            });

            waitAll();

            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(outPath)))){
                for(ArrayList<Byte> chunk : fileChunksOut) {
                    for(byte b : chunk) {
                        bos.write(b);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void desencriptar(byte[] file, int chunck, int ini, int fi){
        ini+=CryptHeader.tam;
        fi += CryptHeader.tam;

        ArrayList<Byte> auxArr = new ArrayList<>();
        if(ini >= file.length || fi > file.length){
            fileChunksOut[chunck] = auxArr;
            return;
        }
        for (int i = ini; i < fi; i+=nBytes) {
            byte[] aux = new byte[nBytes];
            for (int j = 0; j < nBytes; j++) {
                aux[j] = file[i+j];
            }
            byte[] dec = rsa.decript(new BigInteger(aux)).toByteArray();
            auxArr.add(dec[dec.length-1]);
        }
        fileChunksOut[chunck] = auxArr;
    }

    private void addConcurrent(Runnable r) {
        runnables.add(executor.submit(r));
    }

    private void waitAll(){
        while(!runnables.isEmpty()){
            try {
                runnables.removeFirst().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void aturar(int id) {
        Comunicar.super.aturar(id);
    }



    @Override
    public void run() {

    }
}
