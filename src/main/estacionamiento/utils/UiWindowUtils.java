package main.estacionamiento.utils;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//* Clase de utilidades para configurar estilos y componentes comunes en las ventanas secundarias del sistema de estacionamiento, proporcionando métodos para aplicar estilos base a las ventanas y crear paneles de título con diseño consistente.

public final class UiWindowUtils {

    public static void applySecondaryWindowBaseStyle(JFrame frame) {
        frame.setLayout(new BorderLayout(10, 10));
        ((JPanel) frame.getContentPane()).setBorder(new EmptyBorder(16, 16, 16, 16));
        frame.getContentPane().setBackground(UiColors.BACKGROUND_MAIN);
    }

    public static JPanel createTitlePanel(String titleText) {
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(UiFonts.TITLE_BOLD_LARGE);
        title.setForeground(UiColors.TEXT);

        RoundedPanel titleContainer = new RoundedPanel(24, UiColors.PANEL_TITLE);
        titleContainer.setLayout(new BorderLayout());
        titleContainer.setBorder(new EmptyBorder(14, 18, 14, 18));
        titleContainer.add(title, BorderLayout.CENTER);
        return titleContainer;
    }
}
