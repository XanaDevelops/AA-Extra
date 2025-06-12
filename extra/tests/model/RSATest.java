package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class RSATest {

    RSA rsa;

    @BeforeEach
    void setUp() {
        rsa = new RSA(1000);
    }

    @Test
    void generate() throws ExecutionException, InterruptedException {
        rsa.generate();
        System.out.println(Arrays.toString(rsa.getPublicKey()));
        System.out.println(Arrays.toString(rsa.getPrivateKey()));
    }

    @Test
    void encript() throws ExecutionException, InterruptedException {
        rsa.generate();
        BigInteger test = new BigInteger("1234");
        BigInteger enc = rsa.encript(test);
        BigInteger dec = rsa.decript(enc);
        assertEquals(test, dec);
    }
}