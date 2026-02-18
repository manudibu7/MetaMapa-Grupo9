package com.metamapa.services;

import com.metamapa.domain.Contribuyente;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metamapa.repository.IContribuyentesRepository;

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
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        // Buscar contribuyente existente por keycloakId
        Optional<Contribuyente> existente = repositorio.findByKeycloakId(keycloakId);

        if (existente.isPresent()) {
            return existente.get();
        }

        // Crear nuevo contribuyente
        Contribuyente nuevo = new Contribuyente();
        nuevo.setKeycloakId(keycloakId);
        nuevo.setNombre(nombre != null ? nombre : "");
        nuevo.setApellido(apellido != null ? apellido : "");

        return repositorio.save(nuevo);
    }

    public long registrarContribuyente(ContribuyenteInputDTO contribuyenteInputDTO){
        // Validaciones
        if (contribuyenteInputDTO == null) {
            throw new DatosInvalidosException("Los datos del contribuyente no pueden ser nulos");
        }
        
        // Validar edad si se proporciona
        if (contribuyenteInputDTO.getEdad() != null) {
            if (contribuyenteInputDTO.getEdad() < 0 || contribuyenteInputDTO.getEdad() > 150) {
                throw new DatosInvalidosException("La edad debe estar entre 0 y 150 años");
            }
        }
        
        // Validar que si proporciona nombre o apellido, ambos deben estar presentes
        boolean tieneNombre = contribuyenteInputDTO.getNombre() != null && !contribuyenteInputDTO.getNombre().trim().isEmpty();
        boolean tieneApellido = contribuyenteInputDTO.getApellido() != null && !contribuyenteInputDTO.getApellido().trim().isEmpty();
        
        if (tieneNombre && !tieneApellido) {
            throw new DatosInvalidosException("Si proporciona nombre, debe proporcionar también el apellido");
        }
        if (!tieneNombre && tieneApellido) {
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
        return nuevo.getId();
    }

    public Contribuyente buscarContribuyente(Long id){
       log.debug("Buscando contribuyente con ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("Intento de búsqueda de contribuyente con ID inválido: {}", id);
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }
        
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + id));
    }

    /**
     * Busca un contribuyente por su keycloakId.
     * @param keycloakId ID externo de Keycloak
     * @return El contribuyente encontrado
     * @throws DatosInvalidosException si keycloakId es null o vacío
     * @throws RecursoNoEncontradoException si no se encuentra el contribuyente
     */
    public Contribuyente buscarContribuyentePorKeycloakId(String keycloakId) {
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("Intento de búsqueda de contribuyente con keycloakId inválido: {}", keycloakId);
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        return repositorio.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakId));
    }

    public java.util.List<Contribuyente> listarContribuyentes(){
       log.info("Listando todos los contribuyentes");
        return repositorio.findAll();
    }

    public void actualizarContribuyente(Contribuyente contribuyente){

        log.info("Intentando actualizar contribuyente con ID: {}", contribuyente != null ? contribuyente.getId() : "null");
        if (contribuyente == null || contribuyente.getId() == null) {
            log.warn("Intento de actualización de contribuyente con datos inválidos: contribuyente o ID nulo");
            throw new DatosInvalidosException("El contribuyente y su ID no pueden ser nulos");
        }
        
        if (!repositorio.existsById(contribuyente.getId())) {
            log.warn("Intento de actualización de contribuyente no existente con ID: {}", contribuyente.getId());
            throw new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + contribuyente.getId());
        }
        
        repositorio.save(contribuyente);
    }

    public void eliminarContribuyente(Long id){
        if (id == null || id <= 0) {
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }
        
        if (!repositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + id);
        }
        
        repositorio.deleteById(id);
    }
    public Contribuyente actualizarConKeycloak(
            String keycloakId,
            String nombre,
            String apellido,
            Integer edad
    ) {
        log.info("Intentando actualizar contribuyente con keycloakId: {}", keycloakId);
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("Intento de actualización de contribuyente con keycloakId inválido: {}", keycloakId);
            throw new DatosInvalidosException("keycloakId inválido");
        }

        if (edad != null && (edad < 0 || edad > 150)) {

            log.warn("Intento de actualización de contribuyente con edad inválida: {}", edad);
            throw new DatosInvalidosException("La edad debe estar entre 0 y 150 años");
        }

        boolean tieneNombre = nombre != null && !nombre.trim().isEmpty();
        boolean tieneApellido = apellido != null && !apellido.trim().isEmpty();

        if (tieneNombre ^ tieneApellido) {
            log.warn("Intento de actualización de contribuyente con nombre o apellido incompleto: nombre='{}', apellido='{}'", nombre, apellido);
            throw new DatosInvalidosException(
                    "Nombre y apellido deben proporcionarse juntos"
            );
        }

        Contribuyente contribuyente = repositorio.findByKeycloakId(keycloakId)
                .orElseThrow(() ->{
                        log.warn("Contribuyente no encontrado para keycloakId: {}", keycloakId);
                        return new RecursoNoEncontradoException(
                                    "Contribuyente no encontrado para el usuario autenticado"
                            );
                }
                );

        contribuyente.setNombre(nombre);
        contribuyente.setApellido(apellido);
        contribuyente.setEdad(edad);
        log.debug("Actualizando contribuyente: keycloakId={}, nombre={}, apellido={}, edad={}",
            keycloakId, nombre, apellido, edad
        );
        return repositorio.save(contribuyente);
    }
}
