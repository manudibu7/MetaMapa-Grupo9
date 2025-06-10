package domain;

public class HechoMultimedia extends Hecho {
    public String rutaAlContenido;

    public HechoMultimedia(String titulo, String descripcion, String categoria, String latitud, String longitud, String fecha) {
        super(titulo, descripcion, categoria, latitud, longitud, fecha);
    }
}
