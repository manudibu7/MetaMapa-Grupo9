package services;

import domain.Contribucion;
import domain.PoliticaEdicion;
import dtos.input.ContribucionInputDTO;
import repository.IContribucionesRepository;

public class ServicioContribuciones {
    private IContribucionesRepository repositorio;
    private PoliticaEdicion politicaEdicion;

    public ServicioContribuciones(IContribucionesRepository repositorio, PoliticaEdicion politicaEdicion) {
        this.repositorio = repositorio;
        this.politicaEdicion = politicaEdicion;
    }

    public Long crear(ContribucionInputDTO contribucionInputDTO){

    }


}
