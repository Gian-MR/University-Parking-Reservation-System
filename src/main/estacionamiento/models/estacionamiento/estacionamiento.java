package main.estacionamiento.models.estacionamiento;

//imports
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.time.LocalDate;

import main.estacionamiento.models.auto.auto;
import main.estacionamiento.models.espacio.espacio;
import main.estacionamiento.models.estudiante.estudiante;
import main.estacionamiento.models.servicios_especiales.servicios_especiales.tipo_servicio;
import main.estacionamiento.models.transaccion.transaccion;
import main.estacionamiento.models.transaccion.transaccionHistorial;

//* Clase que representa el estacionamiento con sus atributos y métodos para gestionar espacios, reservaciones, historial y lista de espera
//* Utiliza estructuras de datos como HashSet para espacios disponibles, HashMap para reservaciones, LinkedList para historial y lista de espera, y Stack para acciones realizadas
//* Ademas funcionara como el modelo principal del sistema de estacionamiento, integrando las clases auto, espacio y estudiante para gestionar la lógica del negocio relacionada con el estacionamiento
public class estacionamiento {
    public static final int HORA_APERTURA = 7;
    public static final int HORA_CIERRE = 17;

    //! Disponibilidad
    private Set<espacio> espaciosDisponibles;                    //? Conjunto de espacios de estacionamiento disponibles, utilizando un HashSet para garantizar la unicidad y eficiencia en las operaciones de búsqueda y eliminación

    //! Reservaciones
    private LinkedHashMap<auto, espacio> reservaciones;          //? Mapa que relaciona cada auto con su espacio reservado, utilizando un HashMap para permitir una búsqueda rápida de la reservación de un auto específico
    private HashMap<auto, String> diasReservados;                //? Mapa que guarda el día seleccionado para cada reservación
    private HashMap<auto, String> horariosReservados;            //? Mapa que guarda el rango de horario de cada reservación
    private HashMap<estudiante, auto> estudiantesAutos;          //? Mapa que relaciona cada estudiante con su auto reservado

    //! Lista de espera
    private Queue<estudiante> listaEspera;                       //? Queue que representa la lista de espera de estudiantes para obtener un espacio de estacionamiento, utilizando una LinkedList para implementar la estructura de datos de Queue
    private HashMap<estudiante, auto> esperaAutos;               //? Mapa temporal para almacenar el auto asociado a un estudiante en lista de espera
    private HashMap<estudiante, LocalDate> esperaFechas;         //? Dia reservado por el estudiante en espera
    private HashMap<estudiante, String> esperaHorarios;          //? Horario reservado por el estudiante en espera

    //! Historial y acciones
    private Stack<String> acciones;                              //? Stack que almacena las acciones realizadas en el estacionamiento, permitiendo deshacer la última acción realizada 
    private Stack<ReservacionCancelada> cancelacionesPendientes; //? Stack que almacena las cancelaciones realizadas para poder deshacerlas
    private HashMap<auto, transaccion> autoTransacciones;        //? Mapa que relaciona cada auto con su transacción correspondiente
    private final transaccionHistorial transaccionHistorial;

    //! Configuración
    private final int TOTAL_ESPACIOS_INICIALES = 200;


    //* Constructor para inicializar las estructuras de datos del estacionamiento
    public estacionamiento() {
        this.espaciosDisponibles = new LinkedHashSet<>();        
        this.reservaciones = new LinkedHashMap<>();                    
        this.diasReservados = new HashMap<>();                   
        this.horariosReservados = new HashMap<>();            
        this.listaEspera = new LinkedList<>();                   
        this.acciones = new Stack<>();                           
        this.cancelacionesPendientes = new Stack<>();
        this.estudiantesAutos = new HashMap<>();                 
        this.esperaAutos = new HashMap<>();                      
        this.esperaFechas = new HashMap<>();                     
        this.esperaHorarios = new HashMap<>();                   
        this.autoTransacciones = new HashMap<>();                
        this.transaccionHistorial = new transaccionHistorial();
        inicializarEspacios();
    }

    //* Crea 200 espacios al iniciar el sistema.
    //* Distribucion: 100 Regular, 50 VIP y 50 Electrico.
    private void inicializarEspacios() {
        for (int numero = 1; numero <= TOTAL_ESPACIOS_INICIALES; numero++) {
            espacio.tipo_espacio tipo = obtenerTipoPorNumero(numero);
            agregarEspacio(new espacio(numero, false, tipo));
        }
    }

    private espacio.tipo_espacio obtenerTipoPorNumero(int numeroEspacio) {
        if (numeroEspacio <= 100) {
            return espacio.tipo_espacio.Regular;
        }
        if (numeroEspacio <= 150) {
            return espacio.tipo_espacio.VIP;
        }
        return espacio.tipo_espacio.Electrico;
    }

    //* Método para agregar un nuevo espacio de estacionamiento al conjunto de espacios disponibles, solo si el espacio no está ocupado
    public void agregarEspacio(espacio nuevoEspacio) {
        if (nuevoEspacio != null && !nuevoEspacio.isOcupado()) {
            espaciosDisponibles.add(nuevoEspacio);
        }
    }

    //* Método para reservar un espacio y guardar tanto día como rango horario seleccionado
    public boolean reservarEspacio(auto carro, espacio espacioReservado, String diaReservado, String horarioReservado) {
        if (carro == null || espacioReservado == null) {
            return false;
        }
        if (espacioReservado.isOcupado() || !espaciosDisponibles.contains(espacioReservado)) {
            return false;
        }
        espacioReservado.setOcupado(true);
        espaciosDisponibles.remove(espacioReservado);
        reservaciones.put(carro, espacioReservado);
        diasReservados.put(carro, diaReservado);
        horariosReservados.put(carro, horarioReservado);
        acciones.push("Reservar");
        return true;
    }

    //* Método para reservar un espacio guardando también la relación entre estudiante y auto
    public boolean reservarEspacio(estudiante estudiante, auto carro, espacio espacioReservado, LocalDate fechaReserva, String diaReservado, String horarioReservado) {
        boolean reservado = reservarEspacio(carro, espacioReservado, diaReservado, horarioReservado);
        if (reservado && estudiante != null) {
            estudiantesAutos.put(estudiante, carro);
        }
        return reservado;
    }
    
    public transaccion reservarEspacio(estudiante estudiante, auto carro, espacio espacioReservado, LocalDate fechaReservado,
                                   String horarioReservado,
                                   int horas,
                                   List<tipo_servicio> serviciosExtra) {
    String diaReservado = diaSemanaEspañol(fechaReservado);
    boolean reservado = reservarEspacio(carro, espacioReservado, diaReservado, horarioReservado);
    
    if (!reservado) {
        return null;
    }
    if (estudiante != null) {
        estudiantesAutos.put(estudiante, carro);
    }

    transaccion t = new transaccion(
        espacioReservado.getTipo(),
        serviciosExtra,
        horas,
        espacioReservado.getNumero_espacio(),
        estudiante,
        fechaReservado,
        diaReservado,
        horarioReservado
    );
    transaccionHistorial.addTransaccion(t);
    autoTransacciones.put(carro, t);
    return t;
}
    private String diaSemanaEspañol(LocalDate fecha) {
    switch (fecha.getDayOfWeek()) {
        case MONDAY:    return "Lunes";
        case TUESDAY:   return "Martes";
        case WEDNESDAY: return "Miercoles";
        case THURSDAY:  return "Jueves";
        case FRIDAY:    return "Viernes";
        case SATURDAY:  return "Sabado";
        case SUNDAY:    return "Domingo";
        default:        return "";
    }
}



    //* Método para buscar el auto por la tablilla
    public auto buscarAutoPorTablilla(String tablilla) {
        for (auto carro : reservaciones.keySet()) {
            if (carro.getTablilla().equalsIgnoreCase(tablilla)) {
                return carro;
            }
        }
        return null;
    }

    //* Método para buscar el auto por el número de estudiante
    public auto buscarAutoPorNumeroEstudiante(String numeroEstudiante) {
        for (estudiante est : estudiantesAutos.keySet()) {
            if (est.getId_estudiante().equalsIgnoreCase(numeroEstudiante)) {
                return estudiantesAutos.get(est);
            }
        }
        return null;
    }

    //* Método para buscar el estudiante por el número de estudiante
    public estudiante buscarEstudiantePorNumero(String numeroEstudiante) {
        for (estudiante est : estudiantesAutos.keySet()) {
            if (est.getId_estudiante().equalsIgnoreCase(numeroEstudiante)) {
                return est;
            }
        }
        return null;
    }

    //* Sobrecarga para agregar a la lista de espera junto con el auto y la reserva solicitada
    public void agregarAListaEspera(estudiante estudianteEnEspera, auto carro, LocalDate fechaReserva, String horarioReservado) {
        if (estudianteEnEspera != null) {
            listaEspera.offer(estudianteEnEspera);
            if (carro != null) esperaAutos.put(estudianteEnEspera, carro);
            if (fechaReserva != null) esperaFechas.put(estudianteEnEspera, fechaReserva);
            if (horarioReservado != null) esperaHorarios.put(estudianteEnEspera, horarioReservado);
        }
    }

    //* Método para atender a un estudiante de la lista de espera, removiendo al estudiante del frente de la cola y actualizando el historial de acciones
    public estudiante atenderListaEspera() {
        estudiante siguiente = listaEspera.poll();
        return siguiente;
    }

    //* Obtener y remover el auto asociado a un estudiante que estaba en espera
    public auto obtenerAutoEnEspera(estudiante est) {
        if (est == null) return null;
        return esperaAutos.remove(est);
    }

    public LocalDate obtenerFechaEnEspera(estudiante est) {
        if (est == null) return null;
        return esperaFechas.remove(est);
    }

    public String obtenerHorarioEnEspera(estudiante est) {
        if (est == null) return null;
        return esperaHorarios.remove(est);
    }

    //* Cancela la reservación y devuelve el espacio liberado 
    public espacio cancelarReservacionYDevolverEspacio(auto carro, boolean aplicarCargo) {
        if (carro == null || !reservaciones.containsKey(carro)) {
            return null;
        }
        espacio espacioLiberado = reservaciones.get(carro);
        String diaReservado = diasReservados.get(carro);
        String horarioReservado = horariosReservados.get(carro);
        transaccion trans = autoTransacciones.get(carro);
        cancelacionesPendientes.push(new ReservacionCancelada(carro, espacioLiberado, diaReservado, horarioReservado, trans));
        if (trans != null) {
            if (aplicarCargo){
                trans.agregarCargo(10);
            }
            trans.setEstado(transaccion.EstadoTransaccion.Cancelada);
        }
        autoTransacciones.remove(carro);
        reservaciones.remove(carro);
        espacioLiberado.setOcupado(false);
        espaciosDisponibles.add(espacioLiberado);
        diasReservados.remove(carro);
        horariosReservados.remove(carro);

        estudiante estEliminar = null;
        for (estudiante est : estudiantesAutos.keySet()) {
            if (estudiantesAutos.get(est).equals(carro)) {
                estEliminar = est;
                break;
            }
        }
        if (estEliminar != null) {
            estudiantesAutos.remove(estEliminar);
        }

        acciones.push("Cancelar");
        return espacioLiberado;
    }

    //* Getters para acceder espacios disponibles, reservaciones, historial, lista de espera y acciones realizadas
    public Set<espacio> getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    public LinkedHashMap<auto, espacio> getReservaciones() {
        return reservaciones;
    }

     public String getDiaReservado(auto carro) {
         return diasReservados.get(carro);
     }

    public String getHorarioReservado(auto carro) {
        return horariosReservados.get(carro);
    }

    public HashMap<auto, String> getDiasReservados() {
        return diasReservados;
    }

    public HashMap<auto, String> getHorariosReservados() {
        return horariosReservados;
    }

    public Queue<estudiante> getListaEspera() {
        return listaEspera;
    }

    public Stack<String> getAcciones() {
        return acciones;
    }

    //* Método para buscar un espacio disponible de un tipo específico
    public espacio buscarEspacioDisponiblePorTipo(espacio.tipo_espacio tipo) {
        if (tipo == null || espaciosDisponibles.isEmpty()) {
            return null;
        }
        for (espacio e : espaciosDisponibles) {
            if (e.getTipo() == tipo && !e.isOcupado()) {
                return e;
            }
        }
        return null;
    }

    //* Revierte los efectos de la acción especificada 
    public boolean deshacerAccion(String accion) {
        switch (accion) {
            case "Reservar":
                return deshacerReservacion();
            case "Cancelar":
                return deshacerCancelacion();
            default:
                return false;
        }
    }
    
    //* Método auxiliar para deshacer una reservación 
    private boolean deshacerReservacion() {
        if (reservaciones.isEmpty()) {
            return false;
        }
        
        auto ultimoAuto = null;
        espacio ultimoEspacio = null;
        
        for (Map.Entry<auto, espacio> entry : reservaciones.entrySet()) {
            ultimoAuto = entry.getKey();
            ultimoEspacio = entry.getValue();
        }
        
        if (ultimoAuto != null && ultimoEspacio != null) {
            // Liberar el espacio
            ultimoEspacio.setOcupado(false);
            espaciosDisponibles.add(ultimoEspacio);
            
            // Eliminar la reservación
            reservaciones.remove(ultimoAuto);
            diasReservados.remove(ultimoAuto);
            horariosReservados.remove(ultimoAuto);

            // Remover Transacciones
            transaccion trans = autoTransacciones.remove(ultimoAuto);
            if (trans != null) {
                transaccionHistorial.removerTransaccion(trans);
            }
            
            // Eliminar relación estudiante-auto si existe
            estudiante estEliminar = null;
            for (estudiante est : estudiantesAutos.keySet()) {
                if (estudiantesAutos.get(est).equals(ultimoAuto)) {
                    estEliminar = est;
                    break;
                }
            }
            if (estEliminar != null) {
                estudiantesAutos.remove(estEliminar);
            }
                        
            return true;
        }
        
        return false;
    }
    
    //* Método auxiliar para deshacer una cancelación (restaurar la última cancelación)
    private boolean deshacerCancelacion() {
        if (cancelacionesPendientes.isEmpty()) {
            return false;
        }
        
        ReservacionCancelada cancelacion = cancelacionesPendientes.pop();
        
        // Restaurar la reservación
        cancelacion.espacio.setOcupado(true);
        espaciosDisponibles.remove(cancelacion.espacio);
        reservaciones.put(cancelacion.carro, cancelacion.espacio);
        diasReservados.put(cancelacion.carro, cancelacion.diaReservado);
        horariosReservados.put(cancelacion.carro, cancelacion.horarioReservado);
        
        if (cancelacion.trans != null){
            cancelacion.trans.setEstado(transaccion.EstadoTransaccion.Activa);
            autoTransacciones.put(cancelacion.carro, cancelacion.trans);            
            transaccionHistorial.addTransaccion(cancelacion.trans);
        }
        return true;
    }
    
    //* Clase interna para almacenar información de una cancelación y poder deshacerla
    private static class ReservacionCancelada {
        auto carro;
        espacio espacio;
        String diaReservado;
        String horarioReservado;
        transaccion trans;
        
        ReservacionCancelada(auto carro, espacio espacio, String diaReservado, String horarioReservado, transaccion trans) {
            this.carro = carro;
            this.espacio = espacio;
            this.diaReservado = diaReservado;
            this.horarioReservado = horarioReservado;
            this.trans = trans;
        }
    }

    public transaccionHistorial getTransaccionHistorial(){
        return this.transaccionHistorial;
    }

    public transaccion obtenerTransaccionPorAuto(auto carro) {
        return this.autoTransacciones.get(carro);
    }
}
