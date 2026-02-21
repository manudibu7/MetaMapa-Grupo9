// controllers/ExportacionController.java
package com.metamapa.controllers;

import com.metamapa.infrastructure.Outbox.BandejaDeSalida;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@Slf4j
public class ExportacionController {

    private final BandejaDeSalida outbox;

    @GetMapping("/hechos")
    ResponseEntity<List<HechoOutputDTO>> obtenerHechos(){
        log.info(" Inciando Exportar hechos");
        List<HechoOutputDTO> hechos = outbox.pendientesDeEnvio();  // Cambio de findAll() a pendientesDeEnvio()
        outbox.limpiar();  // Cambio de deleteAll() a limpiar()
        log.debug("Se obtuvieron de hechos={}" , hechos != null ? hechos.size() : 0);
        return ResponseEntity.status(200).body(hechos);
    }
}
