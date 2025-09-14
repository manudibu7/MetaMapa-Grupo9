package domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class Hecho {
    private String Titulo;
    private String descripcion;
    private Categoria categoria;
    private LocalDate fecha;
    private Ubicacion lugarDeOcurrencia;
    private String origen;
    private Etiqueta etiqueta;
    private Archivo adjunto;
}
