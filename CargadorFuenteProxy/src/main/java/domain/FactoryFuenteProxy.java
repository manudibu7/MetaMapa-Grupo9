package domain;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;


public class FactoryFuenteProxy {
    public FuenteDemo createFuenteDemo(URL url)
    {
        Conexion conexion = new Conexion() {
            @Override
            public Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
                return Collections.emptyMap();
            }
        }; //ARREGLAR ESTE TEMA
        return new FuenteDemo(conexion, url);
    }
    public FuenteMetamapa createFuenteMetamapa(URL url){
        WebClient.Builder webClientBuilder = WebClient.builder();
        return new FuenteMetamapa(url,webClientBuilder);
    }
}
