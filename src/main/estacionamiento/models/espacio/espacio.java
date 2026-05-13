package main.estacionamiento.models.espacio;

//* Clase que representa un espacio de estacionamiento con sus atributos básicos

public class espacio {
    private int numero_espacio;
    private boolean ocupado;
    private tipo_espacio tipo;

    public enum tipo_espacio {
        Regular(2),
        VIP(4),
        Electrico(8);

        private final int precioPorHora;

        tipo_espacio(int precioPorHora) {
            this.precioPorHora = precioPorHora;
        }

        public int getPrecioPorHora() {
            return precioPorHora;
        }
    }
    

    //* Constructor para inicializar un espacio de estacionamiento con su número, estado de ocupación y tipo
    public espacio(int numero_espacio, boolean ocupado, tipo_espacio tipo) {
        this.numero_espacio = numero_espacio;
        this.ocupado = ocupado;
        this.tipo = tipo;
    }

    //* Getter y Setter methods 
    public int getNumero_espacio() {
        return numero_espacio;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public tipo_espacio getTipo() {
        return tipo;
    }

    public int getPrecioPorHora() {
        return tipo.getPrecioPorHora();
    }
    
}
