package domain;

import java.util.List;

public class FuenteEstatica extends Fuente {
    public String rutaDataset;
    //private List<Hecho> hechos;
    public ILector lector;
    private Boolean procesada;

    public FuenteEstatica(ILector lector, String rutaDataset) {
        this.rutaDataset = rutaDataset;
        this.procesada = false;
        this.lector = lector;
    }

    @Override
    public  List<Hecho> obtenerHechos(){
        return lector.obtencionHechos(this.rutaDataset);
    };

    public void setProcesada(Boolean procesada) {
        this.procesada = procesada;
    }
}