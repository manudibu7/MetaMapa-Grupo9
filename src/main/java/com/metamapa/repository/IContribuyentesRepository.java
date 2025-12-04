package com.metamapa.repository;

import com.metamapa.domain.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IContribuyentesRepository extends JpaRepository<Contribuyente, Long>{

    /**
     * Busca un contribuyente por su ID de Keycloak.
     * @param keycloakId ID externo proveniente de Keycloak
     * @return Optional con el contribuyente si existe, vacío si no
     */
    Optional<Contribuyente> findByKeycloakId(String keycloakId);

    /*
    public void guardar(Contribuyente contribuyente);
    public Contribuyente buscarPorId(Long id);
    public void eliminar(Contribuyente contribuyente);
    public boolean existe(Long id);
     */
}
