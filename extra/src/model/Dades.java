package model;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Dades {

    public static final Path KEYSTORE_PATH = Path.of(System.getenv("localappdata")+"\\KEYSTORE_PRACTICA_AA\\");

    private static int idCount = 0;

    public Dades(){
        System.out.println(KEYSTORE_PATH);
        if(Files.notExists(KEYSTORE_PATH)){
            try {
                Files.createDirectory(KEYSTORE_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final int getIdCount(){
        return idCount++;
    }
}
