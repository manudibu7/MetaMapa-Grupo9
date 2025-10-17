package com.metamapa.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HechoInputDTO {
    String titulo;
    String descripcion;
    LocalDate fecha;
    UbicacionInputDTO ubicacion;
    // el adjunto se agrega despues
    // fechaDeCarga se completa cuando se sube a la bbdd central
    //origen: lo llena el agregador
    String categoria;
}
