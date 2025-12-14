package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FirmarContrato extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> usuarioBox;
    private JComboBox<String> contratosDisponiblesBox;
    private JPasswordField claveUsuarioPassword;
    private JLabel foto;

    private String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    private String[][] contratosClientes = null;

    public FirmarContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Firmar Contrato");

        // --- ESTILO VISUAL ---
        Color naranjaOscuro = new Color(211, 84, 0);
        Color rojoSalir = new Color(192, 57, 43);
        personalizarBoton(buttonOK, naranjaOscuro, Color.WHITE, false);
        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);

        // Intentar llenar usuarios inicial
        try {
            llenarUsuarios();
        } catch (BancoException e) {
            // Se ignora aquí porque el main maneja la validación de apertura
        }
        cargarFoto();

        buttonOK.addActionListener(e -> onOK());
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

        usuarioBox.addActionListener(e -> actualizarDatosContrato());
    }

    private void onOK() {
        // Validar selección
        if (contratosDisponiblesBox.getItemCount() == 0 || usuarioBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No hay contratos disponibles para firmar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Intentar firmar
            if (ControladorSistema.getInstancia().firmarContratoCliente(
                    usuarioBox.getSelectedItem().toString(),
                    contratosDisponiblesBox.getSelectedItem().toString(),
                    new String(claveUsuarioPassword.getPassword()))) {

                JOptionPane.showMessageDialog(this, "Contrato Firmado Exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // 1. Actualizar lista de contratos del usuario actual
                actualizarDatosContrato();

                // 2. Limpiar clave
                claveUsuarioPassword.setText("");

                // 3. VERIFICAR SI EL USUARIO ACTUAL TERMINÓ TODO
                if(contratosDisponiblesBox.getItemCount() == 0){
                    JOptionPane.showMessageDialog(this, "El cliente actual ha firmado todos sus contratos.", "Info", JOptionPane.INFORMATION_MESSAGE);

                    // INTENTAR RECARGAR LA LISTA DE USUARIOS (Para quitar al que ya terminó)
                    try {
                        llenarUsuarios(); // Si hay otros usuarios, esto funcionará y refrescará el combo

                        // Si llegamos aquí, es que hay otros usuarios. Seleccionamos el primero.
                        if (usuarioBox.getItemCount() > 0) {
                            usuarioBox.setSelectedIndex(0);
                        }

                    } catch (BancoException ex) {
                        // Si salta esta excepción, significa que llenarUsuarios no encontró a NADIE con contratos pendientes.
                        JOptionPane.showMessageDialog(this, " No quedan más contratos pendientes en todo el sistema.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // AHORA SÍ CERRAMOS, PORQUE NO QUEDA NADIE
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Error generico al firmar contrato", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this, "Error al firmar contrato: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    // --- MÉTODOS DE DATOS ---
    private void llenarUsuarios() throws BancoException {
        if (datosClientes == null || datosClientes.length == 0) {
            throw new BancoException("No existen clientes registrados en el sistema.");
        }

        // Guardamos el usuario seleccionado actualmente para intentar mantenerlo si aún tiene contratos
        Object usuarioSeleccionadoAntes = usuarioBox.getSelectedItem();

        usuarioBox.removeAllItems();
        boolean seEncontroAlguien = false;

        for (String[] c : datosClientes) {
            String rutCliente = c[1];
            try {
                String[][] contratosDelCliente = ControladorSistema.getInstancia().listarContratosCliente(rutCliente);
                boolean tienePendiente = false;

                for (String[] contrato : contratosDelCliente) {
                    if ("No Firmado".equalsIgnoreCase(contrato[1])) {
                        tienePendiente = true;
                        break;
                    }
                }

                if (tienePendiente) {
                    usuarioBox.addItem(rutCliente);
                    seEncontroAlguien = true;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (!seEncontroAlguien) {
            throw new BancoException("No existen clientes con contratos pendientes de firma.");
        }
    }

    private void actualizarDatosContrato() {
        if (usuarioBox.getSelectedItem() == null) {
            contratosDisponiblesBox.removeAllItems();
            return;
        }

        String rutUsuario = usuarioBox.getSelectedItem().toString();

        try {
            contratosClientes = ControladorSistema.getInstancia().listarContratosCliente(rutUsuario);
        } catch (BancoException e) {
            contratosDisponiblesBox.removeAllItems();
            return;
        }

        contratosDisponiblesBox.removeAllItems();
        contratosDisponiblesBox.setEnabled(true);

        for (String[] contrato : contratosClientes) {
            if ("No Firmado".equalsIgnoreCase(contrato[1])) {
                contratosDisponiblesBox.addItem(contrato[0]);
            }
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

    private void cargarFoto() {
        try {
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/firmarContrato.png"));
            int ancho = 295;
            int alto = 56;
            Image imagenRedimensionada = imagenOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            foto.setIcon(new ImageIcon(imagenRedimensionada));
        } catch (Exception e) {
            // e.printStackTrace();
        }
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

        try {
            FirmarContrato dialog = new FirmarContrato();
            dialog.llenarUsuarios();
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        } catch (BancoException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}