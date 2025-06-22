package controlador;

import model.RSA;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    static {
        Main.main(new String[0]);
    }

    @Test
    void test64(){
        generarN(64);
    }
    @Test
    void test128(){
        generarN(128);
    }
    @Test
    void test256(){
        generarN(256);
    }
    @Test
    void test512(){
        generarN(512);
    }
    @Test
    void test1024(){
        generarN(1024);
    }

    void generarN(int n){
        RSA r = new RSA(-1, n, "TEST_KEY"+n);
        try {
            r.generate();
            r.save();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}