package controllers;

import domain.Contribucion;
import dtos.input.ArchivoInputDTO;
import dtos.input.ContribucionInputDTO;
import dtos.output.ContribucionOutputDTO;
import dtos.output.ContribuyenteOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ServicioContribuciones;

@RestController
@RequestMapping("/contribuciones")
public class controladorContribuciones {
    @Autowired
    private ServicioContribuciones servicioContribucion;

    @PostMapping
    ResponseEntity<Long> crearContribucion(@RequestBody ContribucionInputDTO contribucionInputDTO){
        long id = servicioContribucion.crear(contribucionInputDTO);
        return ResponseEntity.status(201).body(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> editarContribucion(@PathVariable long id, @RequestBody ContribucionInputDTO dto){
        servicioContribucion.editar(id, dto.getHecho());
        return ResponseEntity.status(200).build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<Void> agregarArchivo(@PathVariable long id, @RequestParam ArchivoInputDTO archivo){
        servicioContribucion.adjuntarArchivo(id,archivo);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ContribucionOutputDTO> verContribucion(@PathVariable long id){
        Contribucion c = servicioContribucion.obtener(id);
        ContribucionOutputDTO dto = servicioContribucion.contribucionAOutputDTO(c);
        return ResponseEntity.status(200).body(dto);
    }



}
