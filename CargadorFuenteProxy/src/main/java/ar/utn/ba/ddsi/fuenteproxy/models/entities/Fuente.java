package ar.utn.ba.ddsi.fuenteproxy.models.entities;

import java.net.URL;

public class Fuente {
    private Long id;
    private URL url;
    private EnumTipoFuenteProxy tipoFuenteProxy;

    public Fuente(Long id, URL url, EnumTipoFuenteProxy tipoFuenteProxy) {
        this.id = id;
        this.url = url;
        this.tipoFuenteProxy = tipoFuenteProxy;}
}
