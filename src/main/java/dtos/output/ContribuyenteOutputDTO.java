package dtos.output;

import lombok.Data;

@Data
public class ContribuyenteOutputDTO {
    String nombre;
    String apellido;
    int edad;
    boolean anonimo;
    long id;
}