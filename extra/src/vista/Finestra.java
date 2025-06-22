package vista;

import controlador.Comunicar;
import controlador.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class Finestra extends JFrame implements Comunicar {
    private GestorClaus gestorClaus;

    private Map<Integer, BarraCarrega> barres = new ConcurrentHashMap<>(new TreeMap<>());

    private JPanel barresCarrega;

    public Finestra() {
        super();
        setTitle("Encriptador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 600);
        setLocationRelativeTo(null);

        JPanel botons = new JPanel();

        JButton btnEncriptar = new JButton("Encriptar");
        btnEncriptar.addActionListener(e -> {
            OperModal m = new OperModal(this, true);
            m.setVisible(true);
        });
        botons.add(btnEncriptar);
        JButton btnDesencriptar = new JButton("Desencriptar");
        btnDesencriptar.addActionListener(e -> {
            OperModal m = new OperModal(this, false);
            m.setVisible(true);
        });
        botons.add(btnDesencriptar);
        JButton btnGestor = new JButton("Gestor");
        btnGestor.addActionListener(e -> {
            gestorClaus.setVisible(true);
        });
        botons.add(btnGestor);

        botons.setBorder(BorderFactory.createTitledBorder("Opcions"));
        this.add(botons, BorderLayout.NORTH);

        JPanel arrastrarPanel = new JPanel();
        arrastrarPanel.setLayout(new BorderLayout());

        JPanel panelEncriptador = new JPanel(new BorderLayout());
        JLabel lblEncriptador = new JLabel("Arrosegar aqui per encriptar");
        lblEncriptador.setVerticalAlignment(SwingConstants.CENTER);
        panelEncriptador.add(lblEncriptador, BorderLayout.CENTER);
        panelEncriptador.setTransferHandler(new TransferHandler(){
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
            @SuppressWarnings("unchecked")
            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if(!canImport(support)) {
                    return false;
                }
                try{
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if(!files.isEmpty()) {
                        File file = files.getFirst();
                        OperModal m = new OperModal(Main.getInstance().getFinestra(), true);
                        m.setOriFile(file.getAbsolutePath());
                        m.setVisible(true);
                        return true;
                    }
                } catch (IOException | UnsupportedFlavorException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });
        panelEncriptador.setBorder(BorderFactory.createTitledBorder("Encriptar"));

        JPanel panelDesencriptador = new JPanel(new BorderLayout());
        JLabel lblDesencriptador = new JLabel("Arrosegar aqui per desencriptar");
        lblDesencriptador.setVerticalAlignment(SwingConstants.CENTER);
        panelDesencriptador.add(lblDesencriptador, BorderLayout.CENTER);
        panelDesencriptador.setTransferHandler(new TransferHandler(){
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
            @SuppressWarnings("unchecked")
            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if(!canImport(support)) {
                    return false;
                }
                try{
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if(!files.isEmpty()) {
                        File file = files.getFirst();
                        OperModal m = new OperModal(Main.getInstance().getFinestra(), false);
                        m.setOriFile(file.getAbsolutePath());
                        m.setVisible(true);
                        return true;
                    }
                } catch (IOException | UnsupportedFlavorException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });
        panelDesencriptador.setBorder(BorderFactory.createTitledBorder("Desencriptar"));


        arrastrarPanel.add(panelEncriptador, BorderLayout.WEST);
        arrastrarPanel.add(panelDesencriptador, BorderLayout.EAST);

        this.add(arrastrarPanel, BorderLayout.CENTER);

        barresCarrega = new JPanel();
        barresCarrega.setLayout(new BoxLayout(barresCarrega, BoxLayout.Y_AXIS));
        barresCarrega.setBorder(BorderFactory.createTitledBorder("Procesos"));
        JScrollPane scrollPane = new JScrollPane(barresCarrega);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        this.add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);

        gestorClaus = new GestorClaus(this);
        timer.start();
    }

    @Override
    public void comunicar(String args) {

    }

    @Override
    public void arrancar(int id){
        if(barres.containsKey(id)){
            barres.get(id).iniciar();
        }else{
            BarraCarrega b = new BarraCarrega("Proces", id);
            barres.put(id, b);
            barresCarrega.add(b);
        }

        repaint();
        revalidate();
    }

    @Override
    public void aturar(int id){
        BarraCarrega b = barres.remove(id);
        if(b!=null){
            barresCarrega.remove(b);
        }
        repaint();
        revalidate();

    }

    @Override
    public void finalitzar(int id){
        BarraCarrega b = barres.get(id);
        if(b != null){
            b.end();
        }
    }

    @Override
    public void actualizar() {
        repaint();
        gestorClaus.repaint();
    }

    @Override
    public void error(String msg){
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    Timer timer = new Timer(1000/12, e -> {
        for(Map.Entry<Integer, BarraCarrega> entry: barres.entrySet()){
            entry.getValue().tick();
        }
    });
}
