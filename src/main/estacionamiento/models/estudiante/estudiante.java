package main.estacionamiento.models.estudiante;

//* Clase que representa un estudiante con sus atributos básicos

public class estudiante {
    private String nombre;
    private String id_estudiante;
    private String correo;
    private String telefono;

    //* Constructor para inicializar un estudiante con su nombre, ID, correo y teléfono
    public estudiante(String nombre, String id_estudiante, String correo, String telefono) {
        this.nombre = nombre;
        this.id_estudiante = id_estudiante;
        this.correo = correo;
        this.telefono = telefono;
    }

    //* Getters methods para cada atributo
    public String getNombre() {
        return nombre;
    }
    
    public String getId_estudiante() {
        return id_estudiante;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

}
