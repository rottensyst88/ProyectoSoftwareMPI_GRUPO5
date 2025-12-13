package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MostrarDatosCliente extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox clienteBox;
    private JComboBox cuentaBox;

    // Aquí usamos la variable, pero se inicializa en createUIComponents
    private JPanel panelFoto;
    private JLabel nroLabel;
    private JLabel fechaLabel;
    private JLabel cvvLabel;

    String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    String[][] cuentasTarjetasDeCl = null;

    public MostrarDatosCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
            llenarUsuarios();

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        clienteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDatosCuenta();
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[][] datos = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(clienteBox.getSelectedItem().toString());

                    for (String[] d : datos) {
                        if (d[0].equals(cuentaBox.getSelectedItem().toString())) {
                            nroLabel.setText(aux_FormatearDatosVentana(d[1]));
                            fechaLabel.setText(d[2]);
                            cvvLabel.setText(d[3]);
                        }
                    }
                } catch (BancoException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // --- MAGIA AQUÍ ---
    // IntelliJ llama a esto automáticamente si marcas "Custom Create" en el .form
    private void createUIComponents() {
        panelFoto = new PanelConFondo("/graficos/fotoTarjeta.jpg");
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        MostrarDatosCliente dialog = new MostrarDatosCliente();
        dialog.setSize(400, 400); // Asegura un tamaño visible
        dialog.pack();
        dialog.setVisible(true);

    }

    // Clase estática interna para el fondo
    static class PanelConFondo extends JPanel {
        private Image imagen;

        public PanelConFondo(String ruta) {
            var resource = getClass().getResource(ruta);
            if (resource != null) {
                imagen = new ImageIcon(resource).getImage();
            } else {
                System.err.println("Error: No se encontró la imagen en " + ruta);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, 400, 225, this);
            }
        }
    }

    private void llenarUsuarios() throws BancoException {

        if (datosClientes.length == 0){
            throw new BancoException("No existe el cliente");
        }

        for (String[] c : datosClientes){
            clienteBox.addItem(c[1]);
        }
    }

    private void actualizarDatosCuenta() {

        String rutUsuario = clienteBox.getSelectedItem().toString();

        try {
            cuentasTarjetasDeCl = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(rutUsuario);
        } catch (BancoException e) {
            throw new RuntimeException(e);
        }

        cuentaBox.removeAllItems();
        cuentaBox.setEnabled(true);

        for (String[] x : cuentasTarjetasDeCl ){
            cuentaBox.addItem(x[0]);
        }
    }

    private String aux_FormatearDatosVentana(String OGText){
        String text = "";
        byte cont = 1;

        char[] a = OGText.toCharArray();

        for (int i = 0; i < a.length; i++){

            text += a[i];

            if (cont == 4){
                text += " ";
                cont = 1;
            }else{
                cont++;
            }
        }

        return text.trim();
    }
}