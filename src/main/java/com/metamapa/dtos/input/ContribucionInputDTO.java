package com.metamapa.dtos.input;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribucionInputDTO {

    Long idContribuyente;
    HechoInputDTO hecho;
    Boolean anonimo;
}
