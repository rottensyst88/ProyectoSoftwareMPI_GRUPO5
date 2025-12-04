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
                mostrarDatosCliente.main(null);
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

}