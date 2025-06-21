package model;

import controlador.Main;
import model.Huffman.Compressor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
class EncriptadorDesencriptadorTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Main.main(null);

    }

    EncriptadorDesencriptador encriptador;
    @BeforeEach
    void setUp() {
        Main.main(null);
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
        String fileOri = "testsRes/luigi gana.jpg";
        String fileDes = "testsRes/luigi gana dec.jpg";
        String fileCry = "testsRes/fotoL.kri";
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
    void testEncriptador3() throws CryptHeader.InvalidKeyHeader, FileNotFoundException {
        String fileOri = "testsRes/test0.txt";
        String fileDes = "testsRes/test0_dec_c.txt";
        String fileCry = "testsRes/test0_c.kri";
        encriptador.encriptar(-1, fileOri, fileCry, true);
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
    void testEncriptador4() throws CryptHeader.InvalidKeyHeader, FileNotFoundException {
        String fileOri = "testsRes/luigi gana.jpg";
        String fileDes = "testsRes/luigi gana dec_c.jpg";
        String fileCry = "testsRes/fotoL_c.kri";
        encriptador.encriptar(-1, fileOri, fileCry, true);
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
    void testEncriptador5() throws CryptHeader.InvalidKeyHeader, IOException {
        Compressor c = new Compressor(-1, "testsRes/fotoL.kri", "testsRes/");
        c.compressFile();
    }

    @Test
    void testEncriptador6() throws CryptHeader.InvalidKeyHeader, IOException {
        Compressor c = new Compressor(-1, "testsRes/test0.kri", "testsRes/");
        c.compressFile();
    }

}