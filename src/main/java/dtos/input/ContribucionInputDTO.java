package dtos.input;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribucionInputDTO {
    long idContribuyente;
    HechoInputDTO hecho;
}
