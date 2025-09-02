package domain;

import java.util.List;

public class ConsensoDefault implements AlgoritmoDeConsenso {
    @Override
    public List<Hecho> aplicar(List<Hecho> hechos,List<Fuente> fuentes){
        return hechos;
    }

}
