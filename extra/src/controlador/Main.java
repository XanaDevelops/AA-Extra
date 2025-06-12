package controlador;

import model.Dades;
import vista.Finestra;

import javax.swing.*;

public class Main implements Comunicar{
    private static Main instance;
    private final Dades dades;
    private Finestra finestra;

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
    public void comunicar(String args) {
        System.err.println(args);
    }

    public final Main getInstance(){
        return instance;
    }

    public final Dades getDades(){
        return dades;
    }
    public final Finestra getFinestra(){
        return finestra;
    }
}