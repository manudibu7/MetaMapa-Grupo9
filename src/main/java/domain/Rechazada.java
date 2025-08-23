package domain;

public class Rechazada extends EstadoRevision {
    private String motivo;

    public String getMotivo(){return motivo;}
    public void setMotivo(String mensaje) {motivo = mensaje;}
    public EnumEstadoHecho devolverEstadoHecho() {return EnumEstadoHecho.DADO_DE_BAJA;}
}
