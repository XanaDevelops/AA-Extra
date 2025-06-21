package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class EncriptadorDesencriptadorTest {

    EncriptadorDesencriptador encriptador;
    @BeforeEach
    void setUp() {
        encriptador = new EncriptadorDesencriptador(-1, "TEST_CLAU");
    }

    @Test
    void testEncriptador() throws CryptHeader.InvalidKeyHeader, FileNotFoundException {
        String fileOri = "testsRes/test0.txt";
        String fileDes = "testsRes/test0_dec.txt";
        String fileCry = "testsRes/test0.kri";
        encriptador.encriptar(-1, fileOri, fileCry, false);
        EncriptadorDesencriptador encriptador2 = new EncriptadorDesencriptador(-1, "TEST_CLAU");
        encriptador2.desencriptar(-1, fileCry, fileDes);

        try(BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(new File(fileOri)));
           BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(new File(fileDes)))){
            byte[] a = bis1.readAllBytes();
            byte[] b = bis2.readAllBytes();
            assertArrayEquals(a, b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEncriptador2() throws CryptHeader.InvalidKeyHeader, FileNotFoundException {
        String fileOri = "testsRes/coche tobias zorro.png";
        String fileDes = "testsRes/coche tobias zorro dec.png";
        String fileCry = "testsRes/foto.kri";
        encriptador.encriptar(-1, fileOri, fileCry, false);
        EncriptadorDesencriptador encriptador2 = new EncriptadorDesencriptador(-1, "TEST_CLAU");
        encriptador2.desencriptar(-1, fileCry, fileDes);

        try(BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(new File(fileOri)));
            BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(new File(fileDes)))){
            byte[] a = bis1.readAllBytes();
            byte[] b = bis2.readAllBytes();
            assertArrayEquals(a, b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}