package model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CryptHeaderTest {

    @Test
    void createHeader() throws IOException, NoSuchAlgorithmException {
        RSA rsa = RSA.fromFile("TEST_CLAU");


        byte[] header = CryptHeader.createHeader(rsa, false);
        for (int i = 0; i < 3; i++) {
            assertEquals((byte) (CryptHeader.magic >> (16-8*i)) & 0xff, header[i]);
        };
        assertEquals(0, header[3]);
        byte[] checksum = rsa.getChecksumByte();
        for (int i = 0; i < checksum.length; i++) {
            assertEquals(checksum[i], header[i+CryptHeader.checkIndex]);
        };


    }
}