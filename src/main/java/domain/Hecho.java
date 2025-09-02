package domain;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class Hecho {
    private String Titulo;
    private String descripcion;
    private Categoria categoria;
    private LocalDate fecha;
    private LocalDate fechaDeCarga;
    private Fuente fuente;
    private Ubicacion lugarDeOcurrencia;
    private String origen;
    private Etiqueta etiqueta;
    private EnumEstadoHecho estado;
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

    //¿por alguna razon lectorCSV los necesita en STRING?
    public void cambiarUbicacion(String dato, String dato1) {
        float nuevaLatitud = Float.parseFloat(dato);
        float nuevaLongitud = Float.parseFloat(dato1);
        lugarDeOcurrencia.setUbicacion(nuevaLatitud,nuevaLongitud);
    }

    //string a fecha??
    public LocalDate getFecha() {return fecha;}
    public void cambiarFecha(String dato) {
        //TODO
    }

    public void setFuente(Fuente fuente) {
        this.fuente = fuente;
    }
    public Fuente getFuente() {return fuente;}

    public String getOrigen() {return origen;}

    public Boolean esVisible() {return visibilidad;}
    public void cambiarVisibilidad() {visibilidad = true;} //asi por el momento

    public void cambiarEstado(EnumEstadoHecho estado) {this.estado = estado;}
}
