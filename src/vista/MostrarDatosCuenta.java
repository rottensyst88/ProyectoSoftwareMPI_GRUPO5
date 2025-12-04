package vista;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MostrarDatosCuenta extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel datosUsuario;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JPanel datosTarjeta; // Este será nuestro panel con fondo
    private JLabel nroCuenta;
    private JLabel fechaCuenta;
    private JLabel cvvCuenta;

    public MostrarDatosCuenta() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // Configurar el panel datosTarjeta con fondo de imagen
        configurarFondoTarjeta();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void configurarFondoTarjeta() {
        // Crear el panel personalizado con fondo
        PanelConFondoTarjeta panelFondo = new PanelConFondoTarjeta();

        // Obtener información del layout y componentes del panel original
        LayoutManager layoutOriginal = datosTarjeta.getLayout();
        Component[] componentes = datosTarjeta.getComponents();

        // Guardar información de los componentes y sus constraints
        java.util.List<ComponentInfo> componentInfos = new java.util.ArrayList<>();

        try {
            java.lang.reflect.Method getConstraintsMethod =
                layoutOriginal.getClass().getMethod("getConstraints", Component.class);

            for (Component comp : componentes) {
                Object constraint = getConstraintsMethod.invoke(layoutOriginal, comp);
                // Clonar la constraint para evitar problemas de referencia
                Object clonedConstraint = cloneConstraint(constraint);
                componentInfos.add(new ComponentInfo(comp, clonedConstraint));
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo constraints: " + e.getMessage());
            // Sin constraints, guardar solo los componentes
            for (Component comp : componentes) {
                componentInfos.add(new ComponentInfo(comp, null));
            }
        }

        // Crear un nuevo layout manager (clonar el original)
        LayoutManager nuevoLayout = cloneLayoutManager(layoutOriginal);
        panelFondo.setLayout(nuevoLayout);

        // Remover componentes del panel original
        datosTarjeta.removeAll();

        // Agregar componentes al nuevo panel con sus constraints clonadas
        for (ComponentInfo info : componentInfos) {
            if (info.constraint != null) {
                panelFondo.add(info.component, info.constraint);
            } else {
                panelFondo.add(info.component);
            }
        }

        // Reemplazar el panel en el contenedor padre
        Container parent = datosTarjeta.getParent();
        if (parent != null) {
            // Obtener las constraints del panel datosTarjeta en su contenedor padre
            Object parentConstraints = null;
            LayoutManager parentLayout = parent.getLayout();

            try {
                java.lang.reflect.Method getConstraintsMethod =
                    parentLayout.getClass().getMethod("getConstraints", Component.class);
                Object originalConstraint = getConstraintsMethod.invoke(parentLayout, datosTarjeta);
                parentConstraints = cloneConstraint(originalConstraint);
            } catch (Exception e) {
                System.err.println("No se pudieron obtener las constraints del padre: " + e.getMessage());
            }

            // Remover el panel original
            parent.remove(datosTarjeta);

            // Agregar el nuevo panel con las constraints del padre
            if (parentConstraints != null) {
                parent.add(panelFondo, parentConstraints);
            } else {
                parent.add(panelFondo);
            }

            // Actualizar la referencia
            datosTarjeta = panelFondo;

            parent.revalidate();
            parent.repaint();
        }
    }

    // Clase auxiliar para guardar información de componentes
    private static class ComponentInfo {
        Component component;
        Object constraint;

        ComponentInfo(Component component, Object constraint) {
            this.component = component;
            this.constraint = constraint;
        }
    }

    // Método para clonar un layout manager
    private LayoutManager cloneLayoutManager(LayoutManager original) {
        try {
            // Intentar clonar el layout usando reflexión
            Class<?> layoutClass = original.getClass();

            // Para GridLayoutManager de IntelliJ
            if (layoutClass.getName().contains("GridLayoutManager")) {
                // Obtener los parámetros del constructor
                java.lang.reflect.Method getRowCountMethod = layoutClass.getMethod("getRowCount");
                java.lang.reflect.Method getColumnCountMethod = layoutClass.getMethod("getColumnCount");

                int rowCount = (Integer) getRowCountMethod.invoke(original);
                int columnCount = (Integer) getColumnCountMethod.invoke(original);

                // Crear una nueva instancia
                java.lang.reflect.Constructor<?> constructor = layoutClass.getConstructor(int.class, int.class);
                return (LayoutManager) constructor.newInstance(rowCount, columnCount);
            }
        } catch (Exception e) {
            System.err.println("No se pudo clonar el layout manager, usando GridLayout por defecto: " + e.getMessage());
        }

        // Fallback: usar un GridLayout simple
        return new GridLayout(0, 1);
    }

    // Método para clonar constraints
    private Object cloneConstraint(Object constraint) {
        if (constraint == null) return null;

        try {
            // Para GridConstraints de IntelliJ
            if (constraint.getClass().getName().contains("GridConstraints")) {
                // Intentar usar el método clone si existe
                java.lang.reflect.Method cloneMethod = constraint.getClass().getMethod("clone");
                return cloneMethod.invoke(constraint);
            }
        } catch (Exception e) {
            // Si falla el clonado, retornar el original
            System.err.println("No se pudo clonar la constraint, usando original: " + e.getMessage());
        }

        return constraint;
    }

    // Clase interna para el panel con fondo de tarjeta
    private static class PanelConFondoTarjeta extends JPanel {
        private Image imagenFondo;

        public PanelConFondoTarjeta() {
            try {
                // Carga la imagen - ajusta la ruta según tu estructura de proyecto
                java.net.URL imageURL = getClass().getResource("/graficos/fotoTarjeta.jpg");
                if (imageURL != null) {
                    imagenFondo = new ImageIcon(imageURL).getImage();
                    setOpaque(false); // Importante para transparencia

                    // Establecer un tamaño preferido basado en la imagen
                    setPreferredSize(new Dimension(
                            imagenFondo.getWidth(this),
                            imagenFondo.getHeight(this)
                    ));
                } else {
                    System.err.println("No se encontró la imagen: /graficos/fotoTarjeta.jpg");
                    usarFondoAlternativo();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar fotoTarjeta.jpg: " + e.getMessage());
                usarFondoAlternativo();
            }
        }

        private void usarFondoAlternativo() {
            // Si no se encuentra la imagen, usar un color de fondo
            imagenFondo = null;
            setBackground(new Color(50, 50, 100));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null) {
                // Escalar para llenar el panel manteniendo la calidad
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                     RenderingHints.VALUE_RENDER_QUALITY);

                // Opción 1: Escalar para llenar el panel completamente
                g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);

                // Opción 2: Centrar la imagen en su tamaño original (descomenta para usar)
                // int x = (getWidth() - imagenFondo.getWidth(this)) / 2;
                // int y = (getHeight() - imagenFondo.getHeight(this)) / 2;
                // g2d.drawImage(imagenFondo, x, y, this);
            }
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MostrarDatosCuenta dialog = new MostrarDatosCuenta();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }
}