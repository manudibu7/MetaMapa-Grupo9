package com.metamapa.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribucionOutputDTO {
    long idContribuyente;
    HechoOutputDTO hecho;
    long idContribucion;
}
