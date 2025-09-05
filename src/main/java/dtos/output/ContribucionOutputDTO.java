package dtos.output;

import lombok.Data;

@Data
public class ContribucionOutputDTO {
    long idContribuyente;
    HechoOutputDTO hecho;
    long idContribucion;
}
