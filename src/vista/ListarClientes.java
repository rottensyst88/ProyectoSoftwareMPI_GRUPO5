package vista;

import controlador.ControladorSistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ListarClientes extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table1;

    // Obtención de datos del controlador
    private String[][] listaDatos = ControladorSistema.getInstancia().listarClientes();
    private String[] columnas = {"Nombre completo", "Rut", "Dirección", "Clave"};

    public ListarClientes() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Listado de Clientes"); // Título de la ventana

        // Configurar modelo de tabla
        // Hacemos que las celdas no sean editables para que sea solo de lectura
        DefaultTableModel model = new DefaultTableModel(listaDatos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(model);

        // --- PERSONALIZACIÓN DE ESTILO ---

        // 1. Botón OK: Naranja Oscuro (El que elegiste: 211, 84, 0)
        Color naranjaOscuro = new Color(211, 84, 0);
        personalizarBoton(buttonOK, naranjaOscuro, Color.WHITE, false);
        buttonOK.setText("Aceptar"); // Cambiamos "OK" por "Aceptar" si prefieres español

        // 2. Botón Cancel: Rojo (Estilo Salir)
        Color rojoSalir = new Color(192, 57, 43);
        personalizarBoton(buttonCancel, rojoSalir, Color.WHITE, true);
        buttonCancel.setText("Volver"); // Cambiamos "Cancel" por "Volver"

        // --- FIN PERSONALIZACIÓN ---

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
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    // --- MÉTODO DE ESTILO COMPARTIDO ---
    private void personalizarBoton(JButton boton, Color fondo, Color texto, boolean esMini) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);

        // Quitar bordes (EmptyBorder)
        if (esMini) {
            boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        } else {
            boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });
    }

    public static void main(String[] args) {
        // --- ACTIVAR LOOK AND FEEL NIMBUS ---
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
            } catch (Exception ex) {
            }
        }

        ListarClientes dialog = new ListarClientes();
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Centrar en pantalla
        dialog.setVisible(true);
    }
}