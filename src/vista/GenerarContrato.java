package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerarContrato extends JDialog {
    private JPanel contentPane;
    private JButton generarContratoButton;
    private JButton buttonCancel;
    private JComboBox<String> usuariosBox;
    private JComboBox<String> tipocuentaBox;
    private JLabel foto;

    private String[][] listaClientes = ControladorSistema.getInstancia().listarClientes();

    // Definimos los tipos de cuenta que el sistema soporta
    private final String[] TIPOS_CUENTA_TOTALES = {"CUENTA RUT", "CUENTA CORRIENTE", "CUENTA AHORRO"};

    public GenerarContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(generarContratoButton);
        setTitle("Generar Nuevo Contrato");

        // --- ESTILO VISUAL ---
        Color naranjaOscuro = new Color(211, 84, 0);
        personalizarBoton(generarContratoButton, naranjaOscuro, Color.WHITE, false);

        Color rojoSalir = new Color(192, 57, 43);
        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);

        cargarFoto();

        // 1. CARGAR USUARIOS FILTRADOS (Solo los que les falta algo)
        try {
            cargarDatos();
        } catch (BancoException e) {
            // Se maneja validación al abrir
        }

        generarContratoButton.addActionListener(e -> onOK());
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

        // Cuando cambia el usuario, actualizamos qué contratos le faltan
        usuariosBox.addActionListener(e -> actualizarTiposDeContrato());

        // Selección inicial
        if (usuariosBox.getItemCount() > 0) {
            usuariosBox.setSelectedIndex(0);
            actualizarTiposDeContrato();
        }
    }

    private void onOK() {
        if (usuariosBox.getSelectedItem() == null || tipocuentaBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un tipo de cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clienteSeleccionado = (String) usuariosBox.getSelectedItem();
        String tipoCuentaSeleccionada = (String) tipocuentaBox.getSelectedItem();

        try {
            ControladorSistema.getInstancia().generarContratoCliente(clienteSeleccionado, tipoCuentaSeleccionada);

            JOptionPane.showMessageDialog(this, "Contrato generado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // --- CAMBIO IMPORTANTE ---
            // Recargamos la lista de usuarios.
            // Si el cliente completó todos sus contratos, cargarDatos() YA NO LO AGREGARÁ.
            cargarDatos();

            // Si después de recargar todavía quedan clientes, actualizamos el combo de contratos
            if (usuariosBox.getItemCount() > 0) {
                actualizarTiposDeContrato();
            } else {
                // Si la lista quedó vacía, cerramos
                JOptionPane.showMessageDialog(this, "No quedan clientes con contratos pendientes de generar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    // --- 2. FILTRO DE CONTRATOS (Para el segundo ComboBox) ---
    private void actualizarTiposDeContrato() {
        if (usuariosBox.getSelectedItem() == null) {
            tipocuentaBox.removeAllItems();
            return;
        }

        String rutCliente = usuariosBox.getSelectedItem().toString();
        List<String> contratosYaExistentes = new ArrayList<>();

        try {
            String[][] data = ControladorSistema.getInstancia().listarContratosCliente(rutCliente);
            if (data != null) {
                for (String[] fila : data) {
                    contratosYaExistentes.add(fila[0].toUpperCase());
                }
            }
        } catch (BancoException e) { }

        tipocuentaBox.removeAllItems();
        boolean hayOpciones = false;

        for (String tipo : TIPOS_CUENTA_TOTALES) {
            if (!contratosYaExistentes.contains(tipo)) {
                tipocuentaBox.addItem(tipo);
                hayOpciones = true;
            }
        }
        generarContratoButton.setEnabled(hayOpciones);
    }

    // --- 3. CARGA DE DATOS FILTRADA (AQUÍ ESTÁ LA SOLUCIÓN) ---
    private void cargarDatos() throws BancoException {
        if (listaClientes == null || listaClientes.length == 0) {
            throw new BancoException("No existen clientes en el registro");
        }

        // Guardamos selección actual por si no ha terminado
        Object seleccionadoAntes = usuariosBox.getSelectedItem();

        usuariosBox.removeAllItems();
        boolean hayAlguienPendiente = false;

        for (String[] cliente : listaClientes) {
            String rut = cliente[1];

            // Verificamos qué contratos tiene este cliente
            List<String> contratosQueTiene = new ArrayList<>();
            try {
                String[][] data = ControladorSistema.getInstancia().listarContratosCliente(rut);
                if (data != null) {
                    for (String[] fila : data) contratosQueTiene.add(fila[0].toUpperCase());
                }
            } catch (Exception e) {}

            // Verificamos si le falta alguno de los totales
            boolean leFaltaAlgo = false;
            for (String tipo : TIPOS_CUENTA_TOTALES) {
                if (!contratosQueTiene.contains(tipo)) {
                    leFaltaAlgo = true;
                    break;
                }
            }

            // SOLO LO AGREGAMOS SI LE FALTA ALGO
            if (leFaltaAlgo) {
                usuariosBox.addItem(rut);
                hayAlguienPendiente = true;
            }
        }

        // Restaurar selección si el usuario aún existe en la lista
        if (seleccionadoAntes != null) {
            // Esto evita que salte al primer usuario si el actual aun puede generar más
            for(int i=0; i<usuariosBox.getItemCount(); i++) {
                if(usuariosBox.getItemAt(i).equals(seleccionadoAntes)) {
                    usuariosBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void cargarFoto() {
        try {
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/generarContrato.png"));
            int ancho = 295;
            int alto = 56;
            Image imagenRedimensionada = imagenOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            foto.setIcon(new ImageIcon(imagenRedimensionada));
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void personalizarBoton(JButton boton, Color fondo, Color texto, boolean esMini) {
        if (boton == null) return;
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        if (esMini) {
            boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        } else {
            boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(fondo.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { boton.setBackground(fondo); }
        });
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        GenerarContrato dialog = new GenerarContrato();

        // Validación inicial
        if (dialog.usuariosBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay clientes pendientes de generar contratos.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        } else {
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }
}