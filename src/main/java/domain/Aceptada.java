package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Aceptada extends EstadoRevision {
    public EnumEstadoHecho devolverEstadoHecho() {return EnumEstadoHecho.DADO_DE_ALTA;}
}
