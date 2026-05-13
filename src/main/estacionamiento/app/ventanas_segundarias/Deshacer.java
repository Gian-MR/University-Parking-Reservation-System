package main.estacionamiento.app.ventanas_segundarias;

// imports
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// imports de clases propias 
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.utils.RoundedButton;
import main.estacionamiento.utils.RoundedPanel;
import main.estacionamiento.utils.UiColors;
import main.estacionamiento.utils.UiFonts;
import main.estacionamiento.utils.UiSizes;
import main.estacionamiento.utils.UiWindowUtils;

//? Clase que representa la ventana secundaria para deshacer la última acción realizada en el sistema.
//? Utiliza el Stack de acciones del modelo estacionamiento para identificar y revertir la última operación.

public class Deshacer extends JFrame implements ActionListener {
    private JLabel labelUltimaAccion;
    private JLabel labelDescripcionAccion;
    private JButton buttonDeshacer;
    private JButton buttonCancelar;

    private estacionamiento estacionamientoModel;

    public Deshacer(estacionamiento estacionamientoModel) {
        this.estacionamientoModel = estacionamientoModel;
        init();
    }

    private void init() {
        setTitle("Deshacer Reservación o Cancelación");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UiWindowUtils.applySecondaryWindowBaseStyle(this);
        add(UiWindowUtils.createTitlePanel("Deshacer Acción"), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Mostrar la última acción realizada
        String ultimaAccion = obtenerUltimaAccion();
        labelUltimaAccion = new JLabel("Última Reservación o Cancelación:");
        labelUltimaAccion.setFont(UiFonts.BUTTON_BOLD_MEDIUM);
        labelUltimaAccion.setForeground(UiColors.PANEL_TITLE);

        labelDescripcionAccion = new JLabel(ultimaAccion);
        labelDescripcionAccion.setFont(UiFonts.DEFAULT_FONT);
        labelDescripcionAccion.setForeground(UiColors.SECUNDAY_TEXT);

        // Panel para mostrar la información de la acción
        RoundedPanel infoPanel = new RoundedPanel(24, UiColors.PANEL_ACTIONS);
        infoPanel.setLayout(new java.awt.GridLayout(2, 1, 10, 10));
        infoPanel.setBorder(new EmptyBorder(14, 14, 14, 14));
        infoPanel.setOpaque(false);
        infoPanel.add(labelUltimaAccion);
        infoPanel.add(labelDescripcionAccion);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Botones de acción
        buttonDeshacer = createButton("Deshacer Acción");
        buttonCancelar = createButton("Cancelar");

        buttonDeshacer.addActionListener(this);
        buttonCancelar.addActionListener(this);

        RoundedPanel botonesPanel = new RoundedPanel(24, UiColors.PANEL_TRANSPARENT);
        botonesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));
        botonesPanel.setBorder(new EmptyBorder(14, 14, 0, 14));
        botonesPanel.setOpaque(false);
        botonesPanel.add(buttonDeshacer);
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

    private String obtenerUltimaAccion() {
        if (estacionamientoModel.getAcciones().isEmpty()) {
            return "No hay acciones para deshacer";
        }
        return estacionamientoModel.getAcciones().peek();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonDeshacer) {
            deshacerUltimaAccion();
        } else if (e.getSource() == buttonCancelar) {
            dispose();
        }
    }

    private void deshacerUltimaAccion() {
        // Verificar si hay acciones para deshacer
        if (estacionamientoModel.getAcciones().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay acciones para deshacer.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String ultimaAccion = estacionamientoModel.getAcciones().peek();

        // Confirmar la acción de deshacer
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea deshacer la última acción: \"" + ultimaAccion + "\"?",
                "Confirmar Deshacer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar el deshacer en el historial de transacciones y en el modelo
        try {
            // Remover la acción del stack
            estacionamientoModel.getAcciones().pop();

            // Luego deshacer la acción en el modelo estacionamiento
            boolean undoExitoso = estacionamientoModel.deshacerAccion(ultimaAccion);

            if (undoExitoso) {
                JOptionPane.showMessageDialog(this,
                        "Acción deshecha exitosamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Si no se pudo deshacer en el modelo, mostramos advertencia pero la
                // transacción ya fue removida
                JOptionPane.showMessageDialog(this,
                        "La transacción fue removida del historial, pero no se pudo revertir completamente la acción.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Actualizar la interfaz
            labelDescripcionAccion.setText(obtenerUltimaAccion());
        } catch (IllegalStateException | UnsupportedOperationException ex) {
            // Volver a poner la acción en el stack
            estacionamientoModel.getAcciones().push(ultimaAccion);
            JOptionPane.showMessageDialog(this,
                    "No se pudo deshacer la acción. Intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}