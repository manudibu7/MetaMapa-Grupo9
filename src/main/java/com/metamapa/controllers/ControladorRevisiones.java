package com.metamapa.controllers;

import com.metamapa.domain.Contribucion;
import com.metamapa.domain.EstadoRevision;
import com.metamapa.domain.Revision;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.RevisionOutputDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioRevisiones;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/revisiones")
@RequiredArgsConstructor // Lombok: genera constructor con servicio inyectado
@CrossOrigin(origins = "http://localhost:3000") // opcional para pruebas desde front
public class ControladorRevisiones {

    private final ServicioRevisiones servicioRevision;

    // GET /revisiones/pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<ContribucionOutputDTO>> listarPendientes() {
        log.info("Recibiendo solicitud para listar contribuciones pendientes de revisión");
        List<ContribucionOutputDTO> pendientes = servicioRevision.listarPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // GET /revisiones/{id}
    @GetMapping("/{idContribucion}")
    public ResponseEntity<RevisionOutputDTO> verDetalle(@PathVariable Long idContribucion) {
        log.warn("Recibiendo solicitud para ver detalle de revisión de contribución con ID: {}", idContribucion);
        RevisionOutputDTO rev = servicioRevision.detalle(idContribucion);
        return ResponseEntity.ok(rev);
    }


    // POST /revisiones/{id}/aceptar
    @PostMapping("/{idContribucion}/aceptar")
    public ResponseEntity<Void> aceptar(@PathVariable Long idContribucion, @RequestBody(required = false) ComentariosDTO body) {
        log.info("Recibiendo solicitud para aceptar revisión de contribución con ID: {}",
                idContribucion);
        String comentarios = body != null ? body.getComentarios() : null;
        Long idContribuyente = body != null ? body.getContribuyenteId() : null;
        servicioRevision.aceptar(idContribucion, comentarios, idContribuyente);
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/aceptar-con-cambios
    @PostMapping("/{idContribucion}/aceptar-con-cambios")
    public ResponseEntity<Void> aceptarConCambios(@PathVariable Long idContribucion, @RequestBody ComentariosDTO body) {
        servicioRevision.aceptarConSugerencias(idContribucion, body != null ? body.getComentarios() : null,
                body != null ? body.getContribuyenteId() : null
        );
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/rechazar
    @PostMapping("/{idContribucion}/rechazar")
    public ResponseEntity<Void> rechazar(@PathVariable Long idContribucion, @RequestBody ComentariosDTO body) {
        servicioRevision.rechazar(idContribucion, body != null ? body.getComentarios() : null,
                body != null ? body.getContribuyenteId() : null
        );
        return ResponseEntity.noContent().build();
    }

    // DTO mínimo para recibir comentarios en los POST
    @Data
    public static class ComentariosDTO {
        private String comentarios;
        // Nuevo campo para recibir el id del contribuyente responsable
        @com.fasterxml.jackson.annotation.JsonProperty("contribuyenteId")
        private Long contribuyenteId;
    }
}
