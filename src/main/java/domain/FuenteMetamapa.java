package domain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FuenteMetamapa extends FuenteProxy {

    private MetaMapaClient client;
    private String url;

    public FuenteMetamapa(MetaMapaClient client, String url) {
        super(null);
        this.client = client;
        this.url = url;
    }
    @Override
    public List<Hecho> obtenerHechos() {
        return client.obtenerHechosDesdeUrl(url + "/hechos").block();
    }

    // Obtener hechos por colección
    public List<Hecho> obtenerHechosDeColeccion(String identificador) {
        return client.obtenerHechosDesdeUrl(url + "/colecciones/" + identificador + "/hechos").block();
    }

    public Mono<String> enviarSolicitudEliminacion(SolicitudEliminacion solicitud) {
        return client.enviarSolicitudEliminacion(solicitud);
    }

//    // Realizar solicitud de eliminación
//    public boolean enviarSolicitudEliminacion(String textoSolicitud) {
//        try {
//            URL url = new URL(apiUrl + "/solicitudes");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Content-Type", "application/json");
//            con.setDoOutput(true);
//
//            String jsonBody = String.format("{motivo : %s}", textoSolicitud);
//
//            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
//                out.writeBytes(jsonBody);
//                out.flush();
//            }
//
//            int responseCode = con.getResponseCode();
//            return responseCode == 200 || responseCode == 201;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


}