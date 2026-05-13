package main.estacionamiento.utils;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

// * Esta clase es utilizada para crear logos.

public final class LogoUtils {

    public static JLabel crearLogoTarzan(int ancho, int alto) {
        ImageIcon icono = new ImageIcon("src/main/resources/Tarzan.png");
        Image escalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(escalada));
    }
}