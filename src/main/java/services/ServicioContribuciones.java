package services;

import domain.*;
import dtos.input.ArchivoInputDTO;
import dtos.input.ContribucionInputDTO;
import dtos.input.HechoInputDTO;
import dtos.input.UbicacionInputDTO;
import dtos.output.*;
import mappers.ArchivoMapper;
import mappers.ContribucionMapper;
import mappers.HechoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.IContribucionesRepository;

import java.time.LocalDate;

@Service
public class ServicioContribuciones {
    @Autowired
    private IContribucionesRepository repositorio;
    final private PoliticaEdicion politicaEdicion= new PoliticaEdicion();
    @Autowired
    private ServicioContribuyente servicioContribuyente;
    private HechoMapper hechoMapper = new HechoMapper();
    private ArchivoMapper archivoMapper = new ArchivoMapper();
    private ContribucionMapper contribucionMapper = new ContribucionMapper();

    public Long crear(ContribucionInputDTO contribucionInputDTO){
        Hecho hecho = hechoMapper.hechoDtoToHecho(contribucionInputDTO.getHecho());
        Contribucion contribucion = new Contribucion();
        contribucion.setHecho(hecho);
        contribucion.getRevision().setContribucion(contribucion);

        Contribuyente contribuyente = servicioContribuyente.buscarContribuyente(contribucionInputDTO.getIdContribuyente());

        contribucion.setContribuyente(contribuyente);
        repositorio.guardar(contribucion);
        return contribucion.getId();
    };

    public void editar(long idContribucion, HechoInputDTO dto){
        Contribucion contribucion = repositorio.buscarPorId(idContribucion);
        if (politicaEdicion.puedeEditar(dto.getFecha())){
            Hecho hecho = hechoMapper.hechoDtoToHecho(dto);
            contribucion.setHecho(hecho);
            contribucion.getRevision().setEstado(EstadoRevision.PENDIENTE);
        } else {
            throw new RuntimeException("No se puede editar la contribucion despues de 7 deas.");
        }
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
        Contribucion contribucion = repositorio.buscarPorId(idContribucion);
        Archivo archivo = archivoMapper.archivoDtoToArchivo(dto);
        contribucion.getHecho().setAdjunto(archivo);
    }

    public ContribucionOutputDTO obtener(long id){
        Contribucion c = repositorio.buscarPorId(id);
        ContribucionOutputDTO dto = contribucionMapper.contribucionToOutputDTO(c);
        return dto;
    }


}
