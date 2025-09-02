package domain;

import java.util.*;

public class MencionesMultiples implements AlgoritmoDeConsenso {
    @Override
    public List<Hecho> aplicar(List<Hecho>hechos,List<Fuente> fuentes){        // Mapa para contar las menciones de cada hecho
        Map<Hecho, Integer> mapa = new HashMap<>();
        List<Hecho> hechosConsensuados = new ArrayList<>();

        // Contar menciones
        for (Hecho h : hechos) {
            mapa.put(h, mapa.getOrDefault(h, 0) + 1);
        }

        // Filtrar por al menos 2 fuentes
        for (Map.Entry<Hecho, Integer> entry : mapa.entrySet()) {
            if (entry.getValue() > 2) {
                hechosConsensuados.add(entry.getKey());
            }
        }

        return hechosConsensuados;
    }

}
