package ar.utn.ba.ddsi.fuenteproxy.models.Repository;


import ar.utn.ba.ddsi.fuenteproxy.models.entities.Fuente;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Hecho;

import java.util.List;

public interface IHechoRepository {
    public void save(Hecho hecho);
    public List<Hecho> findAll();
    public Hecho findById(Long id);
    public void delete(Hecho hecho);
    public List<Fuente> buscarNuevasRutas(Long ultimoID);
}
