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

        log.info("Obteniendo contribuciones para contribuyente con ID: {}", contribuyenteId);
        if (contribuyenteId == null || contribuyenteId <= 0) {
            log.warn("ID de contribuyente inválido: {}", contribuyenteId);
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }

        // Verificar que el contribuyente existe
        servicioContribuyente.buscarContribuyente(contribuyenteId);

        List<Contribucion> contribuciones = repositorio.findByContribuyenteId(contribuyenteId);
        log.debug("Contribuciones encontradas para contribuyente ID {}: {}", contribuyenteId, contribuciones.size());
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
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("Intento de obtener contribuciones con keycloakId inválido: '{}'", keycloakId);
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        List<Contribucion> contribuciones = repositorio.findByContribuyenteKeycloakId(keycloakId);

        // Si no hay contribuciones, verificamos si el contribuyente existe
        if (contribuciones.isEmpty()) {
            // Esto lanzará RecursoNoEncontradoException si el contribuyente no existe
            var contribuyente = servicioContribuyente.buscarContribuyentePorKeycloakId(keycloakId);
            if (contribuyente == null) {
                log.warn("Contribuyente no encontrado con keycloakId: '{}'", keycloakId);
                throw new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakId);
            }
        }

        return contribuciones.stream()
                .map(contribucionMapper::contribucionToOutputDTO)
                .collect(Collectors.toList());
    }

    public Long crear(ContribucionInputDTO contribucionInputDTO){

        log.info("Creando contribución para contribuyente ID: {}", contribucionInputDTO.getIdContribuyente());
        // Validaciones
        if (contribucionInputDTO == null) {
            log.warn("Intento de crear contribución con datos nulos");
            throw new DatosInvalidosException("Los datos de la contribución no pueden ser nulos");
        }
        if (contribucionInputDTO.getIdContribuyente() == null) {
            log.warn("Intento de crear contribución sin ID de contribuyente");
            throw new DatosInvalidosException("El ID del contribuyente es obligatorio");
        }
        if (contribucionInputDTO.getHecho() == null) {
            log.warn("Intento de crear contribución sin hecho");
            throw new DatosInvalidosException("El hecho es obligatorio");
        }

        validarHecho(contribucionInputDTO.getHecho());

        log.info("Datos de hecho validados correctamente para contribuyente ID: {}", contribucionInputDTO.getIdContribuyente());

        Hecho hecho = hechoMapper.hechoDtoToHecho(contribucionInputDTO.getHecho());
        Contribucion contribucion = new Contribucion();
        contribucion.setHecho(hecho);
        contribucion.getRevision().setContribucion(contribucion);
        contribucion.setFechaDeCarga(LocalDate.now());

        Contribuyente contribuyente = servicioContribuyente.buscarContribuyente(contribucionInputDTO.getIdContribuyente());
        if(contribuyente == null) {
            log.warn("Contribuyente no encontrado con ID: {}", contribucionInputDTO.getIdContribuyente());
            throw new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + contribucionInputDTO.getIdContribuyente());
        }
        log.debug("Contribuyente encontrado: (ID: {})", contribuyente.getId());

        contribucion.setContribuyente(contribuyente);
        contribucion.setAnonimo(contribucionInputDTO.getAnonimo());
        repositorio.save(contribucion);
        log.debug("Contribución creada con ID: {} para contribuyente ID: {}", contribucion.getId(), contribucionInputDTO.getIdContribuyente());
        return contribucion.getId();
    }

    public void editar(long idContribucion, HechoInputDTO dto){

        log.info("Editando contribución con ID: {}", idContribucion);
        // Validaciones


        if (dto == null) {
            log.warn("Intento de editar contribución con datos de hecho nulos para contribución ID: {}", idContribucion);
            throw new DatosInvalidosException("Los datos del hecho no pueden ser nulos");
        }
        validarHecho(dto);
        log.debug("Datos de hecho validados correctamente para contribución ID: {}", idContribucion);

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);

                });

        if (contribucion.getFechaDeCarga() == null) {
            log.warn("Contribución con ID: {} no tiene fecha de carga, asignando fecha actual", idContribucion);
            contribucion.setFechaDeCarga(LocalDate.now());
        }

        if (!politicaEdicion.puedeEditar(contribucion.getFechaDeCarga())){
            log.warn("Intento de editar contribución con ID: {} después de 7 días de su creación", idContribucion);
            throw new EdicionNoPermitidaException("No se puede editar la contribución después de 7 días");
        }

        Hecho hecho = hechoMapper.hechoDtoToHecho(dto);
        contribucion.setHecho(hecho);
        contribucion.getRevision().setEstado(EstadoRevision.PENDIENTE);
        repositorio.save(contribucion);
        log.debug("Contribución con ID: {} editada exitosamente", idContribucion);
    }

    public void adjuntarArchivoBinario(long idContribucion, MultipartFile file, String tipoStr) {
        // 1. Validaciones básicas

        log.info("Adjuntando archivo a contribución con ID: {}", idContribucion);
        if (file.isEmpty()) {
            log.warn("Intento de adjuntar archivo vacío a contribución con ID: {}", idContribucion);
            throw new DatosInvalidosException("El archivo no puede estar vacío");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                     return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        try {
            // 2. Guardar el archivo físicamente (SIMULACIÓN LOCAL)
            // En producción, aquí llamarías a S3 o Cloudinary

            // Generar la URL (En local sería algo así, en la nube te la da el proveedor)
            //String urlGenerada = "/uploads/" + filename;
            log.debug("Subiendo archivo a Cloudinary para contribución ID: {}", idContribucion);
            // 3. Crear la entidad Archivo
            String urlArchivo = cloudinaryService.subirArchivo(file);

            // 3. Crear la entidad Archivo
            Archivo archivo = new Archivo();
            archivo.setUrl(urlArchivo);
            archivo.setTamanio(String.valueOf(file.getSize()));

            // Convertir el String "IMAGEN", "VIDEO" al Enum
            try {
                archivo.setTipo(TipoMedia.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                    log.warn("Tipo de archivo no válido: {}. Asignando tipo por defecto TEXTO para contribución ID: {}", tipoStr, idContribucion);
                archivo.setTipo(TipoMedia.TEXTO); // O lanzar excepción
            }

            // 4. Vincular con el Hecho
            contribucion.getHecho().agregarAdjunto(archivo);
            repositorio.save(contribucion);
            log.info("Archivo adjuntado exitosamente a contribución con ID: {}", idContribucion);

        } catch (IllegalArgumentException e) {
            log.warn("Tipo de archivo no válido: {} para contribución ID: {}", tipoStr, idContribucion);
            throw new DatosInvalidosException("Tipo de archivo no válido: " + tipoStr);        }
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
        if (dto == null) {
            log.warn("Intento de adjuntar archivo con datos nulos a contribución con ID: {}", idContribucion);
            throw new DatosInvalidosException("Los datos del archivo no pueden ser nulos");
        }
        if (dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            log.warn("Intento de adjuntar archivo sin URL a contribución con ID: {}", idContribucion);
            throw new DatosInvalidosException("La URL del archivo es obligatoria");
        }
        if (dto.getTipo() == null) {
            log.warn("Intento de adjuntar archivo sin tipo a contribución con ID: {}", idContribucion);
            throw new DatosInvalidosException("El tipo de media es obligatorio");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Archivo archivo = archivoMapper.archivoDtoToArchivo(dto);
        contribucion.getHecho().agregarAdjunto(archivo);
        repositorio.save(contribucion);
    }

    public ContribucionOutputDTO obtener(long id){

        log.info("Obteniendo contribución con ID: {}", id);
        Contribucion c = repositorio.findById(id)
                .orElseThrow(() ->{
                    log.warn("Contribución no encontrada con ID: {}", id);
                     return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + id);
                });

        ContribucionOutputDTO dto = contribucionMapper.contribucionToOutputDTO(c);
        log.info("Contribución con ID: {} obtenida exitosamente", id);
        return dto;
    }

    private void validarHecho(HechoInputDTO hecho) {
        if (hecho.getTitulo() == null || hecho.getTitulo().trim().isEmpty()) {
                log.warn("Intento de validar hecho con título vacío");
            throw new DatosInvalidosException("El título del hecho es obligatorio");
        }
        if (hecho.getTitulo().length() > 200) {
                log.warn("Intento de validar hecho con título que excede 200 caracteres: {}", hecho.getTitulo());
            throw new DatosInvalidosException("El título no puede exceder 200 caracteres");
        }
        if (hecho.getDescripcion() == null || hecho.getDescripcion().trim().isEmpty()) {
                log.warn("Intento de validar hecho con descripción vacía");
            throw new DatosInvalidosException("La descripción del hecho es obligatoria");
        }
        if (hecho.getFecha() == null) {
                log.warn("Intento de validar hecho sin fecha");
            throw new DatosInvalidosException("La fecha del hecho es obligatoria");
        }
        if (hecho.getFecha().isAfter(LocalDate.now())) {
                log.warn("Intento de validar hecho con fecha futura: {}", hecho.getFecha());
            throw new DatosInvalidosException("La fecha del hecho no puede ser futura");
        }
        if (hecho.getUbicacion() == null) {
                log.warn("Intento de validar hecho sin ubicación");
            throw new DatosInvalidosException("La ubicación es obligatoria");
        }

        validarUbicacion(hecho.getUbicacion());
        log.debug("Ubicación del hecho validada correctamente");
        if (hecho.getCategoria() == null || hecho.getCategoria().trim().isEmpty()) {
                log.warn("Intento de validar hecho sin categoría");
            throw new DatosInvalidosException("La categoría es obligatoria");
        }
        log.info("Hecho validado correctamente con título: '{}'", hecho.getTitulo());
    }

    private void validarUbicacion(UbicacionInputDTO ubicacion) {
        log.info("Validando ubicación con latitud: {} y longitud: {}", ubicacion.getLatitud(), ubicacion.getLongitud());
        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            log.warn("Intento de validar ubicación con latitud o longitud nulas");
            throw new DatosInvalidosException("La latitud y longitud son obligatorias");
        }
        if (ubicacion.getLatitud() < -90 || ubicacion.getLatitud() > 90) {
            log.warn("Intento de validar ubicación con latitud fuera de rango: {}", ubicacion.getLatitud());
            throw new DatosInvalidosException("La latitud debe estar entre -90 y 90");
        }
        if (ubicacion.getLongitud() < -180 || ubicacion.getLongitud() > 180) {
            log.warn("Intento de validar ubicación con longitud fuera de rango: {}", ubicacion.getLongitud());
            throw new DatosInvalidosException("La longitud debe estar entre -180 y 180");
        }
    }
}
