package main.estacionamiento.utils;

import java.awt.Font;

//* Esta clase define una colección de fuentes personalizadas para la interfaz del sistema de estacionamiento.
//* Se utiliza para mantener una consistencia en el estilo de texto en toda la aplicación, facilitando el mantenimiento y la personalización futura.

public final class UiFonts {
    private static final String FAMILY_SANS = "SansSerif";

    public static final Font TITLE_BOLD_LARGE = new Font(FAMILY_SANS, Font.BOLD, 40);
    public static final Font BUTTON_BOLD_MEDIUM = new Font(FAMILY_SANS, Font.BOLD, 16);
    public static final Font DEFAULT_FONT = new Font(FAMILY_SANS, Font.PLAIN, 14);
}
