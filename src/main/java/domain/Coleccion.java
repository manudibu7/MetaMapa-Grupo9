package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Coleccion {

    private String titulo;
    private String descripcion;
    private Fuente fuente;
    private List<InterfaceCondicion> criterioDePertenencia;
    private List<Hecho> hechos;
    private String handle;

    public Coleccion(String titulo, String descripcion, Fuente fuente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
        this.criterioDePertenencia = new ArrayList<>();
        this.hechos = new ArrayList<>();
    }
    public Coleccion(String titulo, String descripcion, Fuente fuente, List<InterfaceCondicion> criterio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
        this.criterioDePertenencia = criterio;
        this.hechos = new ArrayList<>();
    }

    public void cambiarCriterioDePertenencia(List<InterfaceCondicion> criterio) {
        criterioDePertenencia = criterio;
    }
    //revisar
    public void obtenerHechos() {
        hechos = this.fuente.obtenerHechos().stream()
                .filter(hecho -> criterioDePertenencia.stream()
                        .allMatch(condicion -> condicion.cumpleCondicion(hecho)))
                .collect(Collectors.toList());

    }

    public void setearFuente() {
        this.hechos.forEach((h) -> h.setFuente(this.fuente));
    }

    public String getTitulo() { return this.titulo;}

}
