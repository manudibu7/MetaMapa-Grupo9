package com.metamapa.services;

import com.metamapa.domain.Contribuyente;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metamapa.repository.IContribuyentesRepository;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class ServicioContribuyente {
    @Autowired
    private IContribuyentesRepository repositorio;

    /**
     * Obtiene un contribuyente existente por su keycloakId, o crea uno nuevo si no existe.
     *
     * @param keycloakId ID externo proveniente de Keycloak (requerido)
     * @param nombre Nombre del contribuyente (puede ser null o vacío)
     * @param apellido Apellido del contribuyente (puede ser null o vacío)
     * @return El contribuyente existente o el recién creado
     * @throws DatosInvalidosException si keycloakId es null o vacío
     */
    public Contribuyente getOrCreateByKeycloakId(String keycloakId, String nombre, String apellido) {
        // Validar que keycloakId no sea null ni vacío
        log.info("Intentando obtener o crear contribuyente con keycloakId: {}", keycloakId);
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("KeycloakId inválido: {}", keycloakId);
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        // Buscar contribuyente existente por keycloakId
        Optional<Contribuyente> existente = repositorio.findByKeycloakId(keycloakId);


        if (existente.isPresent()) {
            log.info("Contribuyente encontrado para keycloakId {}: ID sistema {}", keycloakId, existente.get().getId());
            return existente.get();
        }

        log.debug("No se encontró contribuyente para keycloakId {}. Creando nuevo contribuyente.", keycloakId);
        // Crear nuevo contribuyente
        Contribuyente nuevo = new Contribuyente();
        nuevo.setKeycloakId(keycloakId);
        nuevo.setNombre(nombre != null ? nombre : "");
        nuevo.setApellido(apellido != null ? apellido : "");

        return repositorio.save(nuevo);
    }

    public long registrarContribuyente(ContribuyenteInputDTO contribuyenteInputDTO){
        // Validaciones
        log.info("Registrando nuevo contribuyente ");
        if (contribuyenteInputDTO == null) {
            log.warn("Datos de contribuyente nulos");
            throw new DatosInvalidosException("Los datos del contribuyente no pueden ser nulos");
        }
        
        // Validar edad si se proporciona
        if (contribuyenteInputDTO.getEdad() != null) {
            log.warn("Validando edad del contribuyente: ");
            if (contribuyenteInputDTO.getEdad() < 0 || contribuyenteInputDTO.getEdad() > 150) {
                log.warn("Edad inválida: {}", contribuyenteInputDTO.getEdad());
                throw new DatosInvalidosException("La edad debe estar entre 0 y 150 años");
            }
        }
        
        // Validar que si proporciona nombre o apellido, ambos deben estar presentes
        boolean tieneNombre = contribuyenteInputDTO.getNombre() != null && !contribuyenteInputDTO.getNombre().trim().isEmpty();
        boolean tieneApellido = contribuyenteInputDTO.getApellido() != null && !contribuyenteInputDTO.getApellido().trim().isEmpty();
        
        if (tieneNombre && !tieneApellido) {
                log.warn("Nombre proporcionado sin apellido: nombre='{}'", contribuyenteInputDTO.getNombre());
            throw new DatosInvalidosException("Si proporciona nombre, debe proporcionar también el apellido");
        }
        if (!tieneNombre && tieneApellido) {
            log.warn("Apellido proporcionado sin nombre: apellido='{}'", contribuyenteInputDTO.getApellido());
            throw new DatosInvalidosException("Si proporciona apellido, debe proporcionar también el nombre");
        }
        
        Contribuyente nuevo = new Contribuyente(contribuyenteInputDTO.getNombre(),
                                                contribuyenteInputDTO.getApellido(),
                                                contribuyenteInputDTO.getEdad());
        // establecer anonimo: true si el DTO pide anonimo o si no hay nombre ni apellido (nulos o vacios)
        //boolean forzarAnonimoPorCamposVacíos = !tieneNombre && !tieneApellido;
        //boolean anonimoSolicitado = false;
        //try {
        //    // lombok genera isAnonimo() para boolean primitives
        //    anonimoSolicitado = contribuyenteInputDTO.isAnonimo();
        //} catch (Exception e) {
        //    // por seguridad, si no existe el metodo isAnonimo(), ignorar y usar default false
        //}
        //nuevo.setAnonimo(anonimoSolicitado || forzarAnonimoPorCamposVacíos);

        repositorio.save(nuevo);
        log.info("Contribuyente registrado con ID: {}", nuevo.getId());
        return nuevo.getId();
    }

    public Contribuyente buscarContribuyente(Long id){
        log.info("Buscando contribuyente por ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("ID de contribuyente inválido: {}", id);
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }
        
        return repositorio.findById(id)
                .orElseThrow(() -> {
                        log.warn("Contribuyente no encontrado con ID: {}", id);
                    return new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + id);
                });
    }

    /**
     * Busca un contribuyente por su keycloakId.
     * @param keycloakId ID externo de Keycloak
     * @return El contribuyente encontrado
     * @throws DatosInvalidosException si keycloakId es null o vacío
     * @throws RecursoNoEncontradoException si no se encuentra el contribuyente
     */
    public Contribuyente buscarContribuyentePorKeycloakId(String keycloakId) {
        log.info("Buscando contribuyente por keycloakId: {}", keycloakId);
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("KeycloakId inválido: {}", keycloakId);
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        return repositorio.findByKeycloakId(keycloakId)
                .orElseThrow(()->{
                    log.warn("Contribuyente no encontrado con keycloakId: {}", keycloakId);
                 return new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakId);
                });

    }

    public java.util.List<Contribuyente> listarContribuyentes(){
        log.info("Listando todos los contribuyentes");
        List<Contribuyente> contribuyentes = repositorio.findAll();
        log.info("Listando contribuyentes. Total encontrados: {}", contribuyentes.size());
        return contribuyentes;
    }

    public void actualizarContribuyente(Contribuyente contribuyente){
        log.info("Actualizando contribuyente con ID: {}", contribuyente != null ? contribuyente.getId() : null);
        if (contribuyente == null || contribuyente.getId() == null) {
            log.warn("Contribuyente o ID nulos");
            throw new DatosInvalidosException("El contribuyente y su ID no pueden ser nulos");
        }
        
        if (!repositorio.existsById(contribuyente.getId())) {
            log.warn("Contribuyente no encontrado con ID: {}", contribuyente.getId());
            throw new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + contribuyente.getId());
        }
        
        repositorio.save(contribuyente);
    }

    public void eliminarContribuyente(Long id){
        log.info("Eliminando contribuyente con ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("ID de contribuyente inválido para eliminación: {}", id);
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }
        
        if (!repositorio.existsById(id)) {
            log.warn("Contribuyente no encontrado para eliminación con ID: {}", id);
            throw new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + id);
        }
        
        repositorio.deleteById(id);
        log.info("Contribuyente eliminado con ID: {}", id);
    }
    public Contribuyente actualizarConKeycloak(
            String keycloakId,
            String nombre,
            String apellido,
            Integer edad
    ) {

        log.info("Actualizando contribuyente con keycloakId: {}", keycloakId);
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("KeycloakId inválido para actualización: {}", keycloakId);
            throw new DatosInvalidosException("keycloakId inválido");
        }

        if (edad != null && (edad < 0 || edad > 150)) {
            log.warn("Edad inválida para actualización: {}", edad);
            throw new DatosInvalidosException("La edad debe estar entre 0 y 150 años");
        }

        boolean tieneNombre = nombre != null && !nombre.trim().isEmpty();
        boolean tieneApellido = apellido != null && !apellido.trim().isEmpty();

        if (tieneNombre ^ tieneApellido) {
            log.warn("Datos de nombre/apellido incompletos para actualización: nombre='{}', apellido='{}'", nombre, apellido);
            throw new DatosInvalidosException(
                    "Nombre y apellido deben proporcionarse juntos"
            );
        }

        Contribuyente contribuyente = repositorio.findByKeycloakId(keycloakId)
                .orElseThrow(() -> {
                            log.warn("Contribuyente no encontrado para actualización con keycloakId: {}", keycloakId);
                            return new RecursoNoEncontradoException(
                                    "Contribuyente no encontrado para el usuario autenticado");
                        }
                );

        contribuyente.setNombre(nombre);
        contribuyente.setApellido(apellido);
        contribuyente.setEdad(edad);

        return repositorio.save(contribuyente);
    }
}
