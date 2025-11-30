package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;
import modelo.Cliente;

import javax.swing.*;
import java.awt.event.*;

public class FirmarContrato extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox usuarioBox;
    private JComboBox contratosDisponiblesBox;
    private JPasswordField claveUsuarioPassword;

    private String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    private String[][] contratosClientes = null;

    public FirmarContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        llenarUsuarios();


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
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
        usuarioBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actualizarDatosContrato();
            }
        });
    }

    private void onOK() {

        if(contratosDisponiblesBox.getItemCount() == 0){
            JOptionPane.showMessageDialog(this, "No hay contratos disponibles para firmar", "Firmar Contrato", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }


        try {
            if (ControladorSistema.getInstancia().firmarContratoCliente(
                    usuarioBox.getSelectedItem().toString(),
                    contratosDisponiblesBox.getSelectedItem().toString(),
                    new String(claveUsuarioPassword.getPassword()))) {

                JOptionPane.showMessageDialog(this, "Contrato Firmado", "Firmar Contrato", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error generico al firmar contrato", "Firmar Contrato", JOptionPane.ERROR_MESSAGE);
            }

        }catch (BancoException e){
            JOptionPane.showMessageDialog(this, "Error al firmar contrato: " + e.getMessage(), "Firmar Contrato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        FirmarContrato dialog = new FirmarContrato();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void llenarUsuarios() throws BancoException {

        if (datosClientes.length == 0){
            throw new BancoException("No existe el cliente");
        }

        for (String[] c : datosClientes){
            usuarioBox.addItem(c[1]);
        }
    }

    private void actualizarDatosContrato() {

        String rutUsuario = usuarioBox.getSelectedItem().toString();

        try {
            contratosClientes = ControladorSistema.getInstancia().listarContratosCliente(rutUsuario);
        } catch (BancoException e) {
            throw new RuntimeException(e);
        }

        contratosDisponiblesBox.removeAllItems();
        contratosDisponiblesBox.setEnabled(true);

        for (String[] contrato : contratosClientes) {
            if (contrato[1].equals("No Firmado")) {
                contratosDisponiblesBox.addItem(contrato[0]);
            }
        }

    }


}
