package main.estacionamiento.utils;

import java.awt.Dimension;
import java.awt.Insets;

//* Esta clase define una colección de tamaños y dimensiones personalizados para la interfaz del sistema de estacionamiento.
//* Se utiliza para mantener una consistencia en las dimensiones de los componentes de la interfaz, facilitando el mantenimiento y la personalización futura.

public final class UiSizes {
    public static final int MAIN_WINDOW_WIDTH = 820;
    public static final int MAIN_WINDOW_HEIGHT = 620;

    public static final int SECONDARY_WINDOW_WIDTH = 600;
    public static final int SECONDARY_WINDOW_HEIGHT = 500;

    public static final int ACTIONS_PANEL_WIDTH = 420;
    public static final int ACTIONS_PANEL_HEIGHT = 320;

    public static final int BUTTON_MARGIN_TOP = 10;
    public static final int BUTTON_MARGIN_LEFT = 14;
    public static final int BUTTON_MARGIN_BOTTOM = 10;
    public static final int BUTTON_MARGIN_RIGHT = 14;

    public static Dimension actionsPanelSize() {
        return new Dimension(ACTIONS_PANEL_WIDTH, ACTIONS_PANEL_HEIGHT);
    }

    public static Insets buttonMargin() {
        return new Insets(
            BUTTON_MARGIN_TOP,
            BUTTON_MARGIN_LEFT,
            BUTTON_MARGIN_BOTTOM,
            BUTTON_MARGIN_RIGHT
        );
    }
}
