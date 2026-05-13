package main.estacionamiento.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

//* Esta clase define un boton con bordes redondeados y colores personalizados para diferentes estados (normal, hover, presionado).
//* Se utiliza para mejorar la apariencia de los botones en la interfaz del sistema de estacionamiento.

public class RoundedButton extends JButton {
    private final int radio;

    public RoundedButton(String texto, int radio) {
        super(texto);
        this.radio = radio;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color colorBase = UiColors.BUTTON_BASE;
        Color colorHover = UiColors.BUTTON_HOVER;
        Color colorPresionado = UiColors.BUTTON_PRESSED;

        if (getModel().isPressed()) {
            g2.setColor(colorPresionado);
        } else if (getModel().isRollover()) {
            g2.setColor(colorHover);
        } else {
            g2.setColor(colorBase);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        g2.dispose();

        super.paintComponent(g);
    }
}
