package domain;


import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Revision {

    private String mensaje = "El hecho está pendiente de revisión";
    private EstadoRevision estado = EstadoRevision.PENDIENTE;
    private Contribuyente responsable;
    private LocalDate fecha;
    private Contribucion contribucion;
}