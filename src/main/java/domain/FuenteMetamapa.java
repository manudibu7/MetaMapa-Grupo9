package domain;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FuenteMetamapa extends FuenteProxy {

    private String url= "http://localhost:8081"; // hardcode solo para test;
    private final WebClient webClient;

    public FuenteMetamapa( //String url,
                           WebClient.Builder builder) {
        super(null);
        this.webClient = builder.build();
       // this.url = url;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        return obtenerHechosDesdeUrl(this.url + "/hechos");
    }

    public List<Hecho> obtenerHechosDesdeUrl(String urlCompleta) {
        return webClient.get()
                .uri(urlCompleta)
                .retrieve()
                .bodyToFlux(Hecho.class)
                .collectList().block();
    }

    // Obtener hechos por colección
    public List<Hecho> obtenerHechosDeColeccion(String identificador) {
        return obtenerHechosDesdeUrl(url + "/colecciones/" + identificador + "/hechos");
    }

    public Mono<String> enviarSolicitudEliminacion(SolicitudEliminacion solicitud) {
        return webClient.post()
                .uri("/solicitudes")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(String.class);
    }

}