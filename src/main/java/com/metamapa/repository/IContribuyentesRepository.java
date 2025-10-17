package com.metamapa.repository;

import com.metamapa.domain.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IContribuyentesRepository extends JpaRepository<Contribuyente, Long>{
    /*
    public void guardar(Contribuyente contribuyente);
    public Contribuyente buscarPorId(Long id);
    public void eliminar(Contribuyente contribuyente);
    public boolean existe(Long id);
     */
}
