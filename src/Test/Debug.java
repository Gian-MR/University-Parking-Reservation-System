package Test;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import main.estacionamiento.models.auto.auto;
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estudiante.estudiante;
import main.estacionamiento.models.estacionamiento.estacionamiento;
import main.estacionamiento.models.servicios_especiales.servicios_especiales;

public class Debug {
    public static void debugPrefill(estacionamiento modelo) {
        if (modelo == null)
            return;

        Set<espacio> availSet = modelo.getEspaciosDisponibles();
        if (availSet == null)
            return;

        List<espacio> toOccupy = new ArrayList<>();

        int regular = 0, vip = 0, electrico = 0;

        for (espacio e : availSet) {
            switch (e.getTipo()) {
                case Regular:
                    if (regular < 50) {
                        toOccupy.add(e);
                        regular++;
                    }
                    break;
                case VIP:
                    if (vip < 25) {
                        toOccupy.add(e);
                        vip++;
                    }
                    break;
                case Electrico:
                    if (electrico < 50) {
                        toOccupy.add(e);
                        electrico++;
                    }
                    break;
            }
        }

        LocalDate fechaReservado = LocalDate.now();
        String horarioReservado = "08:00 - 09:00";

        int index = 1;

        for (espacio e : toOccupy) {
            estudiante est = new estudiante(
                    "Debug " + index,
                    "Debug " + index,
                    "Correo Debug",
                    "123-456-789");

            auto carro = new auto(
                    "Debug",
                    "Car",
                    "2026",
                    "Debug " + index);

            modelo.reservarEspacio(
                    est,
                    carro,
                    e,
                    fechaReservado,
                    horarioReservado,
                    1,
                    new ArrayList<servicios_especiales.tipo_servicio>());
            index++;
        }
    }
}
