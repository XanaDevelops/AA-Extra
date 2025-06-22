package controlador;

import model.CryptHeader;
import model.EncriptadorDesencriptador;
import model.RSA;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ComunicarTest {

    static{
        Main.main(new String[0]);
    }

    String fileOri = "testsRes/coche tobias zorro.png";
    String fileDes = "testsRes/coche.png";
    String fileCry = "testsRes/coche.kri";
    String fileCryC = "testsRes/coche.krib";

    @Test
    @Order(1)
    void test064(){
        enc(64, false);
    }
    @Test
    @Order(2)
    void test064c(){
        dec(64);
    }
    @Test
    @Order(3)
    void test128(){
        enc(128, false);
    }
    @Test
    @Order(4)
    void test128c(){
        dec(128);
    }
    @Test
    @Order(5)
    void test256(){
        enc(256, false);
    }
    @Test
    @Order(6)
    void test256c(){
        dec(256);
    }
    @Test
    @Order(7)
    void test512(){
        enc(512, false);
    }
    @Test
    @Order(8)
    void test512c(){
        dec(512);
    }


    @Test
    @Order(11)
    void dtest064(){
        enc(64, true);
    }
    @Test
    @Order(12)
    void dtest064c(){
        dec(64);
    }
    @Test
    @Order(13)
    void dtest128(){
        enc(128, true);
    }
    @Test
    @Order(14)
    void dtest128c(){
        dec(128);
    }
    @Test
    @Order(15)
    void dtest256(){
        enc(256, true);
    }
    @Test
    @Order(16)
    void dtest256c(){
        dec(256);
    }
    @Test
    @Order(17)
    void dtest512(){
        enc(512, true);
    }
    @Test
    @Order(18)
    void dtest512c(){
        dec(512);
    }


    void enc(int n, boolean c){
        EncriptadorDesencriptador e = new EncriptadorDesencriptador(-1, "TEST_KEY"+n);
        e.encriptar(-1, fileOri, fileCryC, c);
    }

    void dec(int n){
        EncriptadorDesencriptador e = new EncriptadorDesencriptador(-1, "TEST_KEY"+n);
        try {
            e.desencriptar(-1, fileCryC, fileDes);
        } catch (CryptHeader.InvalidKeyHeader ex) {
            throw new RuntimeException(ex);
        }
    }

}