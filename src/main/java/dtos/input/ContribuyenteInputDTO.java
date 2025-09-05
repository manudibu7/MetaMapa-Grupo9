package dtos.input;

import lombok.Data;

@Data
public class ContribuyenteInputDTO {
    String nombre;
    String apellido;
    int edad;
    boolean anonimo;
}
