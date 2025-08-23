package domain;

public class Pendiente extends EstadoRevision {
    public EnumEstadoHecho devolverEstadoHecho() {return EnumEstadoHecho.DADO_DE_BAJA;}
    //podria haber un nuevo tipo de estado de hecho pendiente, pero implica modificar el diagrama
}
