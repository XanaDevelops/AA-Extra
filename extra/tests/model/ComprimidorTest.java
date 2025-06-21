package model;

import controlador.Main;
import model.Huffman.Compressor;
import model.Huffman.Decompressor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ComprimidorTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Main.main(null);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCompresor() throws IOException {
        String fileName = "tests/res/testABC.txt";

        String outputFolder = "tests/res/";
        Compressor c = new Compressor(-1, fileName, outputFolder);
        c.compressFile();
        Decompressor d = new Decompressor("tests/res/testABC.kib", outputFolder);
        d.decompressFile();
        //comparar
        BufferedInputStream inA = new BufferedInputStream(new FileInputStream(fileName)),
                inB = new BufferedInputStream(new FileInputStream(  "tests/res/testABC.txt"));
        byte[] a = inA.readAllBytes();

        byte[] b = inB.readAllBytes();
        assertArrayEquals(a, b);
    }
    @Test
    void testCompresorBig() throws IOException {
        String fileName = "tests/res/big.txt";

        String outputFolder = "tests/res/";
        Compressor c = new Compressor(-1, fileName, outputFolder);
        c.compressFile();
        Decompressor d = new Decompressor("tests/res/big.kib", outputFolder);
        d.decompressFile();
        //comparar
        BufferedInputStream inA = new BufferedInputStream(new FileInputStream(fileName)),
                inB = new BufferedInputStream(new FileInputStream(  "tests/res/orig_big.txt"));
        byte[] a = inA.readAllBytes();

        byte[] b = inB.readAllBytes();
        assertArrayEquals(a, b);
    }
}