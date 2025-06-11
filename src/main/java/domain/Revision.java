package domain;

public class Revision {

    private String mensaje;
    private EnumEstado estado;
    private Contribuyente responsable;

    //Primera instancia de la revision en Contribucion con msj default y estado Pendiente
    public Revision(Contribuyente responsable) {
        this.mensaje = "El hecho está pendiente de revisión";
        this.estado = EnumEstado.PENDIENTE;
        this.responsable = responsable;
    }

    public Revision(String mensaje, EnumEstado estado, Contribuyente responsable) {
        this.mensaje = mensaje;
        this.estado = estado;
        this.responsable = responsable;
    }

    public String getMensaje() {
        return mensaje;
    }

    public EnumEstado getEstado(){
        return estado;
    }

    public Contribuyente getResponsable() {
        return responsable;
    }

    public void setEstado(EnumEstado nuevoEstado) {
        this.estado = nuevoEstado;
    }
    public void setMensaje(String mensaje) {this.mensaje = mensaje;}

}