package controlador;

import model.Dades;
import model.RSA;
import vista.Finestra;

import javax.swing.*;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main implements Comunicar{
    private static Main instance;
    private final Dades dades;
    private Finestra finestra;

    private final TreeMap<Integer, Comunicar> procesos = new TreeMap<>();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);

    public static void main(String[] args) {
        if (instance == null) {
            instance = new Main();
        }
    }

    private Main(){
        dades = new Dades();
        SwingUtilities.invokeLater(() -> finestra = new Finestra());
    }

    @Override
    public void crearClau(int idCount, String nom, int n){
        executar(idCount, new RSA(idCount, n, nom));
    }

    @Override
    public void aturar(int id){
        procesos.get(id).aturar(-1);
        procesos.remove(id);
        actualizar();
    }

    @Override
    public void actualizar(){
        dades.actualitzarClaus();
        finestra.actualizar();
    }

    @Override
    public void finalitzar(int id){
        procesos.remove(id);
        actualizar();
    }

    private void executar(int id, Runnable r){
        procesos.put(id, (Comunicar) r);
        executor.submit(r);
    }

    @Override
    public void comunicar(String args) {
        System.err.println(args);
    }

    public static Main getInstance(){
        return instance;
    }

    public final Dades getDades(){
        return dades;
    }
    public final Finestra getFinestra(){
        return finestra;
    }
}