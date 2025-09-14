package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Archivo {
    long id;
    TipoMedia tipo;
    String url;
    String tamanio;

    public void setTipoFromString(String tipo) {
        this.tipo = TipoMedia.valueOf(tipo.toUpperCase());
    }
}
