package main.estacionamiento.app.ventanas_segundarias;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

// imports de clases propias 
import main.estacionamiento.models.auto.auto;
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.models.estudiante.estudiante;
import main.estacionamiento.models.servicios_especiales.servicios_especiales;
import main.estacionamiento.models.transaccion.transaccion;
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.utils.UiWindowUtils;

//? Clase que representa la ventana secundaria para cancelar una reservaciรณn existente, permitiendo al usuario ingresar la tรกbilla del auto o el nรบmero de estudiante para buscar y cancelar la reservaciรณn correspondiente, ademรกs de gestionar la liberaciรณn del espacio y la asignaciรณn a estudiantes en lista de espera si es necesario.

public class Cancelacion_Cambiar extends JFrame implements ActionListener {
    private JLabel labelTablilla;
    private JTextField fieldTablilla;
    private JLabel labelNumeroEstudiante;
    private JTextField fieldNumeroEstudiante;
    private JButton buttonCancelar;
    private JButton buttonCambiar;

    private estacionamiento estacionamientoModel;

    public Cancelacion_Cambiar(estacionamiento estacionamientoModel) {
        this.estacionamientoModel = estacionamientoModel;
        init();
    }

    private void init() {
        setTitle("Cancelar o Cambiar Reservaciรณn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UiWindowUtils.applySecondaryWindowBaseStyle(this);
        add(UiWindowUtils.createTitlePanel("Cambiar o Cancelar"), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        labelTablilla = new JLabel("Tablilla del Auto:");
        fieldTablilla = new JTextField();

        labelNumeroEstudiante = new JLabel("Número de Estudiante:");
        fieldNumeroEstudiante = new JTextField();

        buttonCancelar = createButton("Cancelar Reservación");
        buttonCambiar = createButton("Cambiar Reservación");

        buttonCancelar.addActionListener(this);
        buttonCambiar.addActionListener(this);

        // Panel central con los campos
        RoundedPanel camposPanel = new RoundedPanel(24, UiColors.PANEL_ACTIONS);
        camposPanel.setLayout(new java.awt.GridLayout(4, 2, 10, 10));
        camposPanel.setOpaque(false);
        camposPanel.setBorder(new EmptyBorder(14, 14, 14, 14));
        camposPanel.add(labelTablilla);
        camposPanel.add(fieldTablilla);
        camposPanel.add(labelNumeroEstudiante);
        camposPanel.add(fieldNumeroEstudiante);

        panel.add(camposPanel, BorderLayout.CENTER);

        // Panel para los botones
        RoundedPanel botonesPanel = new RoundedPanel(24, UiColors.PANEL_TRANSPARENT);
        botonesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(new EmptyBorder(14, 14, 0, 14));
        botonesPanel.add(buttonCambiar);
        botonesPanel.add(buttonCancelar);

        panel.add(botonesPanel, BorderLayout.SOUTH);

        add(panel);
        setSize(UiSizes.SECONDARY_WINDOW_WIDTH, UiSizes.SECONDARY_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new RoundedButton(text, 20);
        button.setForeground(UiColors.TEXT);
        button.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        button.setMargin(UiSizes.buttonMargin());
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonCambiar) {
            cambiarReservacion();
        } else if (e.getSource() == buttonCancelar) {
            String tablilla = fieldTablilla.getText().trim();
            String numeroEstudiante = fieldNumeroEstudiante.getText().trim();

            // Validar que AMBOS campos estén llenos
            if (tablilla.isEmpty() || numeroEstudiante.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingrese la tablilla del auto y el número de estudiante.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar auto por tablilla
            auto autoPorTablilla = estacionamientoModel.buscarAutoPorTablilla(tablilla);

            // Buscar auto por número de estudiante
            auto autoPorEstudiante = estacionamientoModel.buscarAutoPorNumeroEstudiante(numeroEstudiante);

            // Verificar que ambos existan y apunten al mismo auto
            if (autoPorTablilla == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró una reservación con esa tablilla.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (autoPorEstudiante == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró una reservación con ese número de estudiante.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!autoPorTablilla.equals(autoPorEstudiante)) {
                JOptionPane.showMessageDialog(this,
                        "La tablilla y el número de estudiante no corresponden a la misma reservación.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Si todo coincide, cancelar la reservación
            auto autoBuscado = autoPorTablilla;
            espacio espacioLiberado = estacionamientoModel.cancelarReservacionYDevolverEspacio(autoBuscado, true);

            if (espacioLiberado != null) {
                JOptionPane.showMessageDialog(this,
                        "Reservación cancelada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                if (!estacionamientoModel.getListaEspera().isEmpty()) {
                    estudiante siguiente = estacionamientoModel.atenderListaEspera();
                    if (siguiente != null) {
                        auto carroEnEspera = estacionamientoModel.obtenerAutoEnEspera(siguiente);
                        LocalDate fechaEnEspera = estacionamientoModel.obtenerFechaEnEspera(siguiente);
                        String horarioEnEspera = estacionamientoModel.obtenerHorarioEnEspera(siguiente);

                        if (carroEnEspera != null && fechaEnEspera != null) {
                            transaccion trans = estacionamientoModel.reservarEspacio(
                                    siguiente, carroEnEspera, espacioLiberado,
                                    fechaEnEspera, horarioEnEspera,
                                    1, // default 1 hour — waitlist didn't capture this
                                    new ArrayList<>()); // no extra services

                            if (trans != null) {
                                JOptionPane.showMessageDialog(this,
                                        "Espacio asignado al estudiante en lista de espera: " + siguiente.getNombre(),
                                        "Lista de Espera", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "No se pudo asignar el espacio al estudiante en lista de espera.",
                                        "Lista de Espera", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Estudiante en lista de espera no tiene auto o fecha registrada.",
                                    "Lista de Espera", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo cancelar la reservación. Intente nuevamente.");
            }
        }
    }

    private LocalDate proximaFechaParaDia(String nombreDia) {
        java.time.DayOfWeek objetivo;
        switch (nombreDia.toLowerCase()) {
            case "lunes":
                objetivo = java.time.DayOfWeek.MONDAY;
                break;
            case "martes":
                objetivo = java.time.DayOfWeek.TUESDAY;
                break;
            case "miercoles":
                objetivo = java.time.DayOfWeek.WEDNESDAY;
                break;
            case "jueves":
                objetivo = java.time.DayOfWeek.THURSDAY;
                break;
            case "viernes":
                objetivo = java.time.DayOfWeek.FRIDAY;
                break;
            default:
                return LocalDate.now();
        }
        LocalDate fecha = LocalDate.now();
        while (fecha.getDayOfWeek() != objetivo) {
            fecha = fecha.plusDays(1);
        }
        return fecha;
    }

    private void cambiarReservacion() {
        String tablilla = fieldTablilla.getText().trim();
        String numeroEstudiante = fieldNumeroEstudiante.getText().trim();

        if (tablilla.isEmpty() || numeroEstudiante.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese la tablilla del auto Y el número de estudiante.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buscar auto por tablilla
        auto autoPorTablilla = estacionamientoModel.buscarAutoPorTablilla(tablilla);

        // Buscar auto por número de estudiante
        auto autoPorEstudiante = estacionamientoModel.buscarAutoPorNumeroEstudiante(numeroEstudiante);

        // Verificar que ambos existan y apunten al mismo auto
        if (autoPorTablilla == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró una reservación con esa tablilla.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (autoPorEstudiante == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró una reservación con ese número de estudiante.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!autoPorTablilla.equals(autoPorEstudiante)) {
            JOptionPane.showMessageDialog(this,
                    "La tablilla y el número de estudiante no corresponden a la misma reservación.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la reservación actual
        auto autoBuscado = autoPorTablilla;
        espacio espacioActual = estacionamientoModel.getReservaciones().get(autoBuscado);
        String diaActual = estacionamientoModel.getDiaReservado(autoBuscado);
        String horarioActual = estacionamientoModel.getHorarioReservado(autoBuscado);
        estudiante estudianteActual = estacionamientoModel.buscarEstudiantePorNumero(numeroEstudiante);
        transaccion transaccionActual = estacionamientoModel.obtenerTransaccionPorAuto(autoBuscado);

        ArrayList<servicios_especiales.tipo_servicio> serviciosEspeciales = new ArrayList<>();
        if (transaccionActual != null && transaccionActual.getServiciosExtra() != null) {
            serviciosEspeciales.addAll(transaccionActual.getServiciosExtra());
        }

        if (espacioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró una reservación válida.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar opciones de cambio
        String[] opcionesCambio = { "Cambiar Día", "Cambiar Horario", "Cambiar Tipo de Espacio" };
        int opcion = JOptionPane.showOptionDialog(this,
                "Seleccione qué desea cambiar de la reservación:\n\n" +
                        "Espacio actual: #" + espacioActual.getNumero_espacio() + " (" + espacioActual.getTipo() + ")\n"
                        +
                        "Día: " + diaActual + "\n" +
                        "Horario: " + horarioActual,
                "Cambiar Reservación",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesCambio,
                opcionesCambio[0]);

        boolean cambioExitoso = false;

        switch (opcion) {
            case 0:
                String fechaIngresada = JOptionPane.showInputDialog(this,
                        "Ingrese la nueva fecha (YYYY-MM-DD).",
                        "Cambiar Día",
                        JOptionPane.QUESTION_MESSAGE);

                if (fechaIngresada != null) {
                    String fechaNormalizada = fechaIngresada.trim();
                    if (fechaNormalizada.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Debe ingresar una fecha válida.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }

                    LocalDate nuevaFecha;
                    try {
                        nuevaFecha = LocalDate.parse(fechaNormalizada);
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Formato inválido. Use YYYY-MM-DD",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }

                    if (nuevaFecha.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || nuevaFecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                        JOptionPane.showMessageDialog(this,
                                "Solo se permiten reservaciones de lunes a viernes.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }

                    estacionamientoModel.cancelarReservacionYDevolverEspacio(autoBuscado, false);
                    transaccion t = estacionamientoModel.reservarEspacio(
                            estudianteActual, autoBuscado, espacioActual,
                            nuevaFecha, horarioActual,
                            1, serviciosEspeciales);
                    if (t != null){
                        t.agregarCargo(6);
                    }
                    cambioExitoso = (t != null);
                }
                break;

            case 1:
                String[] horarios = { "07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00",
                        "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00",
                        "15:00 - 16:00", "16:00 - 17:00" };
                Object horarioSeleccionado = JOptionPane.showInputDialog(this,
                        "Seleccione el nuevo horario:",
                        "Cambiar Horario",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        horarios,
                        horarioActual);

                if (horarioSeleccionado != null) {
                    LocalDate fechaActual = proximaFechaParaDia(diaActual);
                    estacionamientoModel.cancelarReservacionYDevolverEspacio(autoBuscado, false);
                    transaccion t = estacionamientoModel.reservarEspacio(
                            estudianteActual, autoBuscado, espacioActual,
                            fechaActual, horarioSeleccionado.toString(),
                            1, serviciosEspeciales);
                    if (t != null){
                        t.agregarCargo(6);
                    }
                    cambioExitoso = (t != null);
                }
                break;

            case 2:
                espacio.tipo_espacio[] tipos = espacio.tipo_espacio.values();
                Object tipoSeleccionado = JOptionPane.showInputDialog(this,
                        "Seleccione el nuevo tipo de espacio:",
                        "Cambiar Tipo de Espacio",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tipos,
                        espacioActual.getTipo());

                if (tipoSeleccionado != null) {
                    espacio.tipo_espacio nuevoTipo = (espacio.tipo_espacio) tipoSeleccionado;
                    espacio nuevoEspacio = estacionamientoModel.buscarEspacioDisponiblePorTipo(nuevoTipo);

                    if (nuevoEspacio != null) {
                        LocalDate fechaActual = proximaFechaParaDia(diaActual);
                        estacionamientoModel.cancelarReservacionYDevolverEspacio(autoBuscado, false);
                        transaccion t = estacionamientoModel.reservarEspacio(
                                estudianteActual, autoBuscado, nuevoEspacio,
                                fechaActual, horarioActual,
                                1, serviciosEspeciales);
                        if (t != null){
                            t.agregarCargo(6);
                        }
                        cambioExitoso = (t != null);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No hay espacios disponibles del tipo seleccionado.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
        }

        if (cambioExitoso) {
            JOptionPane.showMessageDialog(this,
                    "Reservación cambiada exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
