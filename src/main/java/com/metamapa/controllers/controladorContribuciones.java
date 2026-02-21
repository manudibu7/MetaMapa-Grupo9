package com.metamapa.controllers;

import com.metamapa.domain.Contribucion;
import com.metamapa.dtos.input.ArchivoInputDTO;
import com.metamapa.dtos.input.ContribucionInputDTO;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioContribuciones;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/contribuciones")
public class controladorContribuciones {
    @Autowired
    private ServicioContribuciones servicioContribucion;

    @PostMapping
    ResponseEntity<Long> crearContribucion(@RequestBody ContribucionInputDTO contribucionInputDTO){
        log.info("Recibiendo solicitud para crear contribución: {}", contribucionInputDTO);
        long id = servicioContribucion.crear(contribucionInputDTO);
        return ResponseEntity.status(201).body(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> editarContribucion(@PathVariable long id, @RequestBody ContribucionInputDTO dto){
        log.info("Recibiendo solicitud para editar contribución id={} ", id);
        servicioContribucion.editar(id, dto.getHecho());
        return ResponseEntity.status(200).build();
    }

    /*@PatchMapping("/{id}")
    ResponseEntity<Void> agregarArchivo(@PathVariable long id, @RequestBody ArchivoInputDTO archivo){
        servicioContribucion.adjuntarArchivo(id,archivo);
        return ResponseEntity.status(200).build();
    }*/

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> subirArchivo(
            @PathVariable long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo) {

        log.info("Recibiendo solicitud para subir archivo a contribución id={}, tipo={}", id, tipo);
        servicioContribucion.adjuntarArchivoBinario(id, file, tipo);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ContribucionOutputDTO> verContribucion(@PathVariable long id){
        log.info("Recibiendo solicitud para ver contribución id={}", id);
        ContribucionOutputDTO c = servicioContribucion.obtener(id);
        return ResponseEntity.status(200).body(c);
    }

    /**
     * Obtiene todas las contribuciones de un contribuyente por su ID interno.
     * @param contribuyenteId ID interno del contribuyente
     * @return Lista de contribuciones del contribuyente (200 OK)
     */
    @GetMapping("/contribuyente/{contribuyenteId}")
    ResponseEntity<List<ContribucionOutputDTO>> obtenerContribucionesPorContribuyente(
            @PathVariable Long contribuyenteId) {
        log.info("Recibiendo solicitud para obtener contribuciones del contribuyente id={}", contribuyenteId);
        List<ContribucionOutputDTO> contribuciones =
                servicioContribucion.obtenerContribucionesPorContribuyente(contribuyenteId);
        log.debug("Contribuciones obtenidas para contribuyente id={}: {}", contribuyenteId, contribuciones.size());
        return ResponseEntity.ok(contribuciones);
    }

    /**
     * Obtiene todas las contribuciones de un contribuyente por su keycloakId.
     * @param keycloakId ID externo de Keycloak del contribuyente
     * @return Lista de contribuciones del contribuyente (200 OK)
     */
    @GetMapping("/keycloak/{keycloakId}")
    ResponseEntity<List<ContribucionOutputDTO>> obtenerContribucionesPorKeycloakId(
            @PathVariable String keycloakId) {
        log.info("Recibiendo solicitud para obtener contribuciones del contribuyente keycloakId={}", keycloakId);
        List<ContribucionOutputDTO> contribuciones =
                servicioContribucion.obtenerContribucionesPorKeycloakId(keycloakId);
        log.debug("Contribuciones obtenidas para contribuyente keycloakId={}: {}", keycloakId, contribuciones.size());
        return ResponseEntity.ok(contribuciones);
    }
}
