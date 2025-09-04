package repository;


import domain.Contribucion;
import domain.InterfaceCondicion;

import java.util.List;

public interface IContribucionesRepository {
    public void guardar(Contribucion contribucion);
    public Contribucion buscarPorId(Long id);
    public void eliminar(Long id);
    public void actualizar(Contribucion contribucion);
    public List<Contribucion> buscarPorFiltro(List<InterfaceCondicion> filtros);
}
