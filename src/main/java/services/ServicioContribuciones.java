package services;

import domain.*;
import dtos.input.ArchivoInputDTO;
import dtos.input.ContribucionInputDTO;
import dtos.input.HechoInputDTO;
import dtos.input.UbicacionInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.IContribucionesRepository;

@Service
public class ServicioContribuciones {
    @Autowired
    private IContribucionesRepository repositorio;
    final private PoliticaEdicion politicaEdicion= new PoliticaEdicion();
    @Autowired
    private ServicioContribuyente servicioContribuyente;

    public Long crear(ContribucionInputDTO contribucionInputDTO){
        Hecho hecho = hechoDtoToHecho(contribucionInputDTO.getHecho());
        Contribucion contribucion = new Contribucion();
        contribucion.setHecho(hecho);

        Contribuyente contribuyente = servicioContribuyente.buscarContribuyente(contribucionInputDTO.getIdContribuyente());

        contribucion.setContribuyente(contribuyente);
        repositorio.guardar(contribucion);
        return contribucion.getId();
    };

    public Hecho hechoDtoToHecho(HechoInputDTO dto){
        Hecho hecho = new Hecho();
        hecho.setTitulo(dto.getTitulo());
        hecho.setDescripcion(dto.getDescripcion());
        hecho.setFecha(dto.getFecha());
        hecho.setEtiqueta(new Etiqueta(dto.getEtiqueta()));
        hecho.setLugarDeOcurrencia(ubicacionDtoToUbicacion(dto.getUbicacion()));
        return hecho;
    }

    public Ubicacion ubicacionDtoToUbicacion(UbicacionInputDTO dto){
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(dto.getLatitud());
        ubicacion.setLongitud(dto.getLongitud());
        return ubicacion;
    }

    public void editar(long idContribucion, HechoInputDTO dto){
        Contribucion contribucion = repositorio.buscarPorId(idContribucion);
        if (politicaEdicion.puedeEditar(dto.getFecha())){
            Hecho hecho = hechoDtoToHecho(dto);
            contribucion.setHecho(hecho);
            repositorio.actualizar(contribucion);
        }
    }

    public Archivo archivoDtoToArchivo(ArchivoInputDTO dto){
        Archivo archivo = new Archivo();
        archivo.setTipoFromString(dto.getTipo());
        archivo.setUrl(dto.getUrl());
        return archivo;
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
        Contribucion contribucion = repositorio.buscarPorId(idContribucion);
        Archivo archivo = archivoDtoToArchivo(dto);
        contribucion.getHecho().setAdjunto(archivo);
        repositorio.actualizar(contribucion);
    }

    public Contribucion obtener(long id){
        return repositorio.buscarPorId(id);
    }


}
