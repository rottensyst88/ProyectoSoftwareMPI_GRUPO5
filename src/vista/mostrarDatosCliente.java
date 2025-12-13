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

    // Componentes gráficos (Labels que van sobre la foto)
    private JPanel panelFoto;
    private JLabel nroLabel;
    private JLabel fechaLabel;
    private JLabel cvvLabel; // Usamos este label para el NOMBRE

    String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    String[][] cuentasTarjetasDeCl = null;

    public mostrarDatosCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Visualizar Tarjeta");

        // --- 1. PERSONALIZACIÓN DE BOTONES ---
        Color naranjaOscuro = new Color(211, 84, 0);
        Color rojoSalir = new Color(192, 57, 43);

        personalizarBoton(buttonOK, naranjaOscuro, Color.WHITE, false);
        buttonOK.setText("Ver Tarjeta");

        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);
        buttonCancel.setText("Volver");
        // -------------------------------------

        try {
            llenarUsuarios();
        } catch (BancoException e) {
            // El manejo de errores críticos se hace ahora desde el main o validación previa
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

        clienteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDatosCuenta();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTarjetaVisual();
            }
        });
    }

    // --- VALIDACIÓN DE APERTURA ---
    public boolean validarEstadoSistema() {
        if (datosClientes == null || datosClientes.length == 0) {
            JOptionPane.showMessageDialog(null, "No existen clientes registrados en el sistema.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean hayCuentasActivas = false;
        for (String[] cliente : datosClientes) {
            String rut = cliente[1];
            try {
                String[][] cuentas = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(rut);
                if (cuentas != null && cuentas.length > 0) {
                    hayCuentasActivas = true;
                    break;
                }
            } catch (Exception e) {
                // Ignorar error individual, seguir buscando
            }
        }

        if (!hayCuentasActivas) {
            JOptionPane.showMessageDialog(null, "No existen cuentas activas (con contrato firmado) para mostrar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }

    // --- INICIALIZACIÓN DE LA FOTO (SOLUCIÓN DE VISIBILIDAD) ---
    private void createUIComponents() {
        panelFoto = new PanelConFondo("/Recursos/cuenta_rut.png");
        panelFoto.setLayout(null); // Permite posicionamiento manual (X, Y)

        // SOLUCIÓN: Forzar la instanciación de los labels y agregarlos al panel.
        nroLabel = new JLabel();
        fechaLabel = new JLabel();
        cvvLabel = new JLabel();

        int anchoTotal = 300;
        int altoLinea = 20;

        // Establecer un tamaño inicial para que el layout(null) los muestre
        nroLabel.setBounds(30, 120, anchoTotal, altoLinea);
        fechaLabel.setBounds(140, 150, 100, altoLinea);
        cvvLabel.setBounds(30, 185, anchoTotal, altoLinea);

        panelFoto.add(nroLabel);
        panelFoto.add(fechaLabel);
        panelFoto.add(cvvLabel);
    }

    private void onCancel() {
        dispose();
    }

    // --- LÓGICA DE VISUALIZACIÓN Y POSICIONAMIENTO ---
    private void actualizarTarjetaVisual() {
        if (clienteBox.getSelectedItem() == null || cuentaBox.getSelectedItem() == null) {
            return;
        }

        String seleccionCuenta = cuentaBox.getSelectedItem().toString();
        String seleccionCliente = clienteBox.getSelectedItem().toString();

        String nombreCompleto = obtenerNombrePorRut(seleccionCliente);

        try {
            String[][] datos = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(seleccionCliente);

            for (String[] d : datos) {
                if (d[0].equals(seleccionCuenta)) {

                    String numeroRaw = d[1];
                    String fechaRaw = d[2];

                    String tipo = seleccionCuenta.toUpperCase();
                    PanelConFondo panel = (PanelConFondo) panelFoto;

                    // 1. CONFIGURACIÓN VISUAL COMÚN
                    nroLabel.setVisible(true);
                    fechaLabel.setVisible(true);
                    cvvLabel.setVisible(true);

                    // Requerimiento: TODO EL TEXTO DEBE SER NEGRO
                    nroLabel.setForeground(Color.BLACK);
                    fechaLabel.setForeground(Color.BLACK);
                    cvvLabel.setForeground(Color.BLACK);

                    // Tipografía
                    Font fuenteTarjeta = new Font("SansSerif", Font.BOLD, 14);
                    nroLabel.setFont(fuenteTarjeta);
                    fechaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    cvvLabel.setFont(fuenteTarjeta);

                    // Establecer el Nombre del titular (Requerimiento)
                    cvvLabel.setText(nombreCompleto.toUpperCase());

                    // 2. CONFIGURACIÓN ESPECÍFICA POR TIPO

                    if (tipo.contains("AHORRO")) {
                        // ** CUENTA AHORRO - Requerimiento: Solo mostrar el número y nombre **
                        panel.setImagen("/Recursos/cuenta_ahorro.jpg");

                        // Texto: Solo el número de cuenta y la fecha (el nombre va en cvvLabel)
                        nroLabel.setText(numeroRaw);
                        fechaLabel.setText("FECHA: " + fechaRaw);

                        // POSICIÓN: Ajustada para el diseño vertical de Ahorro
                        nroLabel.setLocation(40, 100);
                        cvvLabel.setLocation(40, 150); // El nombre va un poco más abajo

                        // Ocultar fecha (solo mostramos número y nombre)
                        fechaLabel.setVisible(false);

                    } else if (tipo.contains("RUT")) {
                        // ** CUENTA RUT **
                        panel.setImagen("/Recursos/cuenta_rut.png");

                        // Texto: Formato Tarjeta (con espacios) y Fecha
                        nroLabel.setText(formatearNumeroTarjeta(numeroRaw));
                        fechaLabel.setText("VALIDA HASTA: " + fechaRaw);

                        // POSICIÓN: Ajustada
                        nroLabel.setLocation(30, 120);
                        fechaLabel.setLocation(140, 150);
                        cvvLabel.setLocation(30, 185);

                    } else if (tipo.contains("CORRIENTE")) {
                        // ** CUENTA CORRIENTE **
                        panel.setImagen("/Recursos/cuenta_corriente.png");

                        // Texto: Formato Tarjeta (con espacios) y Fecha
                        nroLabel.setText(formatearNumeroTarjeta(numeroRaw));
                        fechaLabel.setText("VALIDA HASTA: " + fechaRaw);

                        // POSICIÓN: Ajustada
                        nroLabel.setLocation(40, 130);
                        fechaLabel.setLocation(40, 155);
                        cvvLabel.setLocation(40, 180);
                    }

                    // Forzar actualización
                    panelFoto.repaint();
                    panelFoto.revalidate();
                }
            }
        } catch (BancoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    /**
     * Busca el nombre completo de un cliente usando su RUT.
     * @param rut El RUT a buscar.
     */
    private String obtenerNombrePorRut(String rut) {
        if (datosClientes == null) return "NOMBRE NO DISPONIBLE";

        // Asumiendo que datosClientes es [Nombre completo (0), Rut (1), Dirección (2), Clave (3)]
        for (String[] cliente : datosClientes) {
            if (cliente.length > 1 && cliente[1].equals(rut)) {
                return cliente[0]; // Retorna el Nombre Completo (índice 0)
            }
        }
        return "NOMBRE NO ENCONTRADO";
    }

    // Método para poner espacios cada 4 dígitos: 12345678 -> 1234 5678
    private String formatearNumeroTarjeta(String numero) {
        if (numero == null) return "";
        String limpio = numero.replace(" ", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < limpio.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append("  ");
            }
            sb.append(limpio.charAt(i));
        }
        return sb.toString();
    }

    // --- CARGA DE DATOS Y ESTILO (Métodos auxiliares) ---
    private void llenarUsuarios() throws BancoException {
        if (datosClientes == null || datosClientes.length == 0) return;
        clienteBox.removeAllItems();
        for (String[] c : datosClientes){
            clienteBox.addItem(c[1]);
        }
        clienteBox.setSelectedIndex(-1);
        cuentaBox.removeAllItems();
        cuentaBox.setEnabled(false);
    }

    private void actualizarDatosCuenta() {
        if (clienteBox.getSelectedItem() == null) return;
        String rutUsuario = clienteBox.getSelectedItem().toString();
        try {
            cuentasTarjetasDeCl = ControladorSistema.getInstancia().listarCuentasYTarjetasCliente(rutUsuario);
        } catch (BancoException e) {
            cuentaBox.removeAllItems();
            cuentaBox.setEnabled(false);
            return;
        }
        cuentaBox.removeAllItems();
        if (cuentasTarjetasDeCl != null) {
            for (String[] x : cuentasTarjetasDeCl) {
                cuentaBox.addItem(x[0]);
            }
            cuentaBox.setEnabled(true);
        }
    }

    private void personalizarBoton(JButton boton, Color fondo, Color texto, boolean esMini) {
        if (boton == null) return;
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(esMini ? 8 : 10, esMini ? 15 : 20, esMini ? 8 : 10, esMini ? 15 : 20));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(fondo.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { boton.setBackground(fondo); }
        });
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) break;
            }
        } catch (Exception e) {}
        mostrarDatosCliente dialog = new mostrarDatosCliente();
        if (dialog.validarEstadoSistema()) {
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        } else {
            dialog.dispose();
        }
    }

    static class PanelConFondo extends JPanel {
        private Image imagen;
        public PanelConFondo(String ruta) { setImagen(ruta); }
        public void setImagen(String ruta) {
            var resource = getClass().getResource(ruta);
            if (resource != null) {
                imagen = new ImageIcon(resource).getImage();
                repaint();
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) g.drawImage(imagen, 0, 0, 400, 225, this);
        }
    }
}