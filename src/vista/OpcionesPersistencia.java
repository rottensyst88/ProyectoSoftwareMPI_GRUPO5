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
                try{
                    ControladorSistema.getInstancia().saveControlador();
                } catch (BancoException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dispose();
                }
            }
        });
        loadDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    ControladorSistema.getInstancia().readDatosSistema();
                    JOptionPane.showMessageDialog(null, "Datos cargados correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                } catch (BancoException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dispose();
                }
            }
        });
    }

    public static void main(String[] args) {
        OpcionesPersistencia dialog = new OpcionesPersistencia();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }
}
