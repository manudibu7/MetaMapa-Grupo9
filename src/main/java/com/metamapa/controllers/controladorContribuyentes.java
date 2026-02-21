package com.metamapa.controllers;

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
    public ResponseEntity<ContribuyenteSistemaResponse> syncKeycloak(@RequestBody ContribuyenteKeycloakRequest request) {
        // Validación adicional en el controller
        if (request == null || request.getKeycloakId() == null || request.getKeycloakId().trim().isEmpty()) {
            log.warn("Intento de sincronización con Keycloak sin keycloakId válido");
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
        log.debug("Contribuyente sincronizado con Keycloak: idSistema={}, keycloakId={}", response.getIdSistema(), response.getKeycloakId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<ContribuyenteOutputDTO> agregarContribuyente(@RequestBody ContribuyenteInputDTO contribuyenteInputDTO){

        log.info("Recibiendo solicitud para agregar contribuyente: {}", contribuyenteInputDTO);
        long id = servicioContribuyente.registrarContribuyente(contribuyenteInputDTO);

        var c = servicioContribuyente.buscarContribuyente(id);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());

        return ResponseEntity.status(201).body(output);
    }

    @GetMapping
    ResponseEntity<List<ContribuyenteOutputDTO>> listarContribuyentes(){

        log.info("Recibiendo solicitud para listar contribuyentes");
        var contribuyentes = servicioContribuyente.listarContribuyentes();
        log.debug("Contribuyentes obtenidos: {}", contribuyentes.size());
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
    ResponseEntity<ContribuyenteOutputDTO> obtenerContribuyente(@PathVariable long id){
        log.info("Recibiendo solicitud para obtener contribuyente id={}", id);
        ContribuyenteOutputDTO contribuyenteOutputDTO = new ContribuyenteOutputDTO();
        var c = servicioContribuyente.buscarContribuyente(id);
        log.debug("Contribuyente obtenido para id={}: {}", id, c != null ? c.getNombre() : "null");
        if (c == null) {
            log.warn("Contribuyente no encontrado para id={}", id);
            return  ResponseEntity.status(404).body(null);
        }
        contribuyenteOutputDTO.setId(c.getId());
        contribuyenteOutputDTO.setNombre(c.getNombre());
        contribuyenteOutputDTO.setApellido(c.getApellido());
        contribuyenteOutputDTO.setEdad(c.getEdad());
        return ResponseEntity.status(200).body(contribuyenteOutputDTO);
    }

    @PutMapping("/{id}")
    ResponseEntity<ContribuyenteOutputDTO> actualizarContribuyente(@PathVariable long id, @RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
      log.info("Recibiendo solicitud para actualizar contribuyente id={}", id);
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null){
            log.warn("Contribuyente no encontrado para actualización id={}", id);
            return ResponseEntity.status(404).body(null);
        }

        c.setNombre(contribuyenteInputDTO.getNombre());
        c.setApellido(contribuyenteInputDTO.getApellido());
        c.setEdad(contribuyenteInputDTO.getEdad());
        servicioContribuyente.actualizarContribuyente(c);
        log.info("Contribuyente actualizado id={}", id);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());

        return ResponseEntity.status(200).body(output);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminarContribuyente(@PathVariable long id){
        log.info("Recibiendo solicitud para eliminar contribuyente id={}", id);
        var c = servicioContribuyente.buscarContribuyente(id);

        if (c == null){
            log.warn("Contribuyente no encontrado para eliminación id={}", id);
            return ResponseEntity.status(404).build();
        }

        servicioContribuyente.eliminarContribuyente(id);
        log.info("Contribuyente eliminado id={}", id);
        return ResponseEntity.status(204).build();
    }
    // son distintos a los que estan arriba porque estos funcionan con keycloakID y no con Id comun
    @GetMapping("/me/{keycloakId}")
    public ResponseEntity<ContribuyenteOutputDTO> obtenerConKeycloak(
            @PathVariable String keycloakId
    ) {
        log.info("Recibiendo solicitud para obtener contribuyente por keycloakId={}", keycloakId);
        Contribuyente c = servicioContribuyente.buscarContribuyentePorKeycloakId(keycloakId);
        log.info("Contribuyente obtenido para keycloakId={}: {}", keycloakId, c != null ? c.getNombre() : "null");

        if (c == null) {
            log.warn("Contribuyente no encontrado para keycloakId={}", keycloakId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setEdad(c.getEdad());

        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me/{keycloakId}")
    public ResponseEntity<ContribuyenteOutputDTO> actualizarConKeycloak(
            @PathVariable String keycloakId,
            @RequestBody ContribuyenteInputDTO dto
    ) {
        log.info("Recibiendo solicitud para actualizar contribuyente por keycloakId={}", keycloakId);
        Contribuyente actualizado = servicioContribuyente.actualizarConKeycloak(
                keycloakId,
                dto.getNombre(),
                dto.getApellido(),
                dto.getEdad()
        );



        if (actualizado == null) {
            log.warn("Contribuyente no encontrado para actualización por keycloakId={}", keycloakId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Contribuyente actualizado por keycloakId={}", keycloakId);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(actualizado.getId());
        output.setNombre(actualizado.getNombre());
        output.setApellido(actualizado.getApellido());
        output.setEdad(actualizado.getEdad());

        return ResponseEntity.ok(output);
    }
}
