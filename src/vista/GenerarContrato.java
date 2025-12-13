package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GenerarContrato extends JDialog {
    private JPanel contentPane;
    private JButton generarContratoButton;
    private JButton buttonCancel;
    private JComboBox usuariosBox;
    private JComboBox tipocuentaBox;
    private JLabel foto;

    private String[][] listaClientes = ControladorSistema.getInstancia().listarClientes();


    public GenerarContrato() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(generarContratoButton);

        cargarDatos();
        cargarFoto();

        // --- INICIO CAMBIOS VISUALES ---
        // 1. Estilo Naranja Oscuro para el botón principal
        Color naranjaOscuro = new Color(211, 84, 0);
        personalizarBoton(generarContratoButton, naranjaOscuro, Color.WHITE, false);

        // 2. Estilo Rojo para el botón cancelar
        Color rojoSalir = new Color(192, 57, 43);
        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);
        // --- FIN CAMBIOS VISUALES ---


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
        GenerarContrato dialog = new GenerarContrato();
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

    private void cargarFoto() {
        try {
            // Ruta de la imagen (ajusta según tu proyecto)
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/generarContrato.png"));

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

}