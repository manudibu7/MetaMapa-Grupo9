// controllers/ExportacionController.java
package com.metamapa.controllers;

import com.metamapa.infrastructure.Outbox.BandejaDeSalida;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportacionController {

    private final BandejaDeSalida outbox;

    @GetMapping("/hechos")
    ResponseEntity<List<HechoOutputDTO>> obtenerHechos(){
        log.info("Recibiendo solicitud para exportar hechos desde la bandeja de salida");
        List<HechoOutputDTO> hechos = outbox.pendientesDeEnvio();  // Cambio de findAll() a pendientesDeEnvio()
        outbox.limpiar();// Cambio de deleteAll() a limpiar()
        log.debug("Exportando {} hechos desde la bandeja de salida", hechos.size());
        return ResponseEntity.status(200).body(hechos);
    }
}
