package domain;

public class HechoTexto extends Hecho{
    public String informacion;

    public HechoTexto(String titulo, String descripcion, String categoria, String latitud, String longitud, String fecha) {
        super(titulo, descripcion, categoria, latitud, longitud, fecha);
    }
}
