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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final Path rootLocation = Paths.get("uploads");
    /**
     * Obtiene todas las contribuciones de un contribuyente por su ID interno.
     * @param contribuyenteId ID interno del contribuyente
     * @return Lista de ContribucionOutputDTO del contribuyente
     * @throws DatosInvalidosException si el ID es null o inválido
     * @throws RecursoNoEncontradoException si el contribuyente no existe
     */
    public List<ContribucionOutputDTO> obtenerContribucionesPorContribuyente(Long contribuyenteId) {
        if (contribuyenteId == null || contribuyenteId <= 0) {
            throw new DatosInvalidosException("El ID del contribuyente debe ser un número positivo");
        }

        // Verificar que el contribuyente existe
        servicioContribuyente.buscarContribuyente(contribuyenteId);

        List<Contribucion> contribuciones = repositorio.findByContribuyenteId(contribuyenteId);

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
            throw new DatosInvalidosException("El keycloakId es obligatorio y no puede estar vacío");
        }

        List<Contribucion> contribuciones = repositorio.findByContribuyenteKeycloakId(keycloakId);

        // Si no hay contribuciones, verificamos si el contribuyente existe
        if (contribuciones.isEmpty()) {
            // Esto lanzará RecursoNoEncontradoException si el contribuyente no existe
            var contribuyente = servicioContribuyente.buscarContribuyentePorKeycloakId(keycloakId);
            if (contribuyente == null) {
                throw new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakId);
            }
        }

        return contribuciones.stream()
                .map(contribucionMapper::contribucionToOutputDTO)
                .collect(Collectors.toList());
    }

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

    public void adjuntarArchivoBinario(long idContribucion, MultipartFile file, String tipoStr) {
        // 1. Validaciones básicas
        if (file.isEmpty()) {
            throw new DatosInvalidosException("El archivo no puede estar vacío");
        }

        Contribucion contribucion = repositorio.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        try {
            // 2. Guardar el archivo físicamente (SIMULACIÓN LOCAL)
            // En producción, aquí llamarías a S3 o Cloudinary
            Files.createDirectories(rootLocation);
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));

            // Generar la URL (En local sería algo así, en la nube te la da el proveedor)
            String urlGenerada = "/uploads/" + filename;

            // 3. Crear la entidad Archivo
            Archivo archivo = new Archivo();
            archivo.setUrl(urlGenerada);
            archivo.setTamanio(String.valueOf(file.getSize())); // Guardamos el tamaño en bytes

            // Convertir el String "IMAGEN", "VIDEO" al Enum
            try {
                archivo.setTipo(TipoMedia.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                archivo.setTipo(TipoMedia.TEXTO); // O lanzar excepción
            }

            // 4. Vincular con el Hecho
            contribucion.getHecho().agregarAdjunto(archivo);
            repositorio.save(contribucion);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public void adjuntarArchivo(long idContribucion, ArchivoInputDTO dto){
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
        contribucion.getHecho().agregarAdjunto(archivo);
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
