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
    int edad;
    boolean anonimo;
    long id;
}
