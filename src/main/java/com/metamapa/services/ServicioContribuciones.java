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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metamapa.repository.IContribucionesRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ServicioContribuciones {
    @Autowired
    private IContribucionesRepository repositorio;
    final private PoliticaEdicion politicaEdicion= new PoliticaEdicion();
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ServicioContribuyente servicioContribuyente;
    private HechoMapper hechoMapper = new HechoMapper();
    private ArchivoMapper archivoMapper = new ArchivoMapper();
    private ContribucionMapper contribucionMapper = new ContribucionMapper();

    /**
     * Obtiene todas las contribuciones de un contribuyente por su ID interno.
     * @param contribuyenteId ID interno del contribuyente
     * @return Lista de ContribucionOutputDTO del contribuyente
     * @throws DatosInvalidosException si el ID es null o inválido
     * @throws RecursoNoEncontradoException si el contribuyente no existe
     */
    public List<ContribucionOutputDTO> obtenerContribucionesPorContribuyente(Long contribuyenteId) {
        log.info("Obteniendo contribuciones para contribuyenteId={}", contribuyenteId);
        if (contribuyenteId == null || contribuyenteId <= 0) {
            log.warn("ID de contribuyente inválido: {}", contribuyenteId);
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }

        // Verificar que el contribuyente existe
        servicioContribuyente.buscarContribuyente(contribuyenteId);
        log.info("Contribuyente encontrado para ID: {}", contribuyenteId);
        List<Contribucion> contribuciones = repositorio.findByContribuyenteId(contribuyenteId);
        log.debug("Contribuciones encontradas para contribuyenteId={}: {}", contribuyenteId, contribuciones.size());

        return contribuciones.stream()
                .map(contribucionMapper::contribucionToOutputDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las contribuciones de un contribuyente por su keycloakId.
     * @param keycloakId ID externo de Keycloak del contribuyente
     * @return Lista de ContribucionOutputDTO del contribuyente
     * @throws DatosInvalidosException si el keycloakId es null o vacío
     * @throws RecursoNoEncontradoException si no se encuentra el contribuyente
     */
    public List<ContribucionOutputDTO> obtenerContribucionesPorKeycloakId(String keycloakId) {
        log.debug("Obteniendo contribuciones para keycloakId={}", keycloakId);
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.debug("KeycloakId inválido: '{}'", keycloakId);
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        List<Contribucion> contribuciones = repositorio.findByContribuyenteKeycloakId(keycloakId);
        log.debug("Contribuciones encontradas para keycloakId={}: {}", keycloakId, contribuciones.size());
        // Si no hay contribuciones, verificamos si el contribuyente existe
        if (contribuciones.isEmpty()) {
            // Esto lanzará RecursoNoEncontradoException si el contribuyente no existe
            var contribuyente = servicioContribuyente.buscarContribuyentePorKeycloakId(keycloakId);
            log.debug("Contribuyente encontrado para keycloakId: {}", keycloakId);
            if (contribuyente == null) {
                log.warn("Contribuyente no encontrado para keycloakId: {}", keycloakId);
                throw new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakId);
            }
        }

        return contribuciones.stream()
                .map(contribucionMapper::contribucionToOutputDTO)
                .collect(Collectors.toList());
    }

    public Long crear(ContribucionInputDTO contribucionInputDTO){
        // Validaciones
        log.info("Creando contribución con datos: {}", contribucionInputDTO);
        if (contribucionInputDTO == null) {
            log.warn("Datos de contribución nulos");
            throw new DatosInvalidosException("Los datos de la contribución no pueden ser nulos");
        }
        if (contribucionInputDTO.getIdContribuyente() == null) {
            log.warn("ID de contribuyente nulo");
            throw new DatosInvalidosException("El ID del contribuyente es obligatorio");
        }
        if (contribucionInputDTO.getHecho() == null) {
            log.warn("Datos del hecho nulos");
            throw new DatosInvalidosException("El hecho es obligatorio");
        }

        validarHecho(contribucionInputDTO.getHecho());
        log.info("Datos de hecho validados correctamente para contribución");

        Hecho hecho = hechoMapper.hechoDtoToHecho(contribucionInputDTO.getHecho());
        Contribucion contribucion = new Contribucion();
        contribucion.setHecho(hecho);
        contribucion.getRevision().setContribucion(contribucion);
        contribucion.setFechaDeCarga(LocalDate.now());

        Contribuyente contribuyente = servicioContribuyente.buscarContribuyente(contribucionInputDTO.getIdContribuyente());
        contribucion.setContribuyente(contribuyente);
        contribucion.setAnonimo(contribucionInputDTO.getAnonimo());
        repositorio.save(contribucion);
        log.info("Contribución creada con ID: {}", contribucion.getId());
        return contribucion.getId();
    }

    public void editar(long idContribucion, HechoInputDTO dto){
        // Validaciones

        log.info("Editando contribución ID={} ", idContribucion);
        if (dto == null) {
            log.warn("Datos del hecho nulos para contribución ID={}", idContribucion);
            throw new DatosInvalidosException("Los datos del hecho no pueden ser nulos");
        }
        validarHecho(dto);
        log.info("Datos de hecho validados correctamente para contribución ID={}", idContribucion);

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() ->
                {   log.warn("Contribución no encontrada para ID={}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        if (contribucion.getFechaDeCarga() == null) {
            contribucion.setFechaDeCarga(LocalDate.now());
        }

        if (!politicaEdicion.puedeEditar(contribucion.getFechaDeCarga())){
            log.warn("Edición no permitida para contribución ID={} con fecha de carga {}", idContribucion, contribucion.getFechaDeCarga());
            throw new EdicionNoPermitidaException("No se puede editar la contribución después de 7 días");
        }

        Hecho hecho = hechoMapper.hechoDtoToHecho(dto);
        contribucion.setHecho(hecho);
        contribucion.getRevision().setEstado(EstadoRevision.PENDIENTE);
        log.info("Contribución ID={} editada correctamente, estado de revisión actualizado a PENDIENTE", idContribucion);
        repositorio.save(contribucion);
        log.debug("Contribución ID={} guardada en repositorio después de edición", idContribucion);
    }

    public void adjuntarArchivoBinario(long idContribucion, MultipartFile file, String tipoStr) {
        // 1. Validaciones básicas

        log.info("Adjuntando archivo a contribución ID={} con tipo '{}'", idContribucion, tipoStr);
        if (file.isEmpty()) {
            log.warn("Archivo vacío para contribución ID={}", idContribucion);
            throw new DatosInvalidosException("El archivo no puede estar vacío");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada para ID={}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        try {
            // 2. Guardar el archivo físicamente (SIMULACIÓN LOCAL)
            // En producción, aquí llamarías a S3 o Cloudinary

            // Generar la URL (En local sería algo así, en la nube te la da el proveedor)
            //String urlGenerada = "/uploads/" + filename;

            // 3. Crear la entidad Archivo
            String urlArchivo = cloudinaryService.subirArchivo(file);
            log.info("Archivo subido a Cloudinary para contribución ID={}, URL: {}", idContribucion, urlArchivo);
            // 3. Crear la entidad Archivo
            Archivo archivo = new Archivo();
            archivo.setUrl(urlArchivo);
            archivo.setTamanio(String.valueOf(file.getSize()));

            // Convertir el String "IMAGEN", "VIDEO" al Enum
            try {
                archivo.setTipo(TipoMedia.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                archivo.setTipo(TipoMedia.TEXTO); // O lanzar excepción
            }

            // 4. Vincular con el Hecho
            contribucion.getHecho().agregarAdjunto(archivo);
            repositorio.save(contribucion);
            log.info("Archivo adjuntado correctamente a contribución ID={}", idContribucion);

        } catch (IllegalArgumentException e) {
                log.warn("Tipo de archivo no válido '{}' para contribución ID={}", tipoStr, idContribucion);
            throw new DatosInvalidosException("Tipo de archivo no válido: " + tipoStr);        }
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
        log.info("Adjuntando archivo a contribución ID={} ", idContribucion);
        if (dto == null) {
            log.warn("Datos del archivo nulos para contribución ID={}", idContribucion);
            throw new DatosInvalidosException("Los datos del archivo no pueden ser nulos");
        }
        if (dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            log.warn("URL del archivo vacía para contribución ID={}", idContribucion);
            throw new DatosInvalidosException("La URL del archivo es obligatoria");
        }
        if (dto.getTipo() == null) {
            log.warn("Tipo de archivo nulo para contribución ID={}", idContribucion);
            throw new DatosInvalidosException("El tipo de media es obligatorio");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() ->{
                    log.warn("Contribución no encontrada para ID={}", idContribucion);
                    return  new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        Archivo archivo = archivoMapper.archivoDtoToArchivo(dto);
        contribucion.getHecho().agregarAdjunto(archivo);
        repositorio.save(contribucion);
        log.info("Archivo adjuntado correctamente a contribución ID={}", idContribucion);
    }

    public ContribucionOutputDTO obtener(long id){

        log.info("Obteniendo contribución con ID={}", id);
        Contribucion c = repositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada para ID={}", id);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + id)
                });

        ContribucionOutputDTO dto = contribucionMapper.contribucionToOutputDTO(c);
        return dto;
    }

    private void validarHecho(HechoInputDTO hecho) {

        log.info("Validando datos del hecho: {}", hecho);
        if (hecho.getTitulo() == null || hecho.getTitulo().trim().isEmpty()) {
                log.warn("Título del hecho vacío");
            throw new DatosInvalidosException("El título del hecho es obligatorio");
        }
        if (hecho.getTitulo().length() > 200) {
            log.warn("Título del hecho excede 200 caracteres: {}", hecho.getTitulo());
            throw new DatosInvalidosException("El título no puede exceder 200 caracteres");
        }
        if (hecho.getDescripcion() == null || hecho.getDescripcion().trim().isEmpty()) {
            log.warn("Descripción del hecho vacía");
            throw new DatosInvalidosException("La descripción del hecho es obligatoria");
        }
        if (hecho.getFecha() == null) {
        log.warn("Fecha del hecho nula");
            throw new DatosInvalidosException("La fecha del hecho es obligatoria");
        }
        if (hecho.getFecha().isAfter(LocalDate.now())) {
            log.warn("Fecha del hecho futura: {}", hecho.getFecha());
            throw new DatosInvalidosException("La fecha del hecho no puede ser futura");
        }
        if (hecho.getUbicacion() == null) {
            log.warn("Ubicación del hecho nula");
            throw new DatosInvalidosException("La ubicación es obligatoria");
        }

        validarUbicacion(hecho.getUbicacion());

        log.info("Ubicación del hecho validada correctamente");
        if (hecho.getCategoria() == null || hecho.getCategoria().trim().isEmpty()) {
            log.warn("Categoría del hecho vacía");
            throw new DatosInvalidosException("La categoría es obligatoria");
        }
    }

    private void validarUbicacion(UbicacionInputDTO ubicacion) {
        log.info("Validando datos de ubicación: {}", ubicacion);
        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            throw new DatosInvalidosException("La latitud y longitud son obligatorias");
        }
        if (ubicacion.getLatitud() < -90 || ubicacion.getLatitud() > 90) {
            log
            throw new DatosInvalidosException("La latitud debe estar entre -90 y 90");
        }
        if (ubicacion.getLongitud() < -180 || ubicacion.getLongitud() > 180) {
            throw new DatosInvalidosException("La longitud debe estar entre -180 y 180");
        }

    }
}
