package repository.impl;

import domain.Contribuyente;
import repository.IContribuyentesRepository;

import java.util.List;

public class ContribuyentesRepository implements IContribuyentesRepository {

    private List<Contribuyente> contribuyentes;

    @Override
    public void guardar(Contribuyente contribuyente){
        contribuyentes.add(contribuyente);
        contribuyente.setId((long)this.contribuyentes.size());
    };

    @Override
    public Contribuyente buscarPorId(Long id){
        return contribuyentes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    };

    @Override
    public void eliminar(Contribuyente contribuyente){
        contribuyentes.remove(contribuyente);
    };

    @Override
    public boolean existe(Long id){
        return contribuyentes.stream().anyMatch(c -> c.getId().equals(id));
    };
}
