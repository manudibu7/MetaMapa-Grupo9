package domain;

import java.time.LocalDate;

public class HechoMultimedia extends Hecho {
    public String rutaAlContenido;

    public HechoMultimedia(String titulo, String descripcion, Categoria categoria,Ubicacion lugarDeOcurrencia, LocalDate fecha) {
        super(
                titulo,
                descripcion,
                categoria, // crear Categoria a partir del String
                lugarDeOcurrencia, // crear Ubicacion a partir de Strings
                fecha // convertir String a LocalDate
        );
    }
}
