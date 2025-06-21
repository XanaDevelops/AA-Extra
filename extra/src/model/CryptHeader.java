package model;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class CryptHeader {
    public static final int magic = 0x444756;



    public static final int magicIndex;
    public static final int compressIndex;
    public static final int checkIndex;


    public static int tam = 0;
    static {
        tam = 0;
        magicIndex = tam;
        tam+=3;
        compressIndex = tam;
        tam += 1;
        checkIndex = tam;
        tam+=32;

    }

    public static boolean checkHeader(RSA rsa, byte[] header){
        if(rsa == null){
            return false;
        }
        try {
            byte[] checksum = rsa.getChecksumByte();
            for (int i = 0; i < checksum.length; i++) {
                if (checksum[i] != header[i+checkIndex]) {
                    return false;
                }
            }
            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isCompressed(byte[] file){
        return file[compressIndex] == (byte) 0xFF;
    }


    public static byte[] createHeader(RSA rsa, boolean isCompressed) {
        try {
            byte[] checkSum = rsa.getChecksumByte();
            byte[] header = new byte[tam];
            for (int i = 0; i < 3; i++) {
                header[i] = (byte) (magic >> (16-i*8) & 0xFF);
            }
            header[compressIndex] = isCompressed? (byte) 0xFF : (byte) 0x00;
            System.arraycopy(checkSum, 0, header, checkIndex, checkSum.length);

            return header;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static class InvalidKeyHeader extends Exception {
        public InvalidKeyHeader() {
            super();
        }
    }
}
