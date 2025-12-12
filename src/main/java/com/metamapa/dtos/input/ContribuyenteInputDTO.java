package com.metamapa.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteInputDTO {
    String nombre;
    String apellido;
    Integer edad;
}
