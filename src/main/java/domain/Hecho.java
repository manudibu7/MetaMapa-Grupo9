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
    private boolean visibilidad = true;

    public Hecho(String titulo, String descripcion, Categoria categoria,Ubicacion lugarDeOcurrencia, LocalDate fecha) {
        this.Titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.lugarDeOcurrencia = lugarDeOcurrencia;
        this.fecha = fecha;



    }
    public String getTitulo() {
        return Titulo;
    }
    public Categoria getCategoria() {return categoria;}
    public Etiqueta getEtiqueta() {return etiqueta;}
    // Generar los getters y setters (usar lombok)
    public void cambiarDescripcion(String dato) {
        descripcion = dato;
    }

    public void cambiarCategoria(String dato) {
        categoria.setNombre(dato);
    }
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

    public String getOrigen() {return origen;}

    public Boolean esVisible() {return visibilidad;}
    public void cambiarVisibilidad() {visibilidad = true;} //asi por el momento

    public void cambiarEtiqueta(String dato) {
        etiqueta.setNombre(dato);
    }


}
