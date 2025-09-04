package domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Revision {

    private String mensaje;
    private EstadoRevision estado;
    private Contribuyente responsable;
    private LocalDate fecha;

    //Primera instancia de la revision en Contribucion con msj default y estado Pendiente
    public Revision(Contribuyente responsable) {
        this.mensaje = "El hecho está pendiente de revisión";
        this.estado = new Pendiente();
        this.responsable = responsable;
    }

    public Revision(String mensaje, EstadoRevision estado, Contribuyente responsable) {
        this.mensaje = mensaje;
        this.estado = estado;
        this.responsable = responsable;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Contribuyente getResponsable() {
        return responsable;
    }

    public void setMensaje(String mensaje) {this.mensaje = mensaje;}
}