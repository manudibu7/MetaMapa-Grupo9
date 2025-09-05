package ar.utn.ba.ddsi.fuenteproxy.models.entities;

import java.net.URL;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FuenteMetamapa extends FuenteProxy {

    @NotNull private URL url;
    private final WebClient webClient;

    public FuenteMetamapa(URL url, @org.jetbrains.annotations.NotNull WebClient.@NotNull Builder builder) {
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