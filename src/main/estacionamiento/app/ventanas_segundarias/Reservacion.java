package main.estacionamiento.app.ventanas_segundarias;

// imports de Java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// imports de clases propias
import main.estacionamiento.models.auto.auto;
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estudiante.estudiante;
import main.estacionamiento.models.servicios_especiales.servicios_especiales;
import main.estacionamiento.models.transaccion.transaccion;
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.utils.UiWindowUtils;

public class Reservacion extends JFrame {
    private estacionamiento estacionamiento;

    // * Enum para representar los días de la semana
    private JTextField FechaReserva;

    // * Componentes del formulario - Datos del Estudiante
    private JTextField Nombre;
    private JTextField IdEstudiante;
    private JTextField Correo;
    private JTextField Telefono;

    // * Componentes del formulario - Datos del Auto
    private JTextField Tablilla;
    private JTextField Marca;
    private JTextField Modelo;
    private JTextField Año;

    // * Componentes del formulario - Datos de la Reservación
    private JComboBox<espacio.tipo_espacio> TipoEspacio;
    private JSpinner Horas;
    private JSpinner HoraEntrada;
    private JPanel panelServicios;
    private List<servicios_especiales.tipo_servicio> serviciosDisponibles;
    private List<JCheckBox> checkboxesServicios;

    public Reservacion(estacionamiento estacionamiento) {
        super("Reservación de Espacio");
        this.estacionamiento = estacionamiento;
        this.checkboxesServicios = new ArrayList<>();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(UiSizes.SECONDARY_WINDOW_WIDTH, UiSizes.SECONDARY_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        UiWindowUtils.applySecondaryWindowBaseStyle(this);

        // * Panel principal con scroll
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(UiColors.BACKGROUND_MAIN);
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // * Título
        add(UiWindowUtils.createTitlePanel("Reservación de Espacio"), BorderLayout.NORTH);

        // * Secciones del formulario
        panelPrincipal.add(crearSeccionEstudiante());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionAuto());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionReservacion());
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearPanelBotones());

        // * ScrollPane
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBackground(UiColors.BACKGROUND_MAIN);
        scrollPane.getViewport().setBackground(UiColors.BACKGROUND_MAIN);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    // * Crear la sección del formulario para los datos del estudiante, con campos
    // para nombre, ID, correo y teléfono.
    private JPanel crearSeccionEstudiante() {
        RoundedPanel panel = new RoundedPanel(15, UiColors.PANEL_ACTIONS);
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(crearLabel("Nombre:"));
        Nombre = new JTextField(15);
        panel.add(Nombre);

        panel.add(crearLabel("ID Estudiante:"));
        IdEstudiante = new JTextField(15);
        panel.add(IdEstudiante);

        panel.add(crearLabel("Correo:"));
        Correo = new JTextField(15);
        panel.add(Correo);

        panel.add(crearLabel("Teléfono:"));
        Telefono = new JTextField(15);
        panel.add(Telefono);

        return panel;
    }

    // * Crear la sección del formulario para los datos del auto, con campos para
    // tablilla, marca, modelo y año.
    private JPanel crearSeccionAuto() {
        RoundedPanel panel = new RoundedPanel(15, UiColors.PANEL_ACTIONS);
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(crearLabel("Tablilla:"));
        Tablilla = new JTextField(15);
        panel.add(Tablilla);

        panel.add(crearLabel("Marca:"));
        Marca = new JTextField(15);
        panel.add(Marca);

        panel.add(crearLabel("Modelo:"));
        Modelo = new JTextField(15);
        panel.add(Modelo);

        panel.add(crearLabel("Año:"));
        Año = new JTextField(15);
        panel.add(Año);

        return panel;
    }

    // * Crear la sección del formulario para los datos de la reservación, con
    // opciones para seleccionar el tipo de espacio, cantidad de horas y servicios
    // adicionales disponibles según el tipo de espacio seleccionado.
    private JPanel crearSeccionReservacion() {
        RoundedPanel panel = new RoundedPanel(15, UiColors.PANEL_ACTIONS);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // * Tipo de espacio
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTipo.setOpaque(false);
        panelTipo.add(crearLabel("Tipo de Espacio:"));
        TipoEspacio = new JComboBox<>(espacio.tipo_espacio.values());
        TipoEspacio.addActionListener(e -> actualizarServiciosDisponibles());
        panelTipo.add(TipoEspacio);
        panel.add(panelTipo);
        panel.add(Box.createVerticalStrut(10));

        // * Horario de entrada
        JPanel panelHorarioParking = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelHorarioParking.setOpaque(false);
        panelHorarioParking.add(crearLabel("Horario del parking: 7:00 AM - 5:00 PM"));
        panel.add(panelHorarioParking);
        panel.add(Box.createVerticalStrut(10));

        JPanel panelHoraEntrada = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelHoraEntrada.setOpaque(false);
        panelHoraEntrada.add(crearLabel("Hora de Entrada:"));
        HoraEntrada = new JSpinner(new SpinnerNumberModel(
                main.estacionamiento.models.estacionamiento.estacionamiento.HORA_APERTURA,
                main.estacionamiento.models.estacionamiento.estacionamiento.HORA_APERTURA,
                main.estacionamiento.models.estacionamiento.estacionamiento.HORA_CIERRE - 1,
                1));
        panelHoraEntrada.add(HoraEntrada);
        panelHoraEntrada.add(crearLabel("(formato 24h)"));
        panel.add(panelHoraEntrada);
        panel.add(Box.createVerticalStrut(10));

        // * Cantidad de horas
        JPanel panelHoras = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelHoras.setOpaque(false);
        panelHoras.add(crearLabel("Cantidad de Horas:"));
        Horas = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        HoraEntrada.addChangeListener(e -> actualizarMaximoHoras());
        panelHoras.add(Horas);
        panel.add(panelHoras);
        panel.add(Box.createVerticalStrut(10));

        // * Dia selecionado
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFecha.setOpaque(false);
        panelFecha.add(crearLabel("Fecha de Reservacion:"));
        FechaReserva = new JTextField(10);
        FechaReserva.setText(LocalDate.now().toString());
        FechaReserva.setToolTipText("Formato: YYYY-MM-DD (dias lunes a viernes)");
        panelFecha.add(FechaReserva);
        panelFecha.add(crearLabel("(YYYY-MM-DD)"));
        panel.add(panelFecha);
        panel.add(Box.createVerticalStrut(10));

        // * Servicios especiales
        panel.add(crearLabel("Servicios Adicionales:"));
        panelServicios = new JPanel();
        panelServicios.setLayout(new BoxLayout(panelServicios, BoxLayout.Y_AXIS));
        panelServicios.setOpaque(false);
        actualizarServiciosDisponibles();
        actualizarMaximoHoras();
        panel.add(panelServicios);

        return panel;
    }

    // * Método para actualizar el máximo de horas disponibles en el spinner de horas según la hora de entrada seleccionada
    private void actualizarMaximoHoras() {
        int horasDisponibles = main.estacionamiento.models.estacionamiento.estacionamiento.HORA_CIERRE - (int) HoraEntrada.getValue();
        int maxHoras = Math.min(8, horasDisponibles);

        SpinnerNumberModel modeloHoras = (SpinnerNumberModel) Horas.getModel();
        modeloHoras.setMaximum(maxHoras);

        int horasActuales = (int) Horas.getValue();
        if (horasActuales > maxHoras) {
            Horas.setValue(maxHoras);
        }
    }

    // * Método para actualizar la lista de servicios adicionales disponibles según
    // el tipo de espacio seleccionado
    private void actualizarServiciosDisponibles() {
        panelServicios.removeAll();
        checkboxesServicios.clear();
        serviciosDisponibles = servicios_especiales
                .serviciosDisponiblesPara((espacio.tipo_espacio) TipoEspacio.getSelectedItem());

        for (servicios_especiales.tipo_servicio servicio : serviciosDisponibles) {
            JCheckBox checkBox = new JCheckBox(servicio.name() + " ($" + servicio.getPrecio() + ")");
            checkBox.setOpaque(false);
            panelServicios.add(checkBox);
            checkboxesServicios.add(checkBox);
        }
        panelServicios.revalidate();
        panelServicios.repaint();
    }

    // * Obtiene solo los servicios adicionales que el usuario marco en la interfaz.
    private List<servicios_especiales.tipo_servicio> obtenerServiciosSeleccionados() {
        List<servicios_especiales.tipo_servicio> seleccionados = new ArrayList<>();

        for (int i = 0; i < checkboxesServicios.size() && i < serviciosDisponibles.size(); i++) {
            if (checkboxesServicios.get(i).isSelected()) {
                seleccionados.add(serviciosDisponibles.get(i));
            }
        }

        return seleccionados;
    }

    // * Crear el panel de botones para procesar la reservación o cancelar, con
    // estilos personalizados y acciones asociadas.
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton Reservar = new RoundedButton("Reservar", 20);
        Reservar.setForeground(UiColors.TEXT);
        Reservar.setMargin(UiSizes.buttonMargin());
        Reservar.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        Reservar.addActionListener(e -> procesarReservacion());

        JButton Cancelar = new RoundedButton("Cancelar", 20);
        Cancelar.setForeground(UiColors.TEXT);
        Cancelar.setMargin(UiSizes.buttonMargin());
        Cancelar.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        Cancelar.addActionListener(e -> dispose());

        panel.add(Reservar);
        panel.add(Cancelar);

        return panel;
    }

    // * Método auxiliar para crear etiquetas con estilo consistente.
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UiFonts.DEFAULT_FONT);
        label.setForeground(UiColors.SECUNDAY_TEXT);
        return label;
    }

    // * Método para procesar la reservación, validando los datos ingresados,
    // buscando un espacio disponible del tipo seleccionado
    private void procesarReservacion() {
        // ! Validar que todos los campos estén llenos
        if (!validarFormulario()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos", "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ! Crear objetos estudiante y auto
        estudiante est = new estudiante(
                Nombre.getText(),
                IdEstudiante.getText(),
                Correo.getText(),
                Telefono.getText());

        auto carro = new auto(
                Marca.getText(),
                Modelo.getText(),
                Año.getText(),
                Tablilla.getText());

        LocalDate fechaReserva;

        try {
            fechaReserva = LocalDate.parse(FechaReserva.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha invalida. Formato: yyyy-MM-dd (ejemplo: 2026-05-01).",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fechaReserva.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                    "No se puede reservar con fecha pasada.",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fechaReserva.getDayOfWeek().getValue() > 5) {
            JOptionPane.showMessageDialog(this,
                    "Solo possible reservar de lunes a viernes.",
                    "Día inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int horaEntrada = (int) HoraEntrada.getValue();
        int horas = (int) Horas.getValue();
        int horaSalida = horaEntrada + horas;
        List<servicios_especiales.tipo_servicio> serviciosSeleccionados = obtenerServiciosSeleccionados();

        String horarioSeleccionado = String.format("%02d:00 - %02d:00", horaEntrada, horaSalida);

        // ! Buscar un espacio disponible del tipo seleccionado
        espacio espacioDisponible = buscarEspacioDisponible((espacio.tipo_espacio) TipoEspacio.getSelectedItem());

        if (espacioDisponible == null) {
            mostrarDialogoSinEspacio(est, carro, fechaReserva, horarioSeleccionado);
            return;
        }
        transaccion trans = estacionamiento.reservarEspacio(est, carro, espacioDisponible, fechaReserva,
                horarioSeleccionado, horas, serviciosSeleccionados);
        if (trans != null) {
            mostrarResumenReservacion(est, carro, espacioDisponible, fechaReserva, horarioSeleccionado);
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al procesar la reservación. Intenta nuevamente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("DEBUG: tipo=" + espacioDisponible.getTipo()
                + " precio/h=" + espacioDisponible.getPrecioPorHora()
                + " horas=" + horas
                + " servicios=" + serviciosSeleccionados
                + " total=" + trans.getCantidad());
    }

    // * Método para validar que todos los campos del formulario estén llenos antes
    // de procesar la reservación
    private boolean validarFormulario() {
        return !Nombre.getText().trim().isEmpty() &&
                !IdEstudiante.getText().trim().isEmpty() &&
                !Correo.getText().trim().isEmpty() &&
                !Telefono.getText().trim().isEmpty() &&
                !Tablilla.getText().trim().isEmpty() &&
                !Marca.getText().trim().isEmpty() &&
                !Modelo.getText().trim().isEmpty() &&
                !Año.getText().trim().isEmpty();
    }

    private espacio buscarEspacioDisponible(espacio.tipo_espacio tipo) {
        return estacionamiento.buscarEspacioDisponiblePorTipo(tipo);
    }

    private void mostrarDialogoSinEspacio(estudiante est, auto carro, LocalDate fechaReserva,
            String horarioSeleccionado) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "No hay espacios disponibles del tipo seleccionado.\n¿Deseas añadirte a la lista de espera?",
                "Sin Espacio Disponible",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            estacionamiento.agregarAListaEspera(est, carro, fechaReserva, horarioSeleccionado);
            JOptionPane.showMessageDialog(this, "Estudiante añadido a la lista de espera.", "Confirmación",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        }
    }

    // * Método para mostrar un resumen detallado de la reservación
    private void mostrarResumenReservacion(estudiante est, auto carro, espacio espacioReservado, LocalDate FechaReserva,
            String horarioSeleccionado) {
        int horas = (int) Horas.getValue();
        int precioPorHora = espacioReservado.getPrecioPorHora();
        int subtotalEspacio = horas * precioPorHora;
        List<servicios_especiales.tipo_servicio> serviciosSeleccionados = obtenerServiciosSeleccionados();
        int totalServicios = servicios_especiales.calcularCostoTotal(serviciosSeleccionados);
        int totalPago = subtotalEspacio + totalServicios;

        // * Se uso StringBuilder pq es mejor y mas eficiente para concatenar muchas
        // lineas de texto
        StringBuilder resumen = new StringBuilder();
        resumen.append("Reservación Exitosa!\n\n");
        resumen.append("--- DATOS DEL ESTUDIANTE ---\n");
        resumen.append("Nombre: ").append(est.getNombre()).append("\n");
        resumen.append("ID: ").append(est.getId_estudiante()).append("\n");
        resumen.append("Correo: ").append(est.getCorreo()).append("\n");
        resumen.append("Teléfono: ").append(est.getTelefono()).append("\n\n");

        resumen.append("--- DATOS DEL AUTO ---\n");
        resumen.append("Tablilla: ").append(carro.getTablilla()).append("\n");
        resumen.append("Marca: ").append(carro.getMarca()).append("\n");
        resumen.append("Modelo: ").append(carro.getModelo()).append("\n");
        resumen.append("Año: ").append(carro.getAño()).append("\n\n");

        resumen.append("--- DATOS DE LA RESERVACIÓN ---\n");
        resumen.append("Espacio #: ").append(espacioReservado.getNumero_espacio()).append("\n");
        resumen.append("Tipo: ").append(espacioReservado.getTipo()).append("\n");
        resumen.append("Fecha: ").append(FechaReserva).append("\n");
        resumen.append("Horario: ").append(horarioSeleccionado).append("\n");
        resumen.append("Horas: ").append(horas).append("\n");
        resumen.append("Precio/Hora: $").append(precioPorHora).append("\n");
        resumen.append("Subtotal Espacio: $").append(subtotalEspacio).append("\n");
        resumen.append("Servicios Especiales: $").append(totalServicios).append("\n");
        resumen.append("Total a Pagar: $").append(totalPago).append("\n");

        JOptionPane.showMessageDialog(this, resumen.toString(), "Resumen de Reservación",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // * Método para limpiar el formulario después de procesar una reservación
    private void limpiarFormulario() {
        Nombre.setText("");
        IdEstudiante.setText("");
        Correo.setText("");
        Telefono.setText("");
        Tablilla.setText("");
        Marca.setText("");
        Modelo.setText("");
        Año.setText("");
        HoraEntrada.setValue(main.estacionamiento.models.estacionamiento.estacionamiento.HORA_APERTURA);
        Horas.setValue(1);
        FechaReserva.setText(LocalDate.now().toString());
        TipoEspacio.setSelectedIndex(0);
        actualizarServiciosDisponibles();
    }
}