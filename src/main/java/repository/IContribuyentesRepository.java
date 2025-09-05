package repository;

import domain.Contribuyente;
import org.springframework.stereotype.Repository;

public interface IContribuyentesRepository {
    public void guardar(Contribuyente contribuyente);
    public Contribuyente buscarPorId(Long id);
    public void eliminar(Contribuyente contribuyente);
    public boolean existe(Long id);
}
