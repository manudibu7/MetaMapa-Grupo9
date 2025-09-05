package dtos.output;

import lombok.Data;

@Data
public class HechoOutputDTO {
    String titulo;
    String descripcion;
    String fecha;
    UbicacionOutputDTO ubicacion;
    String etiqueta;
    AdjuntoOutputDTO adjunto; // puede tener varios adjunto
}
