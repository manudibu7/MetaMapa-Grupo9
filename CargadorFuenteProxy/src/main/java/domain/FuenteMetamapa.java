package domain;

import java.net.URL;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FuenteMetamapa extends FuenteProxy {

    private URL url;
    private final WebClient webClient;

    public FuenteMetamapa(URL url, WebClient.Builder builder) {
        this.webClient = builder.build();
        this.url = url;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        return webClient.get()
                .uri(this.url + "/hechos")
                .retrieve()
                .bodyToFlux(Hecho.class)
                .collectList().block();
    }
}