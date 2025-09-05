package ar.utn.ba.ddsi.fuenteproxy.models.Repository.imp;

import ar.utn.ba.ddsi.fuenteproxy.models.Repository.IHechoRepository;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Fuente;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.EnumTipoFuenteProxy;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Hecho;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HechoRepository implements IHechoRepository {
    private List<Hecho> hechos = new ArrayList<>();
    private Integer numero = 0 ;

    @Override
    public void save(Hecho hecho) {
        this.hechos.add(hecho);
    }

    @Override
    public List<Hecho> findAll() {
        return this.hechos;
    }

    @Override
    public Hecho findById(Long id) {
        return null;
    }
    @Override
    public void delete(Hecho hecho) {}

    @Override
    public List<Fuente> buscarNuevasRutas(Long ultimoID){
        Long id = ultimoID; //cualquiera esto
        URL url = null;
        EnumTipoFuenteProxy tipoFuenteProxy = EnumTipoFuenteProxy.METAMAPA;
        List<Fuente> rutas = null;
        rutas.add(new Fuente(id,url,tipoFuenteProxy));
        return rutas;
    }
}
