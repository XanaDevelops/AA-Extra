package vista;

import controlador.Main;
import model.Dades;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class GestorClaus extends JDialog {
    public GestorClaus(JFrame parent) {
        super(parent, "Gestor Claus", true);
        this.setLayout(new BorderLayout());
        this.setSize(300, 400);
        this.setLocationRelativeTo(parent);

        JPanel botons = new JPanel();
        JButton btnCrear = new JButton("Crear");
        btnCrear.addActionListener(e -> {
            DialogCrearClau.Resultat r = (new DialogCrearClau(this)).showDialog();
            if (r != null) {
                Main.getInstance().crearClau(Main.getInstance().getDades().getIdCount(), r.nom, r.n);
            }
        });
        botons.add(btnCrear);
        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.addActionListener(e -> {});
        botons.add(btnBorrar);

        botons.setBorder(BorderFactory.createTitledBorder("Opcions"));
        this.add(botons, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane();
        JList<String> llistaClaus = new JList<>();
        DefaultListModel<String> lModel = new DefaultListModel<>();
        for (String x :Main.getInstance().getDades().getClaus()){
            lModel.addElement(x);
        }
        llistaClaus.setModel(lModel);

        scroll.setViewportView(llistaClaus);
        scroll.setBorder(BorderFactory.createTitledBorder("Claus"));

        this.add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }
}
