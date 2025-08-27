package domain;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CargadorHechos extends FuenteDemo{
    private Conexion conexion;
    private URL url;
    private LocalDateTime fechaUltimaConsulta;

    public CargadorHechos(Conexion conexion, URL url) {
        this.conexion = conexion;
        this.url = url;
    }

    public Hecho cargarHecho(){
        Map<String, Object> proximoHecho = conexion.siguienteHecho(this.url, this.fechaUltimaConsulta);
        return transformar(proximoHecho);
    }

    public Hecho transformar(Map<String, Object> map) {
        String titulo = (String) map.get("titulo");
        String descripcion = (String) map.get("descripcion");

        String categoriaStr = (String) map.get("categoria");
        Categoria categoria = new Categoria(categoriaStr);

        String latitudStr = (String) map.get("latitud");
        String longitudStr = (String) map.get("longitud");
        float latitud = Float.parseFloat(latitudStr);
        float longitud = Float.parseFloat(longitudStr);
        Ubicacion ubicacion = new Ubicacion(latitud, longitud);

        String fechaStr = (String) map.get("fecha");
        LocalDate fecha = LocalDate.parse(fechaStr);

        return new Hecho(titulo, descripcion, categoria, ubicacion, fecha);
    }

}
