package vista;

import javax.swing.*;
import java.awt.*;

public class DialogCrearClau extends JDialog {

    private boolean confirm = false;
    private JSpinner spinner;
    private JTextField textField;

    public static class Resultat{
        public int n;
        public String nom;
        public Resultat(int n, String nom){
            this.n = n;
            this.nom = nom;
        }
    }

    public DialogCrearClau(Window parent) {
        super(parent, "Crear Clau", ModalityType.APPLICATION_MODAL);
        //this.setSize(200, 150);

        JPanel dades = new JPanel();
        dades.setLayout(new BoxLayout(dades, BoxLayout.Y_AXIS));
        spinner = new JSpinner(new SpinnerNumberModel(1024, 1, Integer.MAX_VALUE, 1));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(10);
        dades.add(spinner);
        textField = new JTextField();
        dades.add(textField);

        this.add(dades, BorderLayout.NORTH);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(e -> {confirm = true; setVisible(false);});

        this.add(btnOk, BorderLayout.SOUTH);

        pack();
        this.setLocationRelativeTo(parent);

    }

    public Resultat showDialog(){
        this.setVisible(true);
        if(confirm){
            return new Resultat((Integer)spinner.getValue(), textField.getText());
        }
        return null;
    }
}
