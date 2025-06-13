package vista;

import controlador.Comunicar;

import javax.swing.*;
import java.awt.*;

public class Finestra extends JFrame implements Comunicar {
    private GestorClaus gestorClaus;

    public Finestra() {
        super();
        setTitle("Encriptador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 800);
        setLocationRelativeTo(null);

        JPanel botons = new JPanel();

        JButton btnEncriptar = new JButton("Encriptar");
        btnEncriptar.addActionListener(e -> {
            System.err.println("encriptar");
        });
        botons.add(btnEncriptar);
        JButton btnDesencriptar = new JButton("Desencriptar");
        btnDesencriptar.addActionListener(e -> {
            System.err.println("desencriptar");
        });
        botons.add(btnDesencriptar);
        JButton btnGestor = new JButton("Gestor");
        btnGestor.addActionListener(e -> {
            gestorClaus.setVisible(true);
        });
        botons.add(btnGestor);

        botons.setBorder(BorderFactory.createTitledBorder("Opcions"));
        this.add(botons, BorderLayout.NORTH);


        setVisible(true);

        gestorClaus = new GestorClaus(this);
    }

    @Override
    public void comunicar(String args) {

    }

    @Override
    public void actualizar() {
        repaint();
        gestorClaus.repaint();
    }
}
