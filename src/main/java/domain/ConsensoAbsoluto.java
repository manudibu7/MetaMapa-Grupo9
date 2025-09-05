package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
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
*/


import java.util.*;

public class ConsensoAbsoluto implements AlgoritmoDeConsenso {

    /**
     * Devuelve los hechos que aparecen en tantas menciones como cantidad de fuentes (consenso absoluto).
     * Supone que la lista `hechos` contiene todas las menciones provenientes de todas las fuentes.
     */
    @Override
    public List<Hecho> aplicar(List<Hecho> hechos, List<Fuente> fuentes) {
        if (hechos == null || hechos.isEmpty() || fuentes == null || fuentes.isEmpty()) {
            return Collections.emptyList();
        }

        // 1) Contar menciones por Hecho
        Map<Hecho, Integer> conteo = new HashMap<>();
        for (Hecho h : hechos) {
            if (h == null) continue;
            conteo.merge(h, 1, Integer::sum);
        }

        // 2) Umbral de consenso absoluto: todas las fuentes
        final int umbral = fuentes.size();

        // 3) Filtrar los que alcanzan el umbral
        List<Hecho> consensuados = new ArrayList<>();
        for (Map.Entry<Hecho, Integer> e : conteo.entrySet()) {
            if (e.getValue() >= umbral) {
                consensuados.add(e.getKey());
            }
        }

        return consensuados;
    }
}

