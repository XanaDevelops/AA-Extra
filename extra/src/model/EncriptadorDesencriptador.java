package model;

import controlador.Comunicar;
import controlador.Main;
import model.Huffman.Compressor;
import model.Huffman.Decompressor;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private boolean aturar = false;

    private int id;

    public EncriptadorDesencriptador(int id, String keyName) {
        rsa = RSA.fromFile(keyName);
        nBytes = (rsa.keyLength()+2)/2;
        this.id = id;
        Main.getInstance().getFinestra().arrancar(id);
    }


    @Override
    public void comunicar(String args) {

    }



    @Override
    public void encriptar(int id, String filePath, String outPath, boolean comprimir) {

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))){
            byte[] fileIn = bis.readAllBytes();

            int _step = fileIn.length / N_THREADS;
            final int step = _step + 1 - (_step % N_THREADS);
            for (int i = 0; i < N_THREADS -1; i++) {
                final int j = i;
                byte[] finalFileIn = fileIn;
                addConcurrent(() -> {
                    encriptar(finalFileIn, j, j * step, (j+1) * step);
                });
            }
            byte[] finalFileIn1 = fileIn;
            addConcurrent(() -> {
                encriptar(finalFileIn1, N_THREADS-1, (N_THREADS-1) * step, finalFileIn1.length);
            });

            waitAll();

            String oriOutPath = outPath;
            if(comprimir) {
                File temp = File.createTempFile("kri_temp", null);
                outPath = temp.getAbsolutePath();

                //Path temp = Files.createTempDirectory("huff_temp");
//                Compressor c = new Compressor(id, filePath, temp.toString());
//                c.compressFile();
//                try(BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream((c.getFileOut())))){
//                    fileIn = bis2.readAllBytes();
//                }
            }

            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath))){
                byte[] header = CryptHeader.createHeader(rsa, comprimir);
                bos.write(header);
                for (ArrayList<Byte> chunk : fileChunksOut) {
                    for(byte b : chunk) {
                        bos.write(b);
                    }
                }
            }

            if(comprimir) {
                Compressor c = new Compressor(-1, outPath, oriOutPath);
                c.compressFile();
            }

            Main.getInstance().finalitzar(id);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void encriptar(byte[] file, int chunck, int ini, int fi){
        Main.getInstance().getFinestra().arrancar(id);

        ArrayList<Byte> auxArr = new ArrayList<>();
        for (int i = ini; i < fi && !aturar; i++) {
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

    public boolean checkKey(String keyName, String filePath) {
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)))){
            byte[] header = bis.readNBytes(CryptHeader.tam);
            RSA rsa = RSA.fromFile(keyName);
            return CryptHeader.checkHeader(rsa, header);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void desencriptar(int id, String filePath, String outPath) throws CryptHeader.InvalidKeyHeader {
        Main.getInstance().getFinestra().arrancar(id);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)))){
            byte[] fileIn = bis.readAllBytes();
            //check header
            if(!CryptHeader.checkHeader(rsa, fileIn)){
                File temp = File.createTempFile("kri_temp", null);
                Decompressor d = new Decompressor(filePath, temp.getAbsolutePath());
                d.decompressFile();
                try(BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(temp))){
                    fileIn = bis2.readAllBytes();
                }
            }

            int _split = ((fileIn.length-CryptHeader.tam)/nBytes)/(N_THREADS);
            final int split = _split*nBytes;
            for (int i = 0; i < N_THREADS-1; i++) {
                int j = i;
                byte[] finalFileIn = fileIn;
                addConcurrent(() -> {
                    desencriptar(finalFileIn, j, j * split, (j+1) * split);
                });
            }
            byte[] finalFileIn1 = fileIn;
            addConcurrent(() -> {
                desencriptar(finalFileIn1, N_THREADS-1, (N_THREADS-1) * split, finalFileIn1.length - CryptHeader.tam);
            });

            waitAll();

            String oriOutPath = outPath;



            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(outPath)))){
                for(ArrayList<Byte> chunk : fileChunksOut) {
                    for(byte b : chunk) {
                        bos.write(b);
                    }
                }
            }



            Main.getInstance().finalitzar(id);

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
        for (int i = ini; i < fi && !aturar; i+=nBytes) {
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
        aturar = true;
    }



    @Override
    public void run() {

    }
}
