package ar.utn.ba.ddsi.fuenteproxy.models.entities;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

public interface Conexion {
    Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta);
}