package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;
import modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FirmarContrato extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox usuarioBox;
    private JComboBox contratosDisponiblesBox;
    private JPasswordField claveUsuarioPassword;
    private JLabel foto;

    private String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();
    private String[][] contratosClientes = null;

    public FirmarContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Firmar Contrato");

        // --- INICIO CAMBIOS VISUALES ---
        // 1. Estilo Naranja Oscuro para el botón principal (Firmar)
        Color naranjaOscuro = new Color(211, 84, 0);
        personalizarBoton(buttonOK, naranjaOscuro, Color.WHITE, false);

        // 2. Estilo Rojo para el botón cancelar
        Color rojoSalir = new Color(192, 57, 43);
        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);
        // --- FIN CAMBIOS VISUALES ---

        try {
            llenarUsuarios();
        } catch (BancoException e) {

        }
        cargarFoto();


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

    // --- MÉTODOS VISUALES AGREGADOS ---
    private void personalizarBoton(JButton boton, Color fondo, Color texto, boolean esMini) {
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
    // ---------------------------------

    public static void main(String[] args) {
        // --- CAMBIO VISUAL (Nimbus) ---
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } catch (Exception ex) {}
        }
        // ------------------------------

        // *************** VALIDACIÓN ANTES DE ABRIR LA VENTANA ***************
        try {
            // Instanciar el diálogo primero para poder llamar a llenarUsuarios
            FirmarContrato dialog = new FirmarContrato();

            // Reintentar llenar la lista para verificar si hay clientes disponibles
            dialog.llenarUsuarios();

            // Si el método llenarUsuarios no lanza excepción, significa que hay contratos pendientes.
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

        } catch (BancoException e) {
            // Si llenarUsuarios lanza una excepción (No hay clientes o no hay contratos pendientes),
            // mostramos el error y NO abrimos la ventana.
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Firmar Contrato",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        // ********************************************************************
    }

    // ***** MÉTODO MODIFICADO PARA FILTRAR CLIENTES CON CONTRATOS PENDIENTES *****
    private void llenarUsuarios() throws BancoException {

        // 1. Verificación básica: ¿Hay clientes en la BD?
        if (datosClientes == null || datosClientes.length == 0){
            throw new BancoException("No existen clientes registrados en el sistema.");
        }

        usuarioBox.removeAllItems();
        boolean seEncontroAlguien = false;

        // 2. Recorremos TODOS los clientes
        for (String[] c : datosClientes){
            String rutCliente = c[1]; // Asumiendo que el índice 1 es el RUT

            try {
                // Obtenemos los contratos de ESTE cliente específico
                String[][] contratosDelCliente = ControladorSistema.getInstancia().listarContratosCliente(rutCliente);

                // 3. Verificamos si tiene AL MENOS UN contrato "No Firmado"
                boolean tienePendiente = false;
                for (String[] contrato : contratosDelCliente) {
                    // contrato[1] es el estado ("Firmado" o "No Firmado")
                    if ("No Firmado".equalsIgnoreCase(contrato[1])) {
                        tienePendiente = true;
                        break;
                    }
                }

                // 4. Si tiene pendientes, lo agregamos a la lista desplegable
                if (tienePendiente) {
                    usuarioBox.addItem(rutCliente);
                    seEncontroAlguien = true;
                }

            } catch (Exception e) {
                // Si el cliente no tiene contratos creados, el controlador puede lanzar una excepción. La ignoramos.
                continue;
            }
        }

        // 5. Si después de revisar a todos, nadie tenía contratos pendientes:
        if (!seEncontroAlguien) {
            throw new BancoException("No existen clientes con contratos pendientes de firma.");
        }
    }

    private void actualizarDatosContrato() {
        if (usuarioBox.getSelectedItem() == null) {
            // Esto ocurre cuando se limpian los ítems o no hay ninguno.
            // Limpiar la lista de contratos por si acaso y salir.
            contratosDisponiblesBox.removeAllItems();
            return;}

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

    private void cargarFoto() {
        try {
            // Ruta de la imagen (ajusta según tu proyecto)
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/firmarContrato.png"));

            // Redimensionar la imagen
            int ancho = 295;  // Ajusta estos valores según necesites
            int alto = 56;
            Image imagenRedimensionada = imagenOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

            // Asignar la imagen redimensionada al JLabel
            foto.setIcon(new ImageIcon(imagenRedimensionada));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}