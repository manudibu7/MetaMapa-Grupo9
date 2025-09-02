package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Coleccion {

    private String titulo;
    private String descripcion;
    private List<Fuente> fuentes;
    private List<InterfaceCondicion> criterioDePertenencia;
    private List<Hecho> hechos;
    private String handle;
    private AlgoritmoDeConsenso algoritmoDeConsenso;

    public Coleccion(String titulo, String descripcion, List<Fuente> fuentes) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criterioDePertenencia = new ArrayList<>();
        this.hechos = new ArrayList<>();
    }
    public Coleccion(String titulo, String descripcion, List <Fuente> fuentes, List<InterfaceCondicion> criterio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criterioDePertenencia = criterio;
        this.hechos = new ArrayList<>();
    }

    public void cambiarCriterioDePertenencia(List<InterfaceCondicion> criterio) {
        criterioDePertenencia = criterio;
    }
    //revisar
<<<<<<< HEAD
    public List<Hecho> obtenerHechos() {
        List<Hecho> hechos = new ArrayList<>();
        this.fuentes.forEach((f) -> hechos.addAll(f.obtenerHechos()));
        return hechos; //Arreglar dps
=======
    public void obtenerHechos() {
        hechos = this.fuente.obtenerHechos().stream()
                .filter(hecho -> criterioDePertenencia.stream()
                        .allMatch(condicion -> condicion.cumpleCondicion(hecho)))
                .collect(Collectors.toList());

    }

    public void setearFuente() {
        this.hechos.forEach((h) -> h.setFuente(this.fuente));
>>>>>>> d283bb744f5ecc0b2053da8f5e100c10abc3e59b
    }
    public List<Hecho> obtenerHechosConsensuados(List<Fuente> fuentes){ //hacer otro map 0 no consensuado 1 consensuado
        List<Hecho> hechosConsensuados=this.algoritmoDeConsenso.aplicar(this.hechos,fuentes);
        return hechosConsensuados;
    }
    public List<Fuente> obtenerFuentes() {
        List<Fuente> fuentes = this.hechos.stream().map(h->h.getFuente()).distinct().collect(Collectors.toList());
        return fuentes;
    }
    public String getTitulo() { return this.titulo;}

}
