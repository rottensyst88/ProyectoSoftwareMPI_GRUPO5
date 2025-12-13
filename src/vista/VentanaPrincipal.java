package vista;

import excepcion.BancoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaPrincipal extends JDialog {
    private JPanel contentPane;
    private JButton salirButton;
    private JButton acercaDeButton;
    private JButton crearClienteButton;
    private JButton crearContratoButton;
    private JButton mostrarCuentasClienteButton;
    private JButton listarClientesButton;
    private JButton firmarContratoButton;
    private JPanel panelFoto;
    private JLabel foto;
    private JPanel botones;
    private JButton opcionesPersist;

    public VentanaPrincipal() {

        setContentPane(contentPane);
        setModal(true);
        cargarFoto();
        getRootPane().setDefaultButton(salirButton);

        // --- PERSONALIZACIÓN DEL ESTILO DE LOS BOTONES ---

        // 1. Estilo Naranja Uniforme para los botones de acción principal
        Color naranjaPrincipal = new Color(230, 126, 34); // Naranja Flat
        Color blancoTexto = Color.WHITE;

        personalizarBotonUniforme(crearClienteButton, naranjaPrincipal, blancoTexto);
        personalizarBotonUniforme(crearContratoButton, naranjaPrincipal, blancoTexto);
        personalizarBotonUniforme(firmarContratoButton, naranjaPrincipal, blancoTexto);
        personalizarBotonUniforme(mostrarCuentasClienteButton, naranjaPrincipal, blancoTexto);
        personalizarBotonUniforme(listarClientesButton, naranjaPrincipal, blancoTexto);
        personalizarBotonUniforme(opcionesPersist, naranjaPrincipal, blancoTexto);

        // 2. Personalizar botones pequeños sin borde
        // Botón "Acerca de" (Azul)
        personalizarBotonMini(acercaDeButton, new Color(52, 152, 219), Color.WHITE);
        // Botón "Salir" (Rojo)
        personalizarBotonMini(salirButton, new Color(192, 57, 43), Color.WHITE);

        // --- FIN DE LA PERSONALIZACIÓN ---



        salirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        acercaDeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {acercaDe();}
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        crearClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CrearCliente.main(null);
            }
        });
        listarClientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListarClientes.main(null);
            }
        });
        crearContratoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    GenerarContrato.main(null);
                }catch (BancoException e1){
                    JOptionPane.showMessageDialog(null,
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        firmarContratoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    FirmarContrato.main(null);
                } catch (BancoException e1) {
                    JOptionPane.showMessageDialog(null,
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        opcionesPersist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    OpcionesPersistencia.main(null);
                } catch (BancoException e1){
                    JOptionPane.showMessageDialog(null,
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mostrarCuentasClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try{
                    MostrarDatosCliente.main(null);
                } catch (BancoException e1){
                    JOptionPane.showMessageDialog(null,
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void acercaDe(){
        JOptionPane.showMessageDialog(this,
                "Sistema Bancario v1.0\n" +
                        "Sistema de prueba, Versión de compilación 29NOV2025\n",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, puedes establecer un valor predeterminado
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        VentanaPrincipal dialog = new VentanaPrincipal();
        dialog.setTitle("Administración de Sistema Bancario");
        dialog.setSize(800,500);

        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /*
    public static void main(String[] args) {
        try {
            // Establece el Look and Feel de Motif
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        VentanaPrincipal dialog = new VentanaPrincipal();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }*/

    private void cargarFoto() {
        try {
            // Ruta de la imagen (ajusta según tu proyecto)
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/banner_BancoEstado.png"));

            // Redimensionar la imagen
            int ancho = 289;  // Ajusta estos valores según necesites
            int alto = 55;
            Image imagenRedimensionada = imagenOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

            // Asignar la imagen redimensionada al JLabel
            foto.setIcon(new ImageIcon(imagenRedimensionada));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE PERSONALIZACIÓN DE ESTILO ---

    /**
     * Aplica el estilo uniforme (sin borde, color de fondo, fuente ligeramente más pequeña)
     * a los botones principales.
     */
    private void personalizarBotonUniforme(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente más pequeña (12pt)
        boton.setFocusPainted(false); // Quita el recuadro de foco

        // Remover el borde para imitar el estilo plano
        boton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Borde vacío para relleno/padding

        // Efecto visual al pasar el ratón
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });
    }

    /**
     * Personaliza botones pequeños como "Salir" o "Acerca de" sin bordes.
     */
    private void personalizarBotonMini(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);

        // Quitar el borde completamente
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Borde vacío para relleno

        // Efecto visual al pasar el ratón
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });
    }

    // --- FIN MÉTODOS DE PERSONALIZACIÓN DE ESTILO ---





}