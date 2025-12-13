package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mostrarDatosCliente extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> clienteBox;
    private JComboBox<String> cuentaBox;

    // Componentes gráficos
    private JPanel panelFoto; // Se inicializa en createUIComponents
    private JLabel nroLabel;
    private JLabel fechaLabel;
    private JLabel cvvLabel;

    String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    String[][] cuentasTarjetasDeCl = null;

    public mostrarDatosCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Visualizar Tarjeta");

        try {
            llenarUsuarios();
        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

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

        // Al seleccionar cliente, cargar sus cuentas
        clienteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDatosCuenta();
            }
        });

        // Al cambiar de cuenta en el combo, actualizar la foto en tiempo real (opcional)
        // O dejarlo solo al presionar el botón, como prefieras. Aquí lo puse en el botón OK.
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTarjetaVisual();
            }
        });
    }

    // --- INICIALIZACIÓN DE LA FOTO ---
    private void createUIComponents() {
        // Carga inicial (puedes poner una imagen por defecto o vacía)
        panelFoto = new PanelConFondo("/Recursos/cuenta_rut.png");
    }

    private void onCancel() {
        dispose();
    }

    // --- LÓGICA PRINCIPAL DE CAMBIO DE FOTO Y DATOS ---
    private void actualizarTarjetaVisual() {
        if (clienteBox.getSelectedItem() == null || cuentaBox.getSelectedItem() == null) {
            return;
        }

        String seleccionCuenta = cuentaBox.getSelectedItem().toString(); // Ej: "CUENTA RUT", "CUENTA AHORRO"
        String seleccionCliente = clienteBox.getSelectedItem().toString();

        try {
            String[][] datos = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(seleccionCliente);

            for (String[] d : datos) {
                // d[0] es el Tipo de Cuenta
                if (d[0].equals(seleccionCuenta)) {

                    // 1. Llenar los datos siempre
                    nroLabel.setText(d[1]);   // Número
                    fechaLabel.setText(d[2]); // Fecha
                    cvvLabel.setText(d[3]);   // CVV / Nombre

                    // 2. Lógica de Diferenciación
                    String tipo = seleccionCuenta.toUpperCase();
                    PanelConFondo panel = (PanelConFondo) panelFoto;

                    if (tipo.contains("AHORRO")) {
                        // --- CASO CUENTA AHORRO ---
                        // Ruta de tu imagen en la carpeta Recursos/graficos
                        panel.setImagen("/Recursos/cuenta_ahorro.jpg");

                        // Configuración visual específica de Ahorro
                        nroLabel.setVisible(true);
                        fechaLabel.setVisible(false); // Generalmente no tienen fecha visible al frente
                        cvvLabel.setVisible(false);   // Ni CVV

                        // Ajustar color de texto si es necesario (ej. Negro para fondo claro)
                        nroLabel.setForeground(Color.BLACK);

                    } else if (tipo.contains("RUT")) {
                        // --- CASO CUENTA RUT ---
                        panel.setImagen("/Recursos/cuenta_rut.png");

                        // Configuración visual: Mostrar todo
                        nroLabel.setVisible(true);
                        fechaLabel.setVisible(true);
                        cvvLabel.setVisible(true);

                        nroLabel.setForeground(Color.WHITE); // Texto blanco para fondos oscuros

                    } else if (tipo.contains("CORRIENTE")) {
                        // --- CASO CUENTA CORRIENTE ---
                        panel.setImagen("/Recursos/cuenta_corriente.png");

                        // Configuración visual: Mostrar todo
                        nroLabel.setVisible(true);
                        fechaLabel.setVisible(true);
                        cvvLabel.setVisible(true);

                        nroLabel.setForeground(Color.WHITE);
                    }

                    // Forzar actualización visual
                    panelFoto.repaint();
                    panelFoto.revalidate();
                }
            }
        } catch (BancoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
        if (clienteBox.getSelectedItem() == null) return;

        String rutUsuario = clienteBox.getSelectedItem().toString();
        try {
            cuentasTarjetasDeCl = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(rutUsuario);
        } catch (BancoException e) {
            throw new RuntimeException(e);
        }

        cuentaBox.removeAllItems();
        if (cuentasTarjetasDeCl != null) {
            for (String[] x : cuentasTarjetasDeCl) {
                cuentaBox.addItem(x[0]);
            }
        }
    }

    public static void main(String[] args) {
        mostrarDatosCliente dialog = new mostrarDatosCliente();
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // --- CLASE PARA LA IMAGEN DE FONDO ---
    static class PanelConFondo extends JPanel {
        private Image imagen;

        public PanelConFondo(String ruta) {
            setImagen(ruta);
        }

        public void setImagen(String ruta) {
            var resource = getClass().getResource(ruta);
            if (resource != null) {
                imagen = new ImageIcon(resource).getImage();
                repaint();
            } else {
                // Si falla, podrías poner una imagen de error o imprimir en consola
                System.err.println("No se encontró la imagen: " + ruta);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                // Dibuja la imagen ajustada al tamaño del panel (400x225 aprox para tarjeta)
                g.drawImage(imagen, 0, 0, 400, 225, this);
            }
        }
    }
}