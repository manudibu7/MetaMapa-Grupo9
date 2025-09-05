// controllers/ExportacionController.java
package controllers;

import Infrastructure.Outbox.BandejaDeSalida;
import dtos.output.HechoOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportacionController {

    private final BandejaDeSalida outbox;

    @GetMapping("/hechos")
    public List<HechoOutputDTO> listarPendientes() {
        return outbox.pendientesDeEnvio();   // Spring los serializa a JSON automáticamente
    }
}

