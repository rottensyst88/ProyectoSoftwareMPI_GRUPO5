package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;


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
        setTitle("Opciones de Persistencia");

        // --- INICIO DE CAMBIOS VISUALES (CON SEGURIDAD) ---
        Color naranjaOscuro = new Color(211, 84, 0);
        Color rojoSalir = new Color(192, 57, 43);

        // Verificamos si los botones existen antes de pintarlos para evitar errores
        if (saveDataButton != null) {
            personalizarBoton(saveDataButton, naranjaOscuro, Color.WHITE);
        }
        if (loadDatosButton != null) {
            personalizarBoton(loadDatosButton, naranjaOscuro, Color.WHITE);
        }
        if (buttonCancel != null) {
            personalizarBoton(buttonCancel, rojoSalir, Color.WHITE);
        }
        if (buttonOK != null) {
            buttonOK.setVisible(false); // Lo ocultamos si existe, ya que no se usa
        }
        // --- FIN DE CAMBIOS VISUALES ---

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


    // --- MÉTODO AUXILIAR PARA ESTILO ---
    private void personalizarBoton(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        // Borde vacío para estilo plano
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });
    }
    // -----------------------------------

    public static void main(String[] args) {
        OpcionesPersistencia dialog = new OpcionesPersistencia();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }
}
