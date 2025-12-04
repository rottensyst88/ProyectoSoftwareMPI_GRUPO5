package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mostrarDatosCliente extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JComboBox comboBox2;

    // Aquí usamos la variable, pero se inicializa en createUIComponents
    private JPanel panelFoto;

    public mostrarDatosCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
    }

    // --- MAGIA AQUÍ ---
    // IntelliJ llama a esto automáticamente si marcas "Custom Create" en el .form
    private void createUIComponents() {
        panelFoto = new PanelConFondo("/graficos/fotoTarjeta.jpg");
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        mostrarDatosCliente dialog = new mostrarDatosCliente();
        dialog.pack();
        dialog.setSize(500, 400); // Asegura un tamaño visible
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
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}