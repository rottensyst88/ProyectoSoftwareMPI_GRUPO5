package vista;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.event.*;
import controlador.ControladorSistema;
import excepcion.BancoException;

public class CrearCliente extends JDialog {
    private JPanel contentPane;
    private JButton aceptarButton;
    private JButton salirButton;
    private JTextField nombreField;
    private JTextField rutField;
    private JTextField domicilioField;

    public CrearCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(aceptarButton);

        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        salirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

        String nombre = nombreField.getText();
        String rut = rutField.getText();
        String domicilio = domicilioField.getText();

        try {
            ControladorSistema.getInstancia().crearCliente(nombre, rut, domicilio);
            JOptionPane.showMessageDialog(this,
                    "Cliente creado exitosamente",
                    "Crear Cliente",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear cliente: " + e.getMessage(),
                    "Crear Cliente",
                    JOptionPane.ERROR_MESSAGE);
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CrearCliente dialog = new CrearCliente();
        dialog.pack();
        dialog.setVisible(true);
    }
}
