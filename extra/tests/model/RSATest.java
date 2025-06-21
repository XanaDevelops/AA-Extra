package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.CryptoPrimitive;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class RSATest {

    RSA rsa;
    int N = 256;
    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        rsa = new RSA(-1, N, "TEST_CLAU");
        rsa.generate();
        rsa.save();
    }

    @Test
    void generate() throws ExecutionException, InterruptedException {

        System.out.println(Arrays.toString(rsa.getPublicKey()));
        System.out.println(Arrays.toString(rsa.getPrivateKey()));
        System.out.println(rsa.getPublicKey()[0].bitLength() +", " + rsa.getPublicKey()[1].bitLength());
        System.out.println(rsa.getPrivateKey()[0].bitLength() +", " + rsa.getPrivateKey()[1].bitLength());
        System.out.println("Longitud N: " + rsa.getPublicKey()[1].toString().length());
        System.out.println("Longitud E: " + rsa.getPublicKey()[0].toString().length());
        System.out.println("Longitud D: " + rsa.getPrivateKey()[0].toString().length());
    }

    @Test
    void encript() throws ExecutionException, InterruptedException {

        BigInteger test = new BigInteger("1234");
        BigInteger enc = rsa.encript(test);
        BigInteger dec = rsa.decript(enc);
        assertEquals(test, dec);
    }

    @Test
    public void save() throws ExecutionException, InterruptedException {

        RSA rsa2 = RSA.fromFile("TEST_CLAU");
        assertEquals(rsa.getPublicKey()[0], rsa2.getPublicKey()[0]);
        assertEquals(rsa.getPublicKey()[1], rsa2.getPublicKey()[1]);
        assertEquals(rsa.getPrivateKey()[0], rsa2.getPrivateKey()[0]);
        assertEquals(rsa.getNom(), rsa2.getNom());
    }

    @Test
    public void testBytes() throws ExecutionException, InterruptedException {

        byte[] string = "hola".getBytes();
        System.out.println(Arrays.toString(string));
        BigInteger[] in = new BigInteger[string.length];
        for (int i = 0; i < string.length; i++) {
            in[i] = rsa.encript(new BigInteger(String.valueOf(string[i])));
        }
        System.out.println(Arrays.toString(in));
        for (BigInteger i : in) {
            System.out.println(i.bitLength());
        }
        BigInteger[] out = new BigInteger[string.length];
        for (int i = 0; i < string.length; i++) {
            out[i] = rsa.decript(in[i]);
        }
        System.out.println(Arrays.toString(out));
    }
    @Test
    public void testBytesArray() throws ExecutionException, InterruptedException {

        int kLen = rsa.keyLength()+2;
        int nBytes = kLen;
        System.out.println("tope: "+nBytes);
        byte[] orig = new byte[256];
        for (int i = 0; i < orig.length; i++) {
            orig[i] = (byte) i;
        }

        ArrayList<Byte> encryptedFile = new ArrayList<>();
        for (byte b: orig) {
            int bb = b;
            if(bb < 0){
                bb = b +256;
            }
            BigInteger aux = new BigInteger(String.valueOf(bb));

            System.out.println(aux);
            byte[] encript = rsa.encript(aux).toByteArray();
            int borrar = 0;
            for (int i = encript.length; i < nBytes; i++) {
                encryptedFile.add((byte) 0);
                borrar++;
            }
            System.out.println("borrar: " + borrar + " keyL" + nBytes);
            for(byte e: encript){

                encryptedFile.add(e);
            }

            assertEquals(0, encryptedFile.size()%nBytes);
        }

        assertEquals(orig.length, encryptedFile.size()/nBytes);
        System.out.println("||||||||| decrypt");
        ArrayList<Byte> decryptedFile = new ArrayList<>();
        int nElems = encryptedFile.size()/nBytes;
        for (int i = 0; i < nElems; i++) {
            List<Byte> elem = encryptedFile.subList(i*nBytes, (i+1)*nBytes);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(elem.size());
            elem.forEach(buf::put);
            byte[] decrypted = buf.array();
            BigInteger dec = rsa.decript(new BigInteger(decrypted));
            System.out.println(dec);
            byte[] decb = dec.toByteArray();
            if(decb.length == 2){
                assertEquals(0, decb[0]);
                assertTrue(decb[1] < 0);
            }
            assertTrue(decb.length <= 2);
            decryptedFile.add(decb[decb.length-1]);
        }
        for (int i = 0; i < orig.length; i++) {
            assertEquals(orig[i], decryptedFile.get(i));
        }
    }

    @Test
    public void checksum() throws IOException, NoSuchAlgorithmException {
        String c = RSA.getChecksum(new File(Dades.KEYSTORE_PATH+"/TEST_CLAU.pub"));
        System.out.println(c);
        System.out.println(c.length());
    }

    @Test
    public void scratch() throws ExecutionException, InterruptedException {



        byte[] bs = new byte[]{30, -123};

        BigInteger neg = new BigInteger(bs);
        System.out.println(neg);
        System.out.println(Arrays.toString(neg.toByteArray()));
        BigInteger encNeg = rsa.encript(neg);
        System.out.println(encNeg);
        BigInteger decNeg = rsa.decript(encNeg);
        System.out.println(decNeg);


    }

    @Test
    public void scratch2() throws ExecutionException, InterruptedException {

        System.out.println(Arrays.toString(new BigInteger("255").toByteArray()));
    }
}