package main.estacionamiento.models.servicios_especiales;

import java.util.ArrayList;
import java.util.List;
import main.estacionamiento.models.espacio.espacio;

//* Clase que representa servicios adicionales con costo fijo.
//* Cada servicio define para que tipos de espacio aplica.

public class servicios_especiales {

    public enum tipo_servicio {
        Chequeo_gomas(1),
        Chequeo_fluidos(2),
        Cheque_frenos_motor(5),
        Lavado_Exterior(50);

        private final int precio;

        tipo_servicio(int precio) {
            this.precio = precio;
        }

        public int getPrecio() {
            return precio;
        }

        public boolean permitidoPara(espacio.tipo_espacio tipoEspacio) {
            if (this == Chequeo_gomas) {
                return true;
            }

            if (this == Chequeo_fluidos) {
                return tipoEspacio == espacio.tipo_espacio.Regular || tipoEspacio == espacio.tipo_espacio.VIP;
            }

            if (this == Cheque_frenos_motor) {
                return tipoEspacio == espacio.tipo_espacio.VIP;
            }

            if (this == Lavado_Exterior) {
                return tipoEspacio == espacio.tipo_espacio.VIP || tipoEspacio == espacio.tipo_espacio.Electrico;
            }

            return false;
        }
    }

    //* Devuelve los servicios permitidos para el tipo de espacio indicado.
    public static List<tipo_servicio> serviciosDisponiblesPara(espacio.tipo_espacio tipoEspacio) {
        List<tipo_servicio> resultado = new ArrayList<>();
        for (tipo_servicio servicio : tipo_servicio.values()) {
            if (servicio.permitidoPara(tipoEspacio)) {
                resultado.add(servicio);
            }
        }
        return resultado;
    }

    //* Suma los costos de una coleccion de servicios.
    public static int calcularCostoTotal(List<tipo_servicio> servicios) {
        int total = 0;
        if (servicios == null) {
            return total;
        }

        for (tipo_servicio servicio : servicios) {
            total += servicio.getPrecio();
        }
        return total;
    }

}
