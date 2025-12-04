package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OpcionesPersistencia extends JDialog implements Serializable {
    private JPanel contentPane;
    private JButton saveDataButton;
    private JButton loadDatosButton;
    private JButton buttonOK;
    private JButton buttonCancel;

    public OpcionesPersistencia() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        saveDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControladorSistema.getInstancia().saveControlador();
            }
        });
        loadDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControladorSistema.getInstancia().readDatosSistema();
            }
        });
    }

    public static void main(String[] args) {
        OpcionesPersistencia dialog = new OpcionesPersistencia();
        dialog.pack();
        dialog.setVisible(true);
    }
}
