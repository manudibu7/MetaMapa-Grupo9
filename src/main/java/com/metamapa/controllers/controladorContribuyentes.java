package com.metamapa.controllers;

import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioContribuyente;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/contribuyentes")
//@CrossOrigin(origins = "http://localhost:3000")
public class controladorContribuyentes {
    @Autowired
    private ServicioContribuyente servicioContribuyente;

    @PostMapping
    ResponseEntity<ContribuyenteOutputDTO> agregarContribuyente(@RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        long id = servicioContribuyente.registrarContribuyente(contribuyenteInputDTO);
        var c = servicioContribuyente.buscarContribuyente(id);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        output.setAnonimo(c.getAnonimo());

        return ResponseEntity.status(201).body(output);
    }

    @GetMapping
    ResponseEntity<List<ContribuyenteOutputDTO>> listarContribuyentes(){
        var contribuyentes = servicioContribuyente.listarContribuyentes();
        List<ContribuyenteOutputDTO> output = contribuyentes.stream()
                .map(c -> {
                    ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    dto.setApellido(c.getApellido());
                    dto.setEdad(c.getEdad());
                    dto.setAnonimo(c.getAnonimo());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(200).body(output);
    }

    @GetMapping("/{id}")
    ResponseEntity<ContribuyenteOutputDTO> obtenerContribuyente(@PathVariable long id){
        ContribuyenteOutputDTO contribuyenteOutputDTO = new ContribuyenteOutputDTO();
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).body(null);
        contribuyenteOutputDTO.setId(c.getId());
        contribuyenteOutputDTO.setNombre(c.getNombre());
        contribuyenteOutputDTO.setApellido(c.getApellido());
        contribuyenteOutputDTO.setEdad(c.getEdad());
        contribuyenteOutputDTO.setAnonimo(c.getAnonimo());
        return ResponseEntity.status(200).body(contribuyenteOutputDTO);
    }

    @PutMapping("/{id}")
    ResponseEntity<ContribuyenteOutputDTO> actualizarContribuyente(@PathVariable long id, @RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).body(null);

        c.setNombre(contribuyenteInputDTO.getNombre());
        c.setApellido(contribuyenteInputDTO.getApellido());
        c.setEdad(contribuyenteInputDTO.getEdad());
        servicioContribuyente.actualizarContribuyente(c);

        ContribuyenteOutputDTO output = new ContribuyenteOutputDTO();
        output.setId(c.getId());
        output.setNombre(c.getNombre());
        output.setApellido(c.getApellido());
        output.setEdad(c.getEdad());
        output.setAnonimo(c.getAnonimo());

        return ResponseEntity.status(200).body(output);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminarContribuyente(@PathVariable long id){
        var c = servicioContribuyente.buscarContribuyente(id);
        if (c == null) return ResponseEntity.status(404).build();

        servicioContribuyente.eliminarContribuyente(id);
        return ResponseEntity.status(204).build();
    }
}
