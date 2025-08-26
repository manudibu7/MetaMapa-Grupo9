package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FiltradorDeHechos {
    

    public List<Hecho> devolverHechosAPartirDe(List<InterfaceCondicion> condiciones, List<Hecho> hechos) {
        if (hechos == null || hechos.isEmpty()) {
            return Collections.emptyList();
        }
         //hechos no nulos
        List<Hecho> base = hechos.stream().filter(Objects::nonNull).collect(Collectors.toList()); //filtra los nulls

        if (condiciones == null || condiciones.isEmpty()) {
            return new ArrayList<>(base); 
        }

        return base.stream().filter(hecho -> condiciones.stream().filter(Objects::nonNull).allMatch(cond -> cond.cumpleCondicion(hecho))).collect(Collectors.toList());
    }
    
}
