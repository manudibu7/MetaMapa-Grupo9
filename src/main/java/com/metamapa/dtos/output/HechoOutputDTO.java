package com.metamapa.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HechoOutputDTO {
    String titulo;
    String descripcion;
    LocalDate fecha;
    UbicacionOutputDTO ubicacion;
    String categoria;
    List<AdjuntoOutputDTO> adjuntos; // lista de adjuntos
    String tipoDeHecho; // TEXTO o MULTIMEDIA
}
