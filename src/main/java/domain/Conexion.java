package domain;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

public interface Conexion {
    Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta);
}