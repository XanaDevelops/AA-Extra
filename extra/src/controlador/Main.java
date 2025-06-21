package controlador;

import model.CryptHeader;
import model.Dades;
import model.EncriptadorDesencriptador;
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

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

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
    public void eliminarClau(int idClau){
        if(dades.getClaus().size() <= idClau){
            System.err.println("clau no existeix. ignorant");
            return;
        }
        dades.eliminarClau(idClau);
        actualizar();
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
        finestra.finalitzar(id);
        actualizar();
    }

    @Override
    public void encriptar(int id, String kName, String filePath, String outPath, boolean comprimir) {
        EncriptadorDesencriptador ed = new EncriptadorDesencriptador(id, kName);
        executar(id, ed, () -> {ed.encriptar(id, filePath, outPath, comprimir);});
    }

    @Override
    public void desencriptar(int id, String kName, boolean isAuto, String filePath, String outPath) {
        EncriptadorDesencriptador ed = null;
        boolean okAuto = false;
        if(isAuto){
            for(String key: dades.getClaus()){
                ed = new EncriptadorDesencriptador(id, key);
                if(ed.checkKey(key, filePath)){
                    okAuto = true;
                    break;
                }
            }
            if(!okAuto){
                ed = new EncriptadorDesencriptador(id, null);
            }


        }else{
            ed = new EncriptadorDesencriptador(id, kName);
        }
        EncriptadorDesencriptador finalEd = ed;
        executar(id, ed, () -> {
            try {
                finalEd.desencriptar(id, filePath, outPath);
            } catch (CryptHeader.InvalidKeyHeader e) {
                System.err.println(e);
                finestra.finalitzar(id);
                finestra.error("Cap clau és valida!");
            }
        });

    }

    private void executar(int id, Comunicar c, Runnable r){
        procesos.put(id, c);
        executor.execute(r);
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
        if(finestra == null){
            finestra = new Finestra();
        }
        return finestra;
    }
}