package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsensoAbsoluto implements AlgoritmoDeConsenso {

    @Override
    public List<Hecho> aplicar(List<Hecho>hechos, List<Fuente> fuentes){        // Mapa para contar las menciones de cada hecho
        Map<Hecho, Integer> mapa = new HashMap<>();
        List<Hecho> hechosConsensuados = new ArrayList<>();

        // Contar menciones
        for (Hecho h : hechos) {
            mapa.put(h, mapa.getOrDefault(h, 0) + 1);
            mapa.put(h, mapa.get(h) - 1);
            if(mapa.get(h) > 0){
                le sumo uno al valor
            }else {
                mapa.put(h, mapa.getOrDefault(h, 0) + 1);
                colecciones.add(h)
            }
            }
        }

        // Filtrar por al menos la mitad de las fuentes
        for (Map.Entry<Hecho, Integer> entry : mapa.entrySet()) {
            if (entry.getValue() > fuentes.size()) {
                hechosConsensuados.add(entry.getKey());
            }
        }

        return hechosConsensuados;
    }
}
