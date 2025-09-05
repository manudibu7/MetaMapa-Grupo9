package services;

import domain.Contribucion;
import domain.PoliticaEdicion;
import dtos.input.ContribucionInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.IContribucionesRepository;

@Service
public class ServicioContribuciones {
    @Autowired
    private IContribucionesRepository repositorio;
    private PoliticaEdicion politicaEdicion= new PoliticaEdicion();

    //public Long crear(ContribucionInputDTO contribucionInputDTO){    }


}
