package main.estacionamiento.app.ventanas_segundarias;

// imports
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

// imports de clases propias 
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.models.auto.auto;
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.utils.UiWindowUtils;

//? Clase que representa la ventana secundaria para mostrar la disponibilidad de espacios en el estacionamiento, permitiendo al usuario ver un resumen de los espacios disponibles por tipo, así como una lista detallada de cada espacio disponible, con opciones para actualizar la información y cerrar la ventana.

public class Disponibilidad extends JFrame {
    private estacionamiento estacionamientoRef;
    private JTextArea textAreaResults;

    public Disponibilidad(estacionamiento estacionamientoRef) {
        super("Espacios Disponibles");
        this.estacionamientoRef = estacionamientoRef;

        // * Configuracion de ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(UiSizes.SECONDARY_WINDOW_WIDTH, UiSizes.SECONDARY_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        UiWindowUtils.applySecondaryWindowBaseStyle(this);
        add(UiWindowUtils.createTitlePanel("Espacios Disponibles"), BorderLayout.NORTH);

        // * Area de texto para mostrar los espacios disponibles
        textAreaResults = new JTextArea();
        textAreaResults.setEditable(false);
        textAreaResults.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        textAreaResults.setBackground(UiColors.PANEL_ACTIONS);
        textAreaResults.setForeground(UiColors.PANEL_TITLE);

        JScrollPane scrollPane = new JScrollPane(textAreaResults);
        RoundedPanel contenedorResultados = new RoundedPanel(24, UiColors.PANEL_ACTIONS);
        contenedorResultados.setLayout(new BorderLayout());
        contenedorResultados.setBorder(new EmptyBorder(10, 10, 10, 10));
        contenedorResultados.add(scrollPane, BorderLayout.CENTER);
        add(contenedorResultados, BorderLayout.CENTER);

        // * Panel con botones de accion
        JPanel panelButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        panelButtons.setOpaque(false);
        panelButtons.add(createButton("Actualizar", this::actualizarDisponibilidad));
        panelButtons.add(createButton("Cerrar", this::cerrarVentana));
        add(panelButtons, BorderLayout.SOUTH);

        // * Mostrar informacion al abrir la ventana
        actualizarDisponibilidad();
    }

    // * Metodo para crear botones con estilo repetido
    private JButton createButton(String text, Runnable action) {
        JButton button = new RoundedButton(text, 20);
        button.setForeground(UiColors.TEXT);
        button.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        button.setMargin(UiSizes.buttonMargin());
        button.addActionListener(event -> action.run());
        return button;
    }

    // * Metodo para actualizar y mostrar los espacios disponibles
    private void actualizarDisponibilidad() {
        StringBuilder result = new StringBuilder();

        // * Obtener espacios disponibles desde el estacionamiento
        Set<espacio> espaciosDisponibles = estacionamientoRef.getEspaciosDisponibles();
        int totalEspacios = espaciosDisponibles.size();

        // * Mostrar la cantidad total de espacios disponibles
        result.append("Horario del parking: 7:00 AM - 5:00 PM\n");
        result.append("Horario operativo (24h)\n\n");
        result.append("Total de espacios disponibles: ").append(totalEspacios).append("\n\n");

        // * Mostrar mensaje si no hay espacios disponibles
        if (totalEspacios == 0) {
            result.append("No hay espacios disponibles en este momento.\n");
        } else {
            // * Contar espacios por tipo
            int regular = 0;
            int vip = 0;
            int electric = 0;

            for (espacio e : espaciosDisponibles) {
                String type = e.getTipo().toString();
                if (type.equals("Regular")) {
                    regular++;
                } else if (type.equals("VIP")) {
                    vip++;
                } else if (type.equals("Electrico")) {
                    electric++;
                }
            }

            // * Mostrar cantidad por tipo de espacio
            result.append("ESPACIOS POR TIPO:\n");
            result.append("----------------------------------------------------\n");
            result.append("   Regular: ").append(regular).append(" espacios\n");
            result.append("   VIP: ").append(vip).append(" espacios\n");
            result.append("   Electrico: ").append(electric).append(" espacios\n\n");

            result.append("======================================================");
            result.append("\nRESERVACIONES POR DÍA (Lunes - Viernes):\n");
            result.append("----------------------------------------------------\n");

            HashMap<auto, espacio> reservaciones = estacionamientoRef.getReservaciones();
            HashMap<auto, String> diasReservados = estacionamientoRef.getDiasReservados();
            HashMap<auto, String> horariosReservados = estacionamientoRef.getHorariosReservados();

            if (reservaciones.isEmpty()) {
                result.append("   No hay reservaciones activas.\n");
            } else {
                String[] dias = { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes" };

                for (String diaBuscado : dias) {
                    int totalDelDia = 0;
                    for (String diaGuardado : diasReservados.values()) {
                        if (diaBuscado.equals(diaGuardado)) {
                            totalDelDia++;
                        }
                    }

                    result.append(diaBuscado).append(": ").append(totalDelDia).append(" reservaciones\n");

                    for (Map.Entry<auto, espacio> entry : reservaciones.entrySet()) {
                        auto carro = entry.getKey();
                        espacio espacioReservado = entry.getValue();
                        String dia = diasReservados.get(carro);

                        if (diaBuscado.equals(dia)) {
                            result.append("   - Espacio #").append(espacioReservado.getNumero_espacio())
                                    .append(" | Tablilla: ").append(carro.getTablilla())
                                    .append(" | Tipo: ").append(espacioReservado.getTipo())
                                    .append(" | Horario: ")
                                    .append(horariosReservados.getOrDefault(carro, "Sin horario"))
                                    .append("\n");
                        }
                    }
                    if (totalDelDia == 0) {
                        result.append("   Sin reservaciones para este día.\n");
                    }
                    result.append("\n");
                }
            }
        }
        result.append("======================================================\n");

        // * Mostrar el resultado en el area de texto
        textAreaResults.setText(result.toString());
        textAreaResults.setCaretPosition(0);
    }

    // * Metodo para cerrar la ventana
    private void cerrarVentana() {
        dispose();
    }

}
