package com.metamapa.controllers;

import com.metamapa.domain.Contribucion;
import com.metamapa.domain.EstadoRevision;
import com.metamapa.domain.Revision;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.RevisionOutputDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioRevisiones;

import java.util.List;

@RestController
@RequestMapping("/revisiones")
@RequiredArgsConstructor // Lombok: genera constructor con servicio inyectado
@CrossOrigin(origins = "http://localhost:3000") // opcional para pruebas desde front
public class ControladorRevisiones {

    private final ServicioRevisiones servicioRevision;

    // GET /revisiones/pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<ContribucionOutputDTO>> listarPendientes() {
        List<ContribucionOutputDTO> pendientes = servicioRevision.listarPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // GET /revisiones/{id}
    @GetMapping("/{idContribucion}")
    public ResponseEntity<RevisionOutputDTO> verDetalle(@PathVariable Long idContribucion) {
        RevisionOutputDTO rev = servicioRevision.detalle(idContribucion);
        return ResponseEntity.ok(rev);
    }


    // POST /revisiones/{id}/aceptar
    @PostMapping("/{idContribucion}/aceptar")
    public ResponseEntity<Void> aceptar(@PathVariable Long idContribucion, @RequestBody(required = false) ComentariosDTO body) {
        String comentarios = body != null ? body.getComentarios() : null;
        servicioRevision.aceptar(idContribucion, comentarios);
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/aceptar-con-cambios
    @PostMapping("/{idContribucion}/aceptar-con-cambios")
    public ResponseEntity<Void> aceptarConCambios(@PathVariable Long idContribucion, @RequestBody ComentariosDTO body) {
        servicioRevision.aceptarConSugerencias(idContribucion, body != null ? body.getComentarios() : null
        );
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/rechazar
    @PostMapping("/{idContribucion}/rechazar")
    public ResponseEntity<Void> rechazar(@PathVariable Long idContribucion, @RequestBody ComentariosDTO body) {
        servicioRevision.rechazar(idContribucion, body != null ? body.getComentarios() : null
        );
        return ResponseEntity.noContent().build();
    }

    // DTO mínimo para recibir comentarios en los POST
    @Data
    public static class ComentariosDTO {
        private String comentarios;
    }
}
