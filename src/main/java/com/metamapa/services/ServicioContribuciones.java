package com.metamapa.services;

import com.metamapa.domain.*;
import com.metamapa.dtos.input.ArchivoInputDTO;
import com.metamapa.dtos.input.ContribucionInputDTO;
import com.metamapa.dtos.input.HechoInputDTO;
import com.metamapa.dtos.input.UbicacionInputDTO;
import com.metamapa.dtos.output.*;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.EdicionNoPermitidaException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import com.metamapa.mappers.ArchivoMapper;
import com.metamapa.mappers.ContribucionMapper;
import com.metamapa.mappers.HechoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metamapa.repository.IContribucionesRepository;

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
        // Validaciones
        if (contribucionInputDTO == null) {
            throw new DatosInvalidosException("Los datos de la contribución no pueden ser nulos");
        }
        if (contribucionInputDTO.getIdContribuyente() == null) {
            throw new DatosInvalidosException("El ID del contribuyente es obligatorio");
        }
        if (contribucionInputDTO.getHecho() == null) {
            throw new DatosInvalidosException("El hecho es obligatorio");
        }

        validarHecho(contribucionInputDTO.getHecho());

        Hecho hecho = hechoMapper.hechoDtoToHecho(contribucionInputDTO.getHecho());
        Contribucion contribucion = new Contribucion();
        contribucion.setHecho(hecho);
        contribucion.getRevision().setContribucion(contribucion);
        contribucion.setFechaDeCarga(LocalDate.now());

        Contribuyente contribuyente = servicioContribuyente.buscarContribuyente(contribucionInputDTO.getIdContribuyente());
        contribucion.setContribuyente(contribuyente);

        repositorio.save(contribucion);
        return contribucion.getId();
    }

    public void editar(long idContribucion, HechoInputDTO dto){
        // Validaciones
        if (dto == null) {
            throw new DatosInvalidosException("Los datos del hecho no pueden ser nulos");
        }
        validarHecho(dto);

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        if (contribucion.getFechaDeCarga() == null) {
            contribucion.setFechaDeCarga(LocalDate.now());
        }

        if (!politicaEdicion.puedeEditar(contribucion.getFechaDeCarga())){
            throw new EdicionNoPermitidaException("No se puede editar la contribución después de 7 días");
        }

        Hecho hecho = hechoMapper.hechoDtoToHecho(dto);
        contribucion.setHecho(hecho);
        contribucion.getRevision().setEstado(EstadoRevision.PENDIENTE);
        repositorio.save(contribucion);
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
        // Validaciones
        if (dto == null) {
            throw new DatosInvalidosException("Los datos del archivo no pueden ser nulos");
        }
        if (dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            throw new DatosInvalidosException("La URL del archivo es obligatoria");
        }
        if (dto.getTipo() == null) {
            throw new DatosInvalidosException("El tipo de media es obligatorio");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Archivo archivo = archivoMapper.archivoDtoToArchivo(dto);
        contribucion.getHecho().setAdjunto(archivo);
        repositorio.save(contribucion);
    }

    public ContribucionOutputDTO obtener(long id){
        Contribucion c = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + id));

        ContribucionOutputDTO dto = contribucionMapper.contribucionToOutputDTO(c);
        return dto;
    }

    private void validarHecho(HechoInputDTO hecho) {
        if (hecho.getTitulo() == null || hecho.getTitulo().trim().isEmpty()) {
            throw new DatosInvalidosException("El título del hecho es obligatorio");
        }
        if (hecho.getTitulo().length() > 200) {
            throw new DatosInvalidosException("El título no puede exceder 200 caracteres");
        }
        if (hecho.getDescripcion() == null || hecho.getDescripcion().trim().isEmpty()) {
            throw new DatosInvalidosException("La descripción del hecho es obligatoria");
        }
        if (hecho.getFecha() == null) {
            throw new DatosInvalidosException("La fecha del hecho es obligatoria");
        }
        if (hecho.getFecha().isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha del hecho no puede ser futura");
        }
        if (hecho.getUbicacion() == null) {
            throw new DatosInvalidosException("La ubicación es obligatoria");
        }

        validarUbicacion(hecho.getUbicacion());

        if (hecho.getCategoria() == null || hecho.getCategoria().trim().isEmpty()) {
            throw new DatosInvalidosException("La categoría es obligatoria");
        }
    }

    private void validarUbicacion(UbicacionInputDTO ubicacion) {
        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            throw new DatosInvalidosException("La latitud y longitud son obligatorias");
        }
        if (ubicacion.getLatitud() < -90 || ubicacion.getLatitud() > 90) {
            throw new DatosInvalidosException("La latitud debe estar entre -90 y 90");
        }
        if (ubicacion.getLongitud() < -180 || ubicacion.getLongitud() > 180) {
            throw new DatosInvalidosException("La longitud debe estar entre -180 y 180");
        }
    }
}
