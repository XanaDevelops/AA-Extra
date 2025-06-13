package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class RSATest {

    RSA rsa;
    int N = 256;
    @BeforeEach
    void setUp() {
        rsa = new RSA(-1, N, "TEST_CLAU");
    }

    @Test
    void generate() throws ExecutionException, InterruptedException {
        rsa.generate();
        System.out.println(Arrays.toString(rsa.getPublicKey()));
        System.out.println(Arrays.toString(rsa.getPrivateKey()));
        System.out.println(rsa.getPublicKey()[0].bitLength() +", " + rsa.getPublicKey()[1].bitLength());
        System.out.println(rsa.getPrivateKey()[0].bitLength() +", " + rsa.getPrivateKey()[1].bitLength());
    }

    @Test
    void encript() throws ExecutionException, InterruptedException {
        rsa.generate();
        BigInteger test = new BigInteger("1234");
        BigInteger enc = rsa.encript(test);
        BigInteger dec = rsa.decript(enc);
        assertEquals(test, dec);
    }

    @Test
    public void save() throws ExecutionException, InterruptedException {
        rsa.generate();
        rsa.save();
        RSA rsa2 = RSA.fromFile("TEST_CLAU");
        assertEquals(rsa.getPublicKey()[0], rsa2.getPublicKey()[0]);
        assertEquals(rsa.getPublicKey()[1], rsa2.getPublicKey()[1]);
        assertEquals(rsa.getPrivateKey()[0], rsa2.getPrivateKey()[0]);
        assertEquals(rsa.getNom(), rsa2.getNom());
    }
}