package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.event.*;

public class VincularContrato extends JDialog {
    private JPanel contentPane;
    private JButton generarContratoButton;
    private JButton buttonCancel;
    private JComboBox usuariosBox;
    private JComboBox tipocuentaBox;

    private String[][] listaClientes = ControladorSistema.getInstancia().listarClientes();


    public VincularContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(generarContratoButton);

        cargarDatos();


        generarContratoButton.addActionListener(new ActionListener() {
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

        usuariosBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void onOK() {

        String clienteSeleccionado = (String) usuariosBox.getSelectedItem();
        String tipoCuentaSeleccionada = (String) tipocuentaBox.getSelectedItem();

        if (clienteSeleccionado == null || tipoCuentaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un tipo de cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try{
            ControladorSistema.getInstancia().generarContratoCliente(clienteSeleccionado, tipoCuentaSeleccionada);

            JOptionPane.showMessageDialog(this, "Contrato generado exitosamente para el cliente: " + clienteSeleccionado, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        VincularContrato dialog = new VincularContrato();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void cargarDatos() throws BancoException {

        if(listaClientes.length == 0){
            throw new BancoException("No existen empresas en el registro");
        }
        for (String[] x : listaClientes) {
            usuariosBox.addItem(x[1]);
        }
    }
}