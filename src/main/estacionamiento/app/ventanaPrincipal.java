package main.estacionamiento.app;

// imports de Java 
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// imports de clases propias 
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.LogoUtils;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.app.ventanas_segundarias.Cancelacion_Cambiar;
import main.estacionamiento.app.ventanas_segundarias.Deshacer;
import main.estacionamiento.app.ventanas_segundarias.Disponibilidad;
import main.estacionamiento.app.ventanas_segundarias.Reservacion;
import main.estacionamiento.app.ventanas_segundarias.Transacciones;
import main.estacionamiento.models.estacionamiento.estacionamiento;

// import para debug
import Test.Debug;

public class ventanaPrincipal extends JFrame {
    private estacionamiento estacionamiento;
    private final Map<String, JFrame> ventanasAbiertas = new HashMap<>();

    public ventanaPrincipal() {
        super("Sistema de Estacionamiento");

        // ! Inicializar el estacionamiento y el historial de transacciones
        this.estacionamiento = new estacionamiento();

        // ! Configuracion basica de la ventana principal.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UiSizes.MAIN_WINDOW_WIDTH, UiSizes.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().setBackground(UiColors.BACKGROUND_MAIN);

        // ! Titulo superior.
        JLabel titulo = new JLabel("UPRM Parking ", SwingConstants.CENTER);
        titulo.setFont(UiFonts.TITLE_BOLD_LARGE);
        titulo.setForeground(UiColors.TEXT);
        RoundedPanel contenedorTitulo = new RoundedPanel(24, UiColors.PANEL_TITLE);
        contenedorTitulo.setLayout(new BorderLayout());
        contenedorTitulo.setBorder(new EmptyBorder(14, 18, 14, 18));
        contenedorTitulo.add(titulo, BorderLayout.CENTER);
        add(contenedorTitulo, BorderLayout.NORTH);

        // ! Panel central con las acciones disponibles.
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setOpaque(false);
        panel.setPreferredSize(UiSizes.actionsPanelSize());
        panel.add(crearBoton("Hacer reservacion", this::abrirReservacion));
        panel.add(crearBoton("Cancelar o Cambiar Reservación", this::abrirCancelacion));
        panel.add(crearBoton("Mostrar espacios disponibles", this::abrirDisponibilidad));
        panel.add(crearBoton("Mostrar transacciones", this::abrirTransacciones));
        panel.add(crearBoton("Deshacer Reservación o Cancelación", this::abrirDeshacer));
        panel.add(crearBoton("Salir", this::salir));
        // (debug button removed) - logo will act as debug trigger

        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        contenedorCentral.setOpaque(false);

        RoundedPanel contenedorAcciones = new RoundedPanel(24, UiColors.PANEL_ACTIONS);
        contenedorAcciones.setLayout(new BorderLayout());
        contenedorAcciones.setBorder(new EmptyBorder(14, 14, 14, 14));
        contenedorAcciones.add(panel, BorderLayout.CENTER);

        contenedorCentral.add(contenedorAcciones);
        add(contenedorCentral, BorderLayout.CENTER);

        JLabel logoLabel = LogoUtils.crearLogoTarzan(90, 90);
        JPanel contenedorInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenedorInferior.setOpaque(false);
        // make the logo clickable to run the debug prefill
        logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Debug.debugPrefill(estacionamiento);
                JOptionPane.showMessageDialog(ventanaPrincipal.this, "Debug fill applied: spaces updated.", "Debug", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        contenedorInferior.add(logoLabel);
        add(contenedorInferior, BorderLayout.SOUTH);
    }

    // ! Metodo principal para iniciar la aplicacion.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ventanaPrincipal().setVisible(true));
    }

    // ! Metodo auxiliar para crear botones con estilo consistente.
    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new RoundedButton(texto, 20);
        boton.setForeground(UiColors.TEXT);
        boton.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        boton.setMargin(UiSizes.buttonMargin());
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    // ! Metodos para abrir las diferentes ventanas segun la accion seleccionada.
    private void abrirReservacion() {
        abrirVentanaUnica("Reservacion", () -> new Reservacion(estacionamiento));
    }

    private void abrirCancelacion() {
        abrirVentanaUnica("Cancelacion", () -> new Cancelacion_Cambiar(estacionamiento));
    }

    private void abrirDisponibilidad() {
        abrirVentanaUnica("Disponibilidad", () -> new Disponibilidad(estacionamiento));
    }

    private void abrirTransacciones() {
        abrirVentanaUnica("Transacciones", () -> new Transacciones(estacionamiento));
    }

    private void abrirDeshacer() {
        abrirVentanaUnica("Deshacer", () -> new Deshacer(estacionamiento));
    }

    private void salir() {
        // Cierra la ventana principal.
        dispose();
    }

    // ! Metodo para abrir una ventana unica y evitar multiples instancias.
    private void abrirVentanaUnica(String claveVentana, Supplier<JFrame> creadorVentana) {
        JFrame ventanaExistente = ventanasAbiertas.get(claveVentana);
        if (ventanaExistente != null && ventanaExistente.isDisplayable()) {
            ventanaExistente.setState(Frame.NORMAL);
            ventanaExistente.toFront();
            ventanaExistente.requestFocus();
            return;
        }

        JFrame nuevaVentana = creadorVentana.get();
        ventanasAbiertas.put(claveVentana, nuevaVentana);
        nuevaVentana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanasAbiertas.remove(claveVentana);
            }
        });
        nuevaVentana.setVisible(true);
    }
}