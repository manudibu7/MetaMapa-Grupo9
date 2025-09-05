package dtos.input;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HechoInputDTO {
    String titulo;
    String descripcion;
    Date fecha;
    UbicacionInputDTO ubicacion;
    // el adjunto se agrega despues
    // fechaDeCarga se completa cuando se sube a la bbdd central
    //origen: lo llena el agregador
    String etiqueta;
}
