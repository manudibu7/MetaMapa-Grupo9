package ar.utn.ba.ddsi.fuenteproxy.models.entities;

import java.time.LocalDate;


public class Hecho {
    private String Titulo;
    private String descripcion;
    private Categoria categoria;
    private LocalDate fecha;
    private LocalDate fechaDeCarga;
    private Ubicacion lugarDeOcurrencia;
    private String origen;
    private Etiqueta etiqueta;
    private boolean visibilidad = true;

    public Hecho(String titulo, String descripcion, Categoria categoria,Ubicacion lugarDeOcurrencia, LocalDate fecha) {
        this.Titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.lugarDeOcurrencia = lugarDeOcurrencia;
        this.fecha = fecha;

    }
    //getters y setters
    public String getTitulo() {return Titulo;}
    public void cambiarTitulo(String titulo) {this.Titulo= titulo;}
    public void cambiarDescripcion(String descripcion) {this.descripcion = descripcion;}
    public Categoria getCategoria() {return categoria;}
    public void cambiarCategoria(String categoria) {this.categoria.setNombre(categoria);}
    public Etiqueta getEtiqueta() {return etiqueta;}
    public void cambiarEtiqueta(String etiqueta) {this.etiqueta.setNombre(etiqueta);}

    //Así me vendría la info, en String?
    public void cambiarUbicacion(String dato, String dato1) {
        float nuevaLatitud = Float.parseFloat(dato);
        float nuevaLongitud = Float.parseFloat(dato1);
        lugarDeOcurrencia.setUbicacion(nuevaLatitud,nuevaLongitud);
    }

    //string a fecha??
    public LocalDate getFecha() {return fecha;}

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {return origen;}

    public Boolean esVisible() {return visibilidad;}
    public void cambiarVisibilidad() {visibilidad = true;} //asi por el momento

}
