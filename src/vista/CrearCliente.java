package vista;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controlador.ControladorSistema;
import excepcion.BancoException;

public class CrearCliente extends JDialog {
    private JPanel contentPane;
    private JButton aceptarButton;
    private JButton salirButton;
    private JTextField nombreField;
    private JTextField rutField;
    private JTextField domicilioField;
    private JLabel foto;

    public CrearCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(aceptarButton);
        cargarFoto();

        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        salirButton.addActionListener(new ActionListener() {
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

        String nombre = nombreField.getText();
        String rut = rutField.getText();
        String domicilio = domicilioField.getText();


        if(nombre.isEmpty() || rut.isEmpty() || domicilio.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Crear Cliente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ControladorSistema.getInstancia().crearCliente(nombre, rut, domicilio);
            JOptionPane.showMessageDialog(this,
                    "Cliente creado exitosamente. Su clave se le será entregada a continuación. Debe copiarla con el comando CTRL+C y guardarla en un lugar seguro.",
                    "Crear Cliente",
                    JOptionPane.INFORMATION_MESSAGE);

            createAndShowGUI(rut);

        } catch (BancoException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear cliente: " + e.getMessage(),
                    "Crear Cliente",
                    JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        CrearCliente dialog = new CrearCliente();
        dialog.setResizable(false);
        dialog.setTitle("Crear Cliente");
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createAndShowGUI(String rut) {
        String longText = "Tiene que copiar la siguiente cadena de texto: " + ControladorSistema.getInstancia().obtenerClaveCliente(rut);



        JTextArea textArea = new JTextArea(5, 30);
        textArea.setText(longText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Contraseña de usuario");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        dialog.dispose();
    }

    private void cargarFoto() {
        try {
            // Ruta de la imagen (ajusta según tu proyecto)
            ImageIcon imagenOriginal = new ImageIcon(getClass().getResource("/graficos/crearCliente.png"));

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
