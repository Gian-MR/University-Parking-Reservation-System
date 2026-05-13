package main.estacionamiento.app.ventanas_segundarias;

// imports de Java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.LinkedList;
import java.util.List;

// imports de clases propias
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.models.servicios_especiales.servicios_especiales;
import main.estacionamiento.models.transaccion.transaccion;
import main.estacionamiento.models.transaccion.transaccionHistorial;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.utils.UiWindowUtils;
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;

public class Transacciones extends JFrame {

    private final transaccionHistorial historial;
    private JList<transaccion> lista;
    private JTextArea detallePanel;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Transacciones(estacionamiento estacionamiento) {
        super("Historial de Transacciones");
        this.historial = estacionamiento.getTransaccionHistorial();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(UiSizes.SECONDARY_WINDOW_WIDTH + 200, UiSizes.SECONDARY_WINDOW_HEIGHT + 150);
        setLocationRelativeTo(null);

        UiWindowUtils.applySecondaryWindowBaseStyle(this);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false);
        norte.add(UiWindowUtils.createTitlePanel("Historial de Transacciones"));
        norte.add(Box.createVerticalStrut(8));
        norte.add(crearPanelFiltros());
        add(norte, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelLista(), crearPanelDetalles());
        split.setResizeWeight(0.5);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JScrollPane crearPanelLista() {
        transaccion[] transaccionsArray = historial.getHistorial().toArray(new transaccion[0]);
        lista = new JList<>(transaccionsArray);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                transaccion seleccionada = lista.getSelectedValue();
                if (seleccionada != null) {
                    mostrarDetalles(seleccionada);
                }
            }
        });
        cargarTransacciones();
        return new JScrollPane(lista);
    }

    private void cargarTransacciones() {
        mostrarEnLista(historial.getHistorial());
    }

    private void mostrarEnLista(LinkedList<transaccion> transacciones) {
        lista.setListData(transacciones.toArray(new transaccion[0]));
    }

    private JScrollPane crearPanelDetalles() {
        detallePanel = new JTextArea();
        detallePanel.setEditable(false);
        detallePanel.setLineWrap(true);
        detallePanel.setWrapStyleWord(true);
        detallePanel.setText("Seleciona una transaccion para ver sus detalles");
        return new JScrollPane(detallePanel);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        JButton refrescar = new RoundedButton("\u21BB", 20);
        refrescar.setForeground(UiColors.TEXT);
        refrescar.setFont(UiFonts.BUTTON_BOLD_MEDIUM.deriveFont(22f));
        refrescar.setToolTipText("Refrescar");
        refrescar.addActionListener(e -> cargarTransacciones());

        panel.add(refrescar);
        return panel;
    }

    private JPanel crearPanelFiltros() {
        RoundedPanel panel = new RoundedPanel(24, UiColors.PANEL_ACTIONS);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        // quick actions
        JPanel fastbuttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fastbuttons.add(crearLabelFiltro("Filtros:"));
        fastbuttons.setOpaque(false);

        JButton btnTodos = crearBotonFiltro("Todos");
        btnTodos.addActionListener(e -> cargarTransacciones());
        fastbuttons.add(btnTodos);

        JButton btnEstaSemana = crearBotonFiltro("Esta Semana");
        btnEstaSemana.addActionListener(e -> mostrarEnLista(historial.filterByThisWeek()));
        fastbuttons.add(btnEstaSemana);

        JButton btnMasDeDosHoras = crearBotonFiltro("Mas de 2h");
        btnMasDeDosHoras.addActionListener(e -> mostrarEnLista(historial.filterMoreThanAmountofHours(2)));
        fastbuttons.add(btnMasDeDosHoras);

        panel.add(fastbuttons);

        // price options
        JPanel filaPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaPrecio.setOpaque(false);
        filaPrecio.add(crearLabelFiltro("Precio:"));
        JButton btn50 = crearBotonFiltro("≥$50");
        btn50.addActionListener(e -> mostrarEnLista(historial.filterByPriceRange(50, Float.MAX_VALUE)));
        filaPrecio.add(btn50);

        JButton btn100 = crearBotonFiltro("≥$100");
        btn100.addActionListener(e -> mostrarEnLista(historial.filterByPriceRange(100, Float.MAX_VALUE)));
        filaPrecio.add(btn100);

        JButton btnRango = crearBotonFiltro("≥Rango Personalizado");
        btnRango.addActionListener(e -> abrirDialogoRangoPrecio());
        filaPrecio.add(btnRango);

        panel.add(filaPrecio);

        JPanel filaFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaFechas.setOpaque(false);
        filaFechas.add(crearLabelFiltro("Período:"));

        String hoy = LocalDate.now().toString();
        JTextField campoDesde = new JTextField(hoy, 10);
        campoDesde.setToolTipText("Formato: yyyy-MM-dd");
        filaFechas.add(campoDesde);

        filaFechas.add(new JLabel("Hasta"));

        JTextField campoHasta = new JTextField(10);
        campoHasta.setToolTipText("Formato: yyyy-MM-dd");
        filaFechas.add(campoHasta);

        JButton btnAplicarFechas = crearBotonFiltro("Aplicar");
        btnAplicarFechas.addActionListener(e -> aplicarFiltroFechas(campoDesde, campoHasta));

        campoDesde.addActionListener(btnAplicarFechas.getActionListeners()[0]);
        campoHasta.addActionListener(btnAplicarFechas.getActionListeners()[0]);
        filaFechas.add(btnAplicarFechas);

        panel.add(filaFechas);

        JPanel filaEstudiante = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaEstudiante.setOpaque(false);
        filaEstudiante.add(crearLabelFiltro("Estudiante:"));
        JComboBox<String> tipoBusqueda = new JComboBox<>(new String[] { "Nombre", "ID" });
        JTextField campoBusqueda = new JTextField(15);
        JButton btnBuscar = crearBotonFiltro("Buscar");
        btnBuscar.addActionListener(
                e -> aplicarFiltroEstudiante((String) tipoBusqueda.getSelectedItem(), campoBusqueda.getText()));
        campoBusqueda.addActionListener(e -> btnBuscar.doClick());
        
        filaEstudiante.add(tipoBusqueda);
        filaEstudiante.add(campoBusqueda);
        filaEstudiante.add(btnBuscar);

        panel.add(filaEstudiante);

        return panel;
    }

    private void aplicarFiltroEstudiante(String tipo, String valor) {
        String texto = valor.trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa un nombre o ID para buscar.",
                    "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LinkedList<transaccion> resultados;

        if ("ID".equals(tipo)) {
            resultados = historial.filterByStudentId(texto);
        } else {
            resultados = historial.filterByStudentName(texto);
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron transacciones para ese " + tipo.toLowerCase() + ".",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        mostrarEnLista(resultados);
    }

    private void aplicarFiltroFechas(JTextField campoDesde, JTextField campoHasta) {
        String textoDesde = campoDesde.getText().trim();
        String textoHasta = campoHasta.getText().trim();

        if (textoDesde.isEmpty() || textoHasta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresar ambas fechas.",
                    "Campos vacios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate desde = LocalDate.parse(textoDesde);
            LocalDate hasta = LocalDate.parse(textoHasta);

            if (desde.isAfter(hasta)) {
                JOptionPane.showMessageDialog(this, "La fecha 'Desde' debe ser anterior o igual a 'Hasta'.",
                        "Rango Invalido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate inicio = desde;
            LocalDate fin = hasta.plusDays(1);

            mostrarEnLista(historial.filterByDateRange(inicio, fin));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato inválido. Usa yyyy-MM-dd (ejemplo: 2026-05-06).",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel crearLabelFiltro(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(UiColors.SECUNDAY_TEXT);
        label.setFont(UiFonts.DEFAULT_FONT);
        label.setPreferredSize(new java.awt.Dimension(180, 28));
        return label;
    }

    private JButton crearBotonFiltro(String texto) {
        JButton boton = new RoundedButton(texto, 20);
        boton.setForeground(UiColors.TEXT);
        boton.setFont(UiFonts.BUTTON_BOLD_MEDIUM.deriveFont(12f));
        return boton;
    }

    private void mostrarDetalles(transaccion trans) {

        StringBuilder detalle = new StringBuilder();
        detalle.append("ID: ").append(trans.getId()).append("\n")
                .append("Estado: ").append(trans.getEstado()).append("\n\n")

                .append("--- ESTUDIANTE ---\n")
                .append("Nombre: ").append(trans.getEstudiante().getNombre()).append("\n")
                .append("ID: ").append(trans.getEstudiante().getId_estudiante()).append("\n")
                .append("Correo: ").append(trans.getEstudiante().getCorreo()).append("\n")
                .append("Telefono: ").append(trans.getEstudiante().getTelefono()).append("\n\n")

                .append("--- ESPACIO ---\n")
                .append("Espacio #: ").append(trans.getEspacioId()).append("\n")
                .append("Tipo: ").append(trans.getTipoEspacio()).append("\n")
                .append("Precio/hora: $").append(trans.getTipoEspacio().getPrecioPorHora()).append("\n\n")

                .append("--- HORARIO ---\n")
                .append("Fecha: ").append(trans.getFechaReservado()).append("\n")
                .append("Día: ").append(trans.getDia()).append("\n")
                .append("Horario: ").append(trans.getHorario()).append("\n")
                .append("Horas: ").append(trans.getHoras()).append("\n\n")

                .append("--- SERVICIOS ADICIONALES ---\n")
                .append(serviciosToString(trans.getServiciosExtra())).append("\n")
                .append("--- PAGO ---\n")
                .append("Total: $").append(String.format("%.2f", trans.getCantidad())).append("\n\n")

                .append("--- METADATOS ---\n")
                .append("Creada: ").append(trans.getFechaDeTransaccion().format(DATE_FORMAT)).append("\n")
                .append("Última modificación: ").append(trans.getUltimaFechaDeModificacion().format(DATE_FORMAT))
                .append("\n");

        detallePanel.setText(detalle.toString());
        detallePanel.setCaretPosition(0);
    }

    private void abrirDialogoRangoPrecio() {
        boolean running = true;

        while (running) {
            JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
            JTextField campoMin = new JTextField(8);
            JTextField campoMax = new JTextField(8);
            panel.add(new JLabel("Minimo:"));
            panel.add(campoMin);
            panel.add(new JLabel("Maximo:"));
            panel.add(campoMax);

            int resultado = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Filtrar por rango de precio",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (resultado != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                float min = Float.parseFloat(campoMin.getText().trim());
                float max = Float.parseFloat(campoMax.getText().trim());
                if (min > max) {
                    JOptionPane.showMessageDialog(this,
                            "El mínimo debe ser menor o igual al máximo.",
                            "Rango inválido", JOptionPane.WARNING_MESSAGE);
                } else {
                    mostrarEnLista(historial.filterByPriceRange(min, max));
                    running = false;
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingresa números válidos.",
                        "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String serviciosToString(List<servicios_especiales.tipo_servicio> servicios) {
        if (servicios.isEmpty()) {
            return "Ninguno\n";
        }
        StringBuilder resultado = new StringBuilder();
        for (servicios_especiales.tipo_servicio s : servicios) {
            resultado.append("- ").append(s.name()).append(" ($").append(s.getPrecio()).append(")\n");
        }
        return resultado.toString();
    }
}
