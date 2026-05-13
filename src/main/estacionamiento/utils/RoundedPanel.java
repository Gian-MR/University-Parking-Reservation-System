package main.estacionamiento.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

//* Esta clase define un panel con bordes redondeados y un color de fondo personalizado.
//* Se utiliza para mejorar la apariencia de los contenedores en la interfaz del sistema de estacionamiento, como el panel del título o el panel central de opciones.

public class RoundedPanel extends JPanel {
    private final int radio;
    private final Color colorFondo;

    public RoundedPanel(int radio, Color colorFondo) {
        this.radio = radio;
        this.colorFondo = colorFondo;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        g2.dispose();
        super.paintComponent(g);
    }
}
