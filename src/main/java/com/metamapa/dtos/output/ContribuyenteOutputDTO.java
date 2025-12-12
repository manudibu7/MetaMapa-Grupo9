package com.metamapa.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteOutputDTO {
    String nombre;
    String apellido;
    Integer edad;  // Cambiado de int a Integer para permitir null
    Long id;  // Cambiado de long a Long para consistencia
}
