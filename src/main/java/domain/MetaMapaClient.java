package domain;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class MetaMapaClient {


    private final WebClient webClient;

    public MetaMapaClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<List<Hecho>> obtenerHechosDesdeUrl(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Hecho.class)
                .collectList();
    }

    public Mono<String> enviarSolicitudEliminacion(SolicitudEliminacion solicitud) {
        return webClient.post()
                .uri("/solicitudes")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(String.class);
    }

}
