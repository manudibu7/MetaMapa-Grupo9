package domain;

import java.util.List;

public abstract class FuenteProxy extends Fuente {
    protected Fuente fuente;

    public FuenteProxy(Fuente fuente) {
        this.fuente = fuente;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        return fuente.obtenerHechos();
    }
}