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

public class FuenteMetamapa extends FuenteProxy {

    private String apiUrl;
    private ObjectMapper mapper;

    public FuenteMetamapa(String apiUrl) {
        super(null); // No se usa una fuente interna
        this.apiUrl = apiUrl;
        this.mapper = new ObjectMapper();
    }

    // Obtener todos los hechos
    @Override
    public List<Hecho> obtenerHechos() {
        return obtenerHechosDesde(apiUrl + "/hechos");
    }

    // Obtener hechos por colección
    public List<Hecho> obtenerHechosDeColeccion(String identificador) {
        return obtenerHechosDesde(apiUrl + "/colecciones/" + identificador + "/hechos");
    }

    // Realizar solicitud de eliminación
    public boolean enviarSolicitudEliminacion(String textoSolicitud) {
        try {
            URL url = new URL(apiUrl + "/solicitudes");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonBody = String.format("{"motivo": "%s"}", textoSolicitud);

            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(jsonBody);
                out.flush();
            }

            int responseCode = con.getResponseCode();
            return responseCode == 200 || responseCode == 201;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método común para obtener hechos desde un endpoint
    private List<Hecho> obtenerHechosDesde(String endpoint) {
        List<Hecho> hechos = new ArrayList<>();
        try {
            URL url = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            hechos = mapper.readValue(response.toString(), new TypeReference<List<Hecho>>() {});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hechos;
    }
}