package main.estacionamiento.models.auto;

//* Clase que representa un auto con sus atributos básicos

public class auto {
    private String marca;
    private String modelo;
    private String año;
    private String tablilla;

    //* Constructor para inicializar un auto con su marca, modelo, año y placa
    public auto(String marca, String modelo, String año, String tablilla) {
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.tablilla = tablilla;
    }

    //* Getters methods para cada atributo
    public String getMarca() {
        return marca;
    }
    
    public String getModelo() {
        return modelo;
    }

    public String getAño() {
        return año;
    }
    
    public String getTablilla() {
        return tablilla;
    }
}
    