package controllers;

import domain.Contribucion;
import domain.EstadoRevision;
import domain.Revision;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ServicioRevisiones;

import java.util.List;

@RestController
@RequestMapping("/revisiones")
@RequiredArgsConstructor // Lombok: genera constructor con servicio inyectado
@CrossOrigin(origins = "http://localhost:3000") // opcional para pruebas desde front
public class ControladorRevisiones {

    private final ServicioRevisiones servicioRevision;

    // GET /revisiones/pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<Contribucion>> listarPendientes() {
        List<Contribucion> pendientes = servicioRevision.listarPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // GET /revisiones/{id}
    @GetMapping("/{idContribucion}")
    public ResponseEntity<Revision> verDetalle(@PathVariable Long idContribucion) {
        Revision rev = servicioRevision.detalle(idContribucion);
        return ResponseEntity.ok(rev);
    }

    // POST /revisiones/{id}/en-revision
    @PostMapping("/{idContribucion}/en-revision")
    public ResponseEntity<Void> marcarEnRevision(@PathVariable Long idContribucion) {
        servicioRevision.marcarRevision(idContribucion);
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/aceptar
    @PostMapping("/{idContribucion}/aceptar")
    public ResponseEntity<Void> aceptar(@PathVariable Long idContribucion,
                                        @RequestBody(required = false) ComentariosDTO body) {
        String comentarios = body != null ? body.getComentarios() : null;
        servicioRevision.resolverRevision(idContribucion, EstadoRevision.ACEPTADO, comentarios);
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/aceptar-con-cambios
    @PostMapping("/{idContribucion}/aceptar-con-cambios")
    public ResponseEntity<Void> aceptarConCambios(@PathVariable Long idContribucion,
                                                  @RequestBody ComentariosDTO body) {
        servicioRevision.resolverRevision(
                idContribucion,
                EstadoRevision.ACEPTADO_CON_CAMBIOS,
                body != null ? body.getComentarios() : null
        );
        return ResponseEntity.noContent().build();
    }

    // POST /revisiones/{id}/rechazar
    @PostMapping("/{idContribucion}/rechazar")
    public ResponseEntity<Void> rechazar(@PathVariable Long idContribucion,
                                         @RequestBody ComentariosDTO body) {
        servicioRevision.resolverRevision(
                idContribucion,
                EstadoRevision.RECHAZADO,
                body != null ? body.getComentarios() : null
        );
        return ResponseEntity.noContent().build();
    }

    // DTO mínimo para recibir comentarios en los POST
    @Data
    public static class ComentariosDTO {
        private String comentarios;
    }
}
