package dtos.input;

import lombok.Data;

import java.util.Date;

@Data
public class HechoInputDTO {
    String titulo;
    String descripcion;
    Date fecha;
    UbicacionInputDTO ubicacion;
    String lugarDeOcurrencia;
    // el adjunto se agrega despues
    // fechaDeCarga se completa cuando se sube a la bbdd central
    //origen???
    //etiquetas??
}
