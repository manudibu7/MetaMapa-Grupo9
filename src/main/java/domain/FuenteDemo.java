package domain;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class FuenteDemo extends Fuente {
    private Conexion conexion;
    private URL url;
    private LocalDateTime ultimaConsulta = LocalDateTime.now().minusHours(1);
    private List<Hecho> hechos = new ArrayList<>();

    public FuenteDemo(Conexion conexion, URL url) {
        this.conexion = conexion;
        this.url = url;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        Map<String, Object> datos = conexion.siguienteHecho(url, ultimaConsulta);
        if (datos != null) {
            Hecho hecho = construirHechoDesdeMapa(datos);
            hechos.add(hecho);
        }
        ultimaConsulta = LocalDateTime.now();
        return hechos;
    }

    private Hecho construirHechoDesdeMapa(Map<String, Object> datos) {
        String titulo = (String) datos.get("titulo");
        String descripcion = (String) datos.get("descripcion");
        String categoria = (String) datos.get("categoria");
        String latitud = (String) datos.get("latitud");
        String longitud = (String) datos.get("longitud");
        String fecha = (String) datos.get("fecha");

        return new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha);
    }
}