package com.metamapa.services;

import com.metamapa.domain.Contribuyente;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metamapa.repository.IContribuyentesRepository;

@Service
public class ServicioContribuyente {
    @Autowired
    private IContribuyentesRepository repositorio;

    public long registrarContribuyente(ContribuyenteInputDTO contribuyenteInputDTO){
        // Validaciones
        if (contribuyenteInputDTO == null) {
            throw new DatosInvalidosException("Los datos del contribuyente no pueden ser nulos");
        }

        // Validar edad si se proporciona
        if (contribuyenteInputDTO.getEdad() != null) {
            if (contribuyenteInputDTO.getEdad() < 0 || contribuyenteInputDTO.getEdad() > 150) {
                throw new DatosInvalidosException("La edad debe estar entre 0 y 150 años");
            }
        }

        // Validar que si proporciona nombre o apellido, ambos deben estar presentes
        boolean tieneNombre = contribuyenteInputDTO.getNombre() != null && !contribuyenteInputDTO.getNombre().trim().isEmpty();
        boolean tieneApellido = contribuyenteInputDTO.getApellido() != null && !contribuyenteInputDTO.getApellido().trim().isEmpty();

        if (tieneNombre && !tieneApellido) {
            throw new DatosInvalidosException("Si proporciona nombre, debe proporcionar también el apellido");
        }
        if (!tieneNombre && tieneApellido) {
            throw new DatosInvalidosException("Si proporciona apellido, debe proporcionar también el nombre");
        }

        Contribuyente nuevo = new Contribuyente(contribuyenteInputDTO.getNombre(),
                                                contribuyenteInputDTO.getApellido(),
                                                contribuyenteInputDTO.getEdad());
        if(contribuyenteInputDTO.getNombre() == null && contribuyenteInputDTO.getApellido() == null){
            nuevo.setAnonimo(true);
        }
        repositorio.save(nuevo);
        return nuevo.getId();
    }

    public Contribuyente buscarContribuyente(Long id){
        if (id == null || id <= 0) {
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }

        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + id));
    }
}
