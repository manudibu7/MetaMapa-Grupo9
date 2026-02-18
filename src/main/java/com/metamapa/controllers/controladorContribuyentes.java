package com.metamapa.controllers;

import ch.qos.logback.classic.Logger;
import com.metamapa.domain.Contribuyente;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.dtos.input.ContribuyenteKeycloakRequest;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import com.metamapa.dtos.output.ContribuyenteSistemaResponse;
import com.metamapa.exceptions.DatosInvalidosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioContribuyente;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping ("/contribuyentes")
public class controladorContribuyentes {
    @Autowired
    private ServicioContribuyente servicioContribuyente;

    /**
     * Endpoint para sincronizar un contribuyente con Keycloak.
     * Si el contribuyente ya existe (por keycloakId), lo devuelve.
     * Si no existe, lo crea con los datos proporcionados.
     *
     * @param request DTO con keycloakId (obligatorio), nombre y apellido (opcionales)
     * @return 200 OK con el contribuyente sincronizado (idSistema, keycloakId, nombre, apellido)
     * @throws DatosInvalidosException si keycloakId es null o vacío (retorna 400)
     */
    @PostMapping("/sync-keycloak")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ContribuyenteSistemaResponse> syncKeycloak(@RequestBody ContribuyenteKeycloakRequest request) {
        // Validación adicional en el controller
        log.info("Recibiendo solicitud de sincronización con Keycloak para keycloakId: {}", request != null ? request.getKeycloakId() : "null");
        if (request == null || request.getKeycloakId() == null || request.getKeycloakId().trim().isEmpty()) {
            log.warn("Solicitud de sincronización con Keycloak inválida: keycloakId es nulo o vacío");
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        var contribuyente = servicioContribuyente.getOrCreateByKeycloakId(
            request.getKeycloakId(),
            request.getNombre(),
            request.getApellido()
        );

        ContribuyenteSistemaResponse response = new ContribuyenteSistemaResponse();
        response.setIdSistema(contribuyente.getId());
        response.setKeycloakId(contribuyente.getKeycloakId());
        response.setNombre(contribuyente.getNombre());
        response.setApellido(contribuyente.getApellido());
        log.debug("Contribuyente sincronizado: idSistema={}, keycloakId={}, nombre={}, apellido={}",
            response.getIdSistema(), response.getKeycloakId(), response.getNombre(), response.getApellido()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<ContribuyenteOutputDTO> agregarContribuyente(@RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        log.info("Recibiendo solicitud para agregar contribuyente");
        long id = servicioContribuyente.registrarContribuyente(contribuyenteInputDTO);
        var c = servicioContribuyente.buscarContribuyente(id);
        log.debug("Contribuyente creado con ID: {}", id);
        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        log.debug("Contribuyente registrado: id={}, nombre={}, apellido={}, edad={}",
            output.getId(), output.getNombre(), output.getApellido(), output.getEdad()
        );
        return ResponseEntity.status(201).body(output);
    }

    @GetMapping
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<List<ContribuyenteOutputDTO>> listarContribuyentes(){

        var contribuyentes = servicioContribuyente.listarContribuyentes();

        List<ContribuyenteOutputDTO> output = contribuyentes.stream()
                .map(c -> {
                    ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    dto.setApellido(c.getApellido());
                    dto.setEdad(c.getEdad());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(200).body(output);
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<ContribuyenteOutputDTO> obtenerContribuyente(@PathVariable long id){
        ContribuyenteOutputDTO contribuyenteOutputDTO = new ContribuyenteOutputDTO();
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).body(null);
        contribuyenteOutputDTO.setId(c.getId());
        contribuyenteOutputDTO.setNombre(c.getNombre());
        contribuyenteOutputDTO.setApellido(c.getApellido());
        contribuyenteOutputDTO.setEdad(c.getEdad());
        log.debug("Contribuyente obtenido: id={}", contribuyenteOutputDTO.getId());

        return ResponseEntity.status(200).body(contribuyenteOutputDTO);
    }

    @PutMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<ContribuyenteOutputDTO> actualizarContribuyente(@PathVariable long id, @RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        log.info("Recibiendo solicitud para actualizar contribuyente con ID: {}", id);
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null){
            log.warn("Intento de actualización de contribuyente no encontrado con ID: {}", id);
            return ResponseEntity.status(404).body(null);
        }

        c.setNombre(contribuyenteInputDTO.getNombre());
        c.setApellido(contribuyenteInputDTO.getApellido());
        c.setEdad(contribuyenteInputDTO.getEdad());
        servicioContribuyente.actualizarContribuyente(c);
        log.info("Contribuyente actualizado con ID: {}", id);
        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        log.info("Contribuyente actualizado: id={}", output.getId());

        return ResponseEntity.status(200).body(output);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<Void> eliminarContribuyente(@PathVariable long id){
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).build();

        servicioContribuyente.eliminarContribuyente(id);
        return ResponseEntity.status(204).build();
    }
    // son distintos a los que estan arriba porque estos funcionan con keycloakID y no con Id comun
    @GetMapping("/me/{keycloakId}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ContribuyenteOutputDTO> obtenerConKeycloak(
            @PathVariable String keycloakId
    ) {

        log.debug("Recibiendo solicitud para obtener contribuyente con keycloakId: {}", keycloakId);
        Contribuyente c = servicioContribuyente.buscarContribuyentePorKeycloakId(keycloakId);

        if (c == null) {
            log.info("Contribuyente no encontrado para keycloakId: {}", keycloakId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setEdad(c.getEdad());
        log.info("Contribuyente encontrado para keycloakId: {}, idSistema: {}", keycloakId, dto.getId());
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me/{keycloakId}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ContribuyenteOutputDTO> actualizarConKeycloak(
            @PathVariable String keycloakId,
            @RequestBody ContribuyenteInputDTO dto
    ) {
        log.info("Recibiendo solicitud para actualizar contribuyente con keycloakId: {}", keycloakId);
        Contribuyente actualizado = servicioContribuyente.actualizarConKeycloak(
                keycloakId,
                dto.getNombre(),
                dto.getApellido(),
                dto.getEdad()
        );

        if (actualizado == null) {
            log.warn("Intento de actualización de contribuyente no encontrado para keycloakId: {}", keycloakId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(actualizado.getId());
        output.setNombre(actualizado.getNombre());
        output.setApellido(actualizado.getApellido());
        output.setEdad(actualizado.getEdad());
        log.info("Contribuyente actualizado para keycloakId: {}", keycloakId);
        return ResponseEntity.ok(output);
    }
}
