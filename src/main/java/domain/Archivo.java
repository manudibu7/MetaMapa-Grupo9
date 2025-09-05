package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Archivo {
    long id;
    TipoMedia tipo;
    String url;
    String tamanio;

    public void setTipoFromString(String tipo) {
        this.tipo = TipoMedia.valueOf(tipo.toUpperCase());
    }
}
