// controllers/ExportacionController.java
package com.metamapa.controllers;

import com.metamapa.infrastructure.Outbox.BandejaDeSalida;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
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
    ResponseEntity<List<HechoOutputDTO>> obtenerHechos(){
        List<HechoOutputDTO> hechos = outbox.pendientesDeEnvio();  // Cambio de findAll() a pendientesDeEnvio()
        outbox.limpiar();  // Cambio de deleteAll() a limpiar()
        return ResponseEntity.status(200).body(hechos);
    }
}
