package vista;

import model.RSA;

import javax.swing.*;
import java.awt.*;

public class InfoClauModal extends JDialog {

    public InfoClauModal(Window parent, String keyName) {
        super(parent, "Info " +keyName, ModalityType.MODELESS);
        JPanel panel = new JPanel();
        RSA rsa = RSA.fromFile(keyName);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Para centrar verticalmente, añadimos glue antes y después
        panel.add(Box.createVerticalGlue());

        JLabel infoLabel = new JLabel("informació de la clau");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(infoLabel);

        // Tamaños obtenidos aparte:
        int tamanyE = rsa.getPublicKey()[0].toString().length();
        JLabel labelE = new JLabel("Tamany E: "  + tamanyE );
        labelE.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelE);

        int tamanyN =  rsa.getPublicKey()[1].toString().length();
        JLabel labelN = new JLabel("Tamany N: " + tamanyN);
        labelN.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelN);

        int tamanyD = rsa.getPrivateKey()[0].toString().length();
        JLabel labelD = new JLabel("Tamany D: " + tamanyD);
        labelD.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelD);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton okButton = new JButton("OK");
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> this.dispose());
        panel.add(okButton);

        panel.add(Box.createVerticalGlue());

        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }
}
