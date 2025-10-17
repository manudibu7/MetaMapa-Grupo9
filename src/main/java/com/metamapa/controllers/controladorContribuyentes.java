package com.metamapa.controllers;

import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.dtos.output.ContribuyenteOutputDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metamapa.services.ServicioContribuyente;

@RestController
@RequestMapping ("/contribuyentes")
//@CrossOrigin(origins = "http://localhost:3000")
public class controladorContribuyentes {
    @Autowired
    private ServicioContribuyente servicioContribuyente;

    @PostMapping
    ResponseEntity<Long> agregarContribuyente(@RequestBody ContribuyenteInputDTO contribuyenteInputDTO){
        long id = servicioContribuyente.registrarContribuyente(contribuyenteInputDTO);
        return ResponseEntity.status(201).body(id);
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



}
