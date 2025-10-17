package com.metamapa.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HechoOutputDTO {
    String titulo;
    String descripcion;
    LocalDate fecha;
    UbicacionOutputDTO ubicacion;
    String categoria;
    AdjuntoOutputDTO adjunto; // puede tener varios adjunto
}
