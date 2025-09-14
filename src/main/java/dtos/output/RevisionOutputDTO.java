package dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionOutputDTO {
    Long idContribucion;
    String estado;
    String mensaje;
    //no hace falta el revisor?
}
