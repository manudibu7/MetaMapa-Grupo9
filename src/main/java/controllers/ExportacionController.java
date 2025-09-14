// controllers/ExportacionController.java
package controllers;

import Infrastructure.Outbox.BandejaDeSalida;
import dtos.output.ContribuyenteOutputDTO;
import dtos.output.HechoOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportacionController {

    private final BandejaDeSalida outbox;

    @GetMapping("/hechos")
    ResponseEntity<List<HechoOutputDTO>> obtenerContribuyente(){
        List<HechoOutputDTO> hechos = outbox.pendientesDeEnvio();
        outbox.limpiar();
        return ResponseEntity.status(200).body(hechos);
    }
}

