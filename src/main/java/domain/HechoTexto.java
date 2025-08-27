package domain;

import java.time.LocalDate;

public class HechoTexto extends Hecho{
    public String informacion;

    public HechoTexto(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fecha) {
        super(titulo, descripcion, categoria, ubicacion, fecha);
    }
}
