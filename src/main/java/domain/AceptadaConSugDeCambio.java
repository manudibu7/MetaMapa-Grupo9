package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AceptadaConSugDeCambio extends EstadoRevision {
    private String sugerencia;

    public String getsugerencia(){return sugerencia;}
    public void setsugerencia(String mensaje) {sugerencia = mensaje;}
    public EnumEstadoHecho devolverEstadoHecho() {return EnumEstadoHecho.DADO_DE_ALTA;}
}
