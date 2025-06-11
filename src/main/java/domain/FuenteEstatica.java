package domain;

import java.util.List;

public class FuenteEstatica extends Fuente {
    private String rutaDataset;
    private List<Hecho> hechos;
    private ILector lector;

    public FuenteEstatica(String rutaDataset, ILector lector) {
        this.rutaDataset = rutaDataset;
        this.lector = lector;
        this.hechos = lector.obtencionHechos(rutaDataset);
    }

    @Override
    public List<Hecho> obtenerHechos() {
        return hechos;
    }
}