package com.metamapa.repository;


import com.metamapa.domain.Contribucion;
import com.metamapa.domain.InterfaceCondicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IContribucionesRepository extends JpaRepository<Contribucion, Long>  {


    List<Contribucion> findByContribuyenteId(Long contribuyenteId);

    List<Contribucion> findByContribuyenteKeycloakId(String keycloakId);

    /*
    public void guardar(Contribucion contribucion);
    public Contribucion buscarPorId(Long id);
    public void eliminar(Long id);
    public void actualizar(Contribucion contribucion);
    public List<Contribucion> buscarPorFiltro(List<InterfaceCondicion> filtros);
    public List<Contribucion> buscarTodas();
     */
}
