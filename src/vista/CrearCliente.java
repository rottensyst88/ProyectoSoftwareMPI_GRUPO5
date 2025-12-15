package vista;

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
    private JSpinner edadSpinner;
    private JTextField ingresoMensField;
    private JTextField gastosMensualesField;
    private JCheckBox tieneDeudaCheck;

    public CrearCliente() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(aceptarButton);
        cargarFoto();
        edadSpinner.setModel(new SpinnerNumberModel(18, 18, 120, 1)); // Edad mínima 18 años

        // --- PERSONALIZACIÓN DEL ESTILO (IGUAL A VENTANA PRINCIPAL) ---

        // 1. Estilo Naranja Uniforme para el botón de acción principal "Aceptar"
        Color naranjaPrincipal = new Color(179, 79, 0); // Naranja Flat
        Color blancoTexto = Color.WHITE;
        personalizarBotonUniforme(aceptarButton, naranjaPrincipal, blancoTexto);

        // 2. Estilo Rojo sin borde para el botón "Salir"
        Color rojoSalir = new Color(192, 57, 43); // Rojo
        personalizarBotonMini(salirButton, rojoSalir, blancoTexto);

        // --- FIN DE LA PERSONALIZACIÓN ---

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

        nombreField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char caracter = e.getKeyChar();

                // Verifica si el carácter es un dígito.
                if (Character.isDigit(caracter)) {
                    // Consume el evento, impidiendo que el carácter se escriba.
                    e.consume();
                    // Muestra el aviso (opcional)
                    JOptionPane.showMessageDialog(nombreField,
                            "No se permiten números en el campo Nombre.",
                            "Entrada Inválida",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        rutField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char caracter = e.getKeyChar();
                String textoActual = rutField.getText();

                // Regla 1: Permitir control y caracteres comunes (backspace, delete)
                if (e.isControlDown() || e.isActionKey() || caracter == KeyEvent.VK_BACK_SPACE) {
                    return;
                }

                // Regla 2: Permitir dígitos, puntos y guiones
                if (Character.isDigit(caracter) || caracter == '.' || caracter == '-') {
                    return;
                }

                // Regla 3: Permitir 'K' o 'k' SÓLO si es el carácter que sigue al guion.
                // Asumimos el formato es X.XXX.XXX-K. La K va después del guion y el guion no es el último.
                if ((caracter == 'k' || caracter == 'K') && textoActual.contains("-") && textoActual.indexOf('-') == textoActual.length() - 1) {
                    return;
                }

                // Si llega aquí, es una letra o un símbolo no permitido.
                e.consume();
                JOptionPane.showMessageDialog(rutField,
                        "Solo se permiten números, puntos, guiones y 'K'/'k' para el dígito verificador.",
                        "Entrada Inválida",
                        JOptionPane.WARNING_MESSAGE);

            }
        });
    }

    private void onOK() {

        String nombre = nombreField.getText();
        String rut = rutField.getText();
        String domicilio = domicilioField.getText();
        String ingresoMensualStr = ingresoMensField.getText();
        String gastosMensualesStr = gastosMensualesField.getText();
        int edad = (Integer) edadSpinner.getValue();
        boolean tieneDeuda = tieneDeudaCheck.isSelected();

        if(nombre.isEmpty() || rut.isEmpty() || domicilio.isEmpty() || ingresoMensualStr.isEmpty() || gastosMensualesStr.isEmpty() || edad <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Crear Cliente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // --- 1. INTENTO DE CREACIÓN ---
            ControladorSistema.getInstancia().crearCliente(nombre, rut, domicilio, edad,
                    Double.parseDouble(ingresoMensualStr),
                    Double.parseDouble(gastosMensualesStr),
                    tieneDeuda);

            // --- 2. BLOQUE DE ÉXITO (Se cierra la ventana) ---
            JOptionPane.showMessageDialog(this,
                    "Cliente creado exitosamente. Su clave se le será entregada a continuación. Debe copiarla con el comando CTRL+C y guardarla en un lugar seguro.",
                    "Crear Cliente",
                    JOptionPane.INFORMATION_MESSAGE);

            createAndShowGUI(rut);

            mostrarResultadoRiesgoGUI(ControladorSistema.getInstancia().analizarSolicitud(rut));

            // CERRAR LA VENTANA SOLO SI LA CREACIÓN FUE EXITOSA
            dispose();

        } catch (BancoException e) {
            // --- 3. BLOQUE DE ERROR (La ventana permanece abierta) ---

            String mensajeError = e.getMessage();

            JOptionPane.showMessageDialog(this,
                    "Error al crear cliente: " + mensajeError,
                    "Crear Cliente",
                    JOptionPane.ERROR_MESSAGE);

            // 4. LÓGICA DE LIMPIEZA CONDICIONAL

            // Error de Formato de RUT: Solo limpia el campo RUT
            if (mensajeError.contains("formato del RUT ingresado es incorrecto")) {
                rutField.setText("");
            }
            // Error de Cliente Existente: Limpia todos los campos
            else if (mensajeError.contains("Cliente ya existe")) {
                nombreField.setText("");
                rutField.setText("");
                domicilioField.setText("");
            }
            // Otro error (ej. error de base de datos): No limpia nada o solo el campo RUT por seguridad
            // Se deja a criterio, pero en este caso limpiaré solo el RUT para forzar la corrección del dato clave.
            else {
                rutField.setText("");
            }

            // 5. IMPORTANTE: Eliminar el 'dispose()' al final del método.
        }
        // 🔴 NOTA: ELIMINAR O COMENTAR el 'dispose()' que estaba al final del método.
        // dispose(); // ¡QUITAR ESTA LÍNEA!
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

    private void personalizarBotonUniforme(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Sin borde

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
     * Personaliza botones pequeños como "Salir" (Rojo, sin borde).
     */
    private void personalizarBotonMini(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Sin borde

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });
    }

    // Método para mostrar la interpretación del riesgo
    public void mostrarResultadoRiesgoGUI(double valorRiesgo) {

        // 1. Lógica de Interpretación: Definimos el texto y el tipo de alerta
        String mensaje;
        String tituloVentana;
        int tipoIcono; // Para que salga una X roja, un triángulo amarillo o una i azul

        if (valorRiesgo == -1.0) {
            tituloVentana = "SOLICITUD BLOQUEADA";
            mensaje = "ESTADO: RECHAZADO AUTOMÁTICAMENTE\n\n" +
                    "Motivo: El cliente presenta antecedentes comerciales negativos (Deuda Castigada/DICOM).\n" +
                    "No es posible cursar la evaluación financiera.";
            tipoIcono = JOptionPane.ERROR_MESSAGE;
        } else if (valorRiesgo > 60) {
            // CASO RIESGO ALTO
            tituloVentana = "ALERTA DE RIESGO";
            mensaje = String.format("NIVEL DE RIESGO: ALTO (%.2f%%)\n\n" +
                    "El cliente supera el umbral de seguridad.\n" +
                    "RECOMENDACIÓN: Rechazar solicitud de crédito.\n" +
                    "Sus deudas consumen gran parte de sus ingresos.", valorRiesgo);
            tipoIcono = JOptionPane.ERROR_MESSAGE; // Icono Rojo
        }

        else if (valorRiesgo > 40) {
            // CASO RIESGO MEDIO
            tituloVentana = "Análisis de Riesgo";
            mensaje = String.format("NIVEL DE RIESGO: MEDIO (%.2f%%)\n\n" +
                    "El cliente tiene capacidad de pago ajustada.\n" +
                    "RECOMENDACIÓN: Aprobar con condiciones (Aval o monto bajo).", valorRiesgo);
            tipoIcono = JOptionPane.WARNING_MESSAGE; // Icono Amarillo

        } else {
            // CASO RIESGO BAJO
            tituloVentana = "Análisis de Riesgo";
            mensaje = String.format("NIVEL DE RIESGO: BAJO (%.2f%%)\n\n" +
                    "El cliente tiene excelente salud financiera.\n" +
                    "RECOMENDACIÓN: Aprobar solicitud inmediatamente.", valorRiesgo);
            tipoIcono = JOptionPane.INFORMATION_MESSAGE; // Icono Azul
        }

        // 2. Construcción de la GUI (Tu estructura adaptada)
        JTextArea textArea = new JTextArea(6, 35); // Tamaño ajustado
        textArea.setText(mensaje);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Letra un poco más grande

        // Un margen interno para que el texto no pegue con los bordes
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // 3. Creación del Dialog
        // Pasamos 'tipoIcono' para que visualmente se entienda la gravedad
        JOptionPane optionPane = new JOptionPane(panel, tipoIcono);

        // NOTA: Si este método está dentro de un JFrame, usa 'this'. Si es estático, usa 'null'.
        JDialog dialog = optionPane.createDialog(null, tituloVentana);

        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        dialog.dispose();
    }
}
