package repository.impl;

import domain.Contribucion;
import domain.InterfaceCondicion;
import repository.IContribucionesRepository;

import java.util.List;

public class ContribucionesRepository implements IContribucionesRepository {
    private List<Contribucion> contribuciones;

    @Override
    public void guardar(Contribucion contribucion) {
        contribuciones.add(contribucion);
        contribucion.setId((long)this.contribuciones.size());
    }

    @Override
    public Contribucion buscarPorId(Long id) {
        return this.contribuciones.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        contribuciones.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public void actualizar(Contribucion contribucion) {
        for (int i = 0; i < contribuciones.size(); i++) {
            if (contribuciones.get(i).getId().equals(contribucion.getId())) {
                contribuciones.set(i, contribucion);
                return;
            }
        }
    }

    @Override
    public List<Contribucion> buscarPorFiltro(List<InterfaceCondicion> filtros){
        return this.contribuciones.stream()
                .filter(c -> filtros.stream().allMatch(f -> f.cumpleCondicion(c.getHecho())))
                .toList();
    }

}
