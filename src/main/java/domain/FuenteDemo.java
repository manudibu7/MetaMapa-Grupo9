package domain;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;

public class FuenteDemo extends Fuente {
    private Conexion conexion;
    private URL url;
    private LocalDateTime ultimaConsulta = LocalDateTime.now().minusHours(1);
    private final List<Hecho> hechosCargados = new ArrayList<>();

    @Override
    public List<Hecho> obtenerHechos() {
        return this.hechosCargados;
    }
//    @Override
//    public List<Hecho> obtenerHechos() {
//        Map<String, Object> datos = conexion.siguienteHecho(url, ultimaConsulta);
//        if (datos != null) {
//            Hecho hecho = construirHechoDesdeMapa(datos);
//            hechos.add(hecho);
//        }
//        ultimaConsulta = LocalDateTime.now();
//        return hechos;
//    }

    public void agregarHecho(Hecho h) {
        hechosCargados.add(h);
    }
    private Hecho construirHechoDesdeMapa(Map<String, Object> datos) {
        String titulo = (String) datos.get("titulo");
        String descripcion = (String) datos.get("descripcion");

        String categoriaStr = (String) datos.get("categoria");
        Categoria categoria = new Categoria(categoriaStr);

        String latitudStr = (String) datos.get("latitud");
        String longitudStr = (String) datos.get("longitud");
        float latitud = Float.parseFloat(latitudStr);
        float longitud = Float.parseFloat(longitudStr);
        Ubicacion ubicacion = new Ubicacion(latitud, longitud);

        String fechaStr = (String) datos.get("fecha");
        LocalDate fecha = LocalDate.parse(fechaStr);

        return new Hecho(titulo, descripcion, categoria, ubicacion, fecha);
    }
}