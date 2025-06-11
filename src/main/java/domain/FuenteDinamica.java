package domain;

import java.util.ArrayList;
import java.util.List;

public class FuenteDinamica extends Fuente {
    private List<Hecho> hechosCargados = new ArrayList<>();

    public void agregarHecho(Hecho hecho) {
        hechosCargados.add(hecho);
    }

    @Override
    public List<Hecho> obtenerHechos() {
        return hechosCargados;
    }
}