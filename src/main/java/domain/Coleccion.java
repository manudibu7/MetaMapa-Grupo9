package domain;

import java.util.ArrayList;
import java.util.List;

public class Coleccion {

    private String titulo;
    private String descripcion;
    private Fuente fuente;
    private List<Condicion> criterioDePertenencia;
    private List<Hecho> hechos;
    private String handle;

    public Coleccion(String titulo, String descripcion, Fuente fuente) {

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
        this.criterioDePertenencia = new ArrayList<>();
        this.hechos = new ArrayList<>();

    }

    public cambiarCriterioDePertenencia(List<Condicion> criterio) {
        CriterioDePertenencia = criterio;
    }
    //revisar
    public List<Hecho> obtenerHechos() {
        return this.fuente.obtenerHecho()
    }
}
