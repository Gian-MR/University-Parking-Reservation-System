package main.estacionamiento.models.transaccion;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estudiante.estudiante;
import main.estacionamiento.models.servicios_especiales.servicios_especiales;
import main.estacionamiento.models.servicios_especiales.servicios_especiales.tipo_servicio;


//? Mostly a data class
public class transaccion {

    private static int contador = 1;
    private int id;

    private espacio.tipo_espacio tipoEspacio;
    private List<tipo_servicio> serviciosExtra;

    private float cantidad;
    private int horas;
    private int espacioId;
    private String dia;
    private String horario;
    private estudiante estudiante;
    private LocalDate fechaReservado;
    private LocalDateTime fechaDeTransaccion;
    private LocalDateTime fechaDeUltimaModificacion;
    private EstadoTransaccion estado;
    private float cargosAdicionales = 0;


    public transaccion(espacio.tipo_espacio tipoEspacio,
            List<tipo_servicio> serviciosExtra,
            int horas,
            int espacioId,
            estudiante estudiante,
            LocalDate fechaReservado,
            String dia,
            String horario) {
        this.id = contador++;
        this.tipoEspacio = tipoEspacio;
        this.serviciosExtra = serviciosExtra;
        this.horas = horas;
        this.espacioId = espacioId;
        this.estudiante = estudiante;
        this.fechaReservado = fechaReservado;
        this.dia = dia;
        this.horario = horario;
        this.fechaDeTransaccion = LocalDateTime.now();
        this.fechaDeUltimaModificacion = this.fechaDeTransaccion;
        this.estado = EstadoTransaccion.Activa;

        // use type of space for price
        this.cantidad = tipoEspacio.getPrecioPorHora() * horas
                + servicios_especiales.calcularCostoTotal(serviciosExtra);
    }

    // DBMS transaction states:
    public enum EstadoTransaccion {
        Activa,
        Cancelada,
    }

    // used for collecting, should not be easily modified by the user
    public EstadoTransaccion getEstado() {
        return estado;
    }

    public int getId() {
        return this.id;
    }

    public estudiante getEstudiante() {
        return this.estudiante;
    }

    public int getEspacioId() {
        return espacioId;
    }

    public LocalDateTime getFechaDeTransaccion() {
        return this.fechaDeTransaccion;
    }

    public LocalDateTime getUltimaFechaDeModificacion() {
        return this.fechaDeUltimaModificacion;
    }

    public espacio.tipo_espacio getTipoEspacio() {
        return tipoEspacio;
    }

    public List<tipo_servicio> getServiciosExtra() {
        return this.serviciosExtra;
    }

    public float getCantidad() {
        return this.cantidad + cargosAdicionales;
    }

    public int getHoras() {
        return horas;
    }

    public String getHorario() {
        return horario;
    }

    public String getDia() {
        return dia;
    }

    public LocalDate getFechaReservado() {
        return fechaReservado;
    }

    // only setter
    public void  setEstado(EstadoTransaccion estado) {
        this.estado = estado;
        this.fechaDeUltimaModificacion = LocalDateTime.now();
    }

    public void agregarCargo(float fee){
        this.cargosAdicionales += fee;
        this.fechaDeUltimaModificacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        String base = String.format("Transaccion #%d | %s | Espacio %d (%s) | $%.2f", id, estudiante.getNombre(), espacioId,
                tipoEspacio, cantidad);

        if (estado == EstadoTransaccion.Cancelada) {
        return "[CANCELADA] " + base;
        }

        return base;
    }

    

}