package vista;

import controlador.Main;
import model.Dades;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class OperModal extends JDialog {

    boolean isEncrypt = true;

    private JTextField oriFileField;
    private JButton browseExistingButton;

    private JTextField saveFileField;
    private JButton browseArbitraryButton;

    private JComboBox<String> comboBox;
    private JCheckBox checkBox;

    private JButton okButton;
    private JButton cancelButton;

    private boolean confirmed = false;

    private Dades dades;

    public OperModal(Frame frame, boolean isEncrypt) {
        super(frame,  isEncrypt ? "Encriptar" : "Desencriptar", ModalityType.APPLICATION_MODAL);
        this.isEncrypt = isEncrypt;

        this.dades = Main.getInstance().getDades();

        oriFileField = new JTextField(25);
        browseExistingButton = new JButton("...");
        browseExistingButton.addActionListener(e -> onBrowseOrig());

        saveFileField = new JTextField(25);
        browseArbitraryButton = new JButton("...");
        browseArbitraryButton.addActionListener(e -> onBrowseSave());

        comboBox = new JComboBox<>();
        if(!isEncrypt){
            comboBox.addItem("Automàtic");
        }
        for(String s: dades.getClaus()){
            comboBox.addItem(s);
        }
        checkBox = new JCheckBox("Comprimir");

        okButton = new JButton("OK");
        okButton.addActionListener(e -> onOK());

        cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> onCancel());

        JPanel filePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        filePanel.add(new JLabel("Origen:"), gbc);
        gbc.gridx = 1;
        filePanel.add(oriFileField, gbc);
        gbc.gridx = 2;
        filePanel.add(browseExistingButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        filePanel.add(new JLabel("Destí:"), gbc);
        gbc.gridx = 1;
        filePanel.add(saveFileField, gbc);
        gbc.gridx = 2;
        filePanel.add(browseArbitraryButton, gbc);

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        optionsPanel.add(new JLabel("Claus:"));
        optionsPanel.add(comboBox);
        if (isEncrypt) {
            optionsPanel.add(checkBox);
        }


        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(filePanel, BorderLayout.CENTER);
        contentPane.add(optionsPanel, BorderLayout.NORTH);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);

        setPreferredSize(new Dimension(600, 200));

        pack();
        setLocationRelativeTo(frame);
    }

    private void onBrowseOrig() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (!isEncrypt) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Arxius encriptats .kri", "kri");
            chooser.setFileFilter(filter);
        }
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected.exists() && selected.isFile()) {
                oriFileField.setText(selected.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un arxiu existent",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void onBrowseSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (isEncrypt) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Arxius encriptats .kri", "kri");
            chooser.setFileFilter(filter);
        }
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            String path = selected.getAbsolutePath();
            if (isEncrypt && !path.toLowerCase().endsWith(".kri")) {
                path += ".kri";
            }
            saveFileField.setText(path);
        }
    }

    private void onOK() {
        String path = oriFileField.getText().trim();
        if (path.isEmpty() || !(new File(path).exists() && new File(path).isFile())) {
            JOptionPane.showMessageDialog(this,
                    "L'Origen ha d'existir",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        confirmed = true;
        setVisible(false);
    }

    private void onCancel() {
        confirmed = false;
        setVisible(false);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public File getOriFile() {
        String path = oriFileField.getText().trim();
        return path.isEmpty() ? null : new File(path);
    }
    public void setOriFile(String path) {
        oriFileField.setText(path);
    }

    public File getSaveFile() {
        String path = saveFileField.getText().trim();
        return path.isEmpty() ? null : new File(path);
    }

    public String getSelectedOption() {
        return (String) comboBox.getSelectedItem();
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    private void loadEncript(){

    }

    private void loadDecript(){}
}
