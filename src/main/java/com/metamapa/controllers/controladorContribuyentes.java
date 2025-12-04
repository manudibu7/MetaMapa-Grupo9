package com.metamapa.controllers;

import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.dtos.input.ContribuyenteKeycloakRequest;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import com.metamapa.dtos.output.ContribuyenteSistemaResponse;
import com.metamapa.exceptions.DatosInvalidosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioContribuyente;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/contribuyentes")
//@CrossOrigin(origins = "http://localhost:3000")
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

        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<ContribuyenteOutputDTO> agregarContribuyente(@RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        long id = servicioContribuyente.registrarContribuyente(contribuyenteInputDTO);
        var c = servicioContribuyente.buscarContribuyente(id);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        output.setAnonimo(c.getAnonimo());

        return ResponseEntity.status(201).body(output);
    }

    @GetMapping
    ResponseEntity<List<ContribuyenteOutputDTO>> listarContribuyentes(){
        var contribuyentes = servicioContribuyente.listarContribuyentes();
        List<ContribuyenteOutputDTO> output = contribuyentes.stream()
                .map(c -> {
                    ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    dto.setApellido(c.getApellido());
                    dto.setEdad(c.getEdad());
                    dto.setAnonimo(c.getAnonimo());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(200).body(output);
    }

    @GetMapping("/{id}")
    ResponseEntity<ContribuyenteOutputDTO> obtenerContribuyente(@PathVariable long id){
        ContribuyenteOutputDTO contribuyenteOutputDTO = new ContribuyenteOutputDTO();
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).body(null);
        contribuyenteOutputDTO.setId(c.getId());
        contribuyenteOutputDTO.setNombre(c.getNombre());
        contribuyenteOutputDTO.setApellido(c.getApellido());
        contribuyenteOutputDTO.setEdad(c.getEdad());
        contribuyenteOutputDTO.setAnonimo(c.getAnonimo());
        return ResponseEntity.status(200).body(contribuyenteOutputDTO);
    }

    @PutMapping("/{id}")
    ResponseEntity<ContribuyenteOutputDTO> actualizarContribuyente(@PathVariable long id, @RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).body(null);

        c.setNombre(contribuyenteInputDTO.getNombre());
        c.setApellido(contribuyenteInputDTO.getApellido());
        c.setEdad(contribuyenteInputDTO.getEdad());
        servicioContribuyente.actualizarContribuyente(c);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        output.setAnonimo(c.getAnonimo());

        return ResponseEntity.status(200).body(output);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminarContribuyente(@PathVariable long id){
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).build();

        servicioContribuyente.eliminarContribuyente(id);
        return ResponseEntity.status(204).build();
    }
}
