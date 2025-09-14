package dtos.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteInputDTO {
    String nombre;
    String apellido;
    int edad;
    boolean anonimo;
}
