package model;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dades {

    public static final Path KEYSTORE_PATH = Path.of(System.getenv("localappdata")+"/KEYSTORE_PRACTICA_AA/");

    private static int idCount = 0;

    private List<String> claus = new ArrayList<>();

    public Dades(){
        System.out.println(KEYSTORE_PATH);
        if(Files.notExists(KEYSTORE_PATH)){
            try {
                Files.createDirectory(KEYSTORE_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        actualitzarClaus();
        System.err.println(claus);
    }

    public void actualitzarClaus(){
        claus.clear();
        try(Stream<Path> a = Files.walk(KEYSTORE_PATH)) {
            List<Path> files = a.filter(Files::isRegularFile).filter((x) -> x.toString().endsWith(".pub") || x.toString().endsWith(".key")).toList();
            HashSet<String> hash = new HashSet<>();
            System.err.println(files);
            for(Path file : files){
                String name = file.getFileName().toString().split("\\.")[0];

                if (hash.contains(name)){
                    hash.remove(name);
                    claus.add(name);
                }else{
                    hash.add(name);
                }
            }
            if(!hash.isEmpty()){
                System.err.println("CLAUS NO CONSISTENTS!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void eliminarClau(int idClau){
        String aux = claus.get(idClau);
        claus.remove(idClau);
        (new File(KEYSTORE_PATH + "\\" + aux+".pub")).delete();
        (new File(KEYSTORE_PATH + "\\" + aux+".key")).delete();
    }

    public final int getIdCount(){
        return idCount++;
    }

    public final List<String> getClaus(){
        return claus;
    }
}
