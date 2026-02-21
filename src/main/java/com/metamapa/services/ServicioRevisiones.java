// services/ServicioDeRevision.java
package com.metamapa.services;

import com.metamapa.infrastructure.Outbox.BandejaDeSalida;
import com.metamapa.infrastructure.Outbox.PreparadorParaAgregador;
import com.metamapa.domain.*;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.RevisionOutputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import com.metamapa.mappers.ContribucionMapper;
import com.metamapa.mappers.RevisionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.metamapa.repository.IContribucionesRepository;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor     // inyección por constructor (Spring)
public class ServicioRevisiones {
    @Autowired
    private final IContribucionesRepository repo;
    @Autowired
    private final PreparadorParaAgregador preparador;
    @Autowired
    private final BandejaDeSalida outbox;
    @Autowired
    private final RevisionMapper revisionMapper;
    @Autowired
    private final ContribucionMapper contribucionMapper;
    @Autowired
    private final com.metamapa.repository.IContribuyentesRepository contribuyentesRepository;


    @Transactional
    public void aceptar(Long idContribucion, String comentarios, Long idContribuyente) {
        log.info(" Iniciando proceso de aceptación para contribución ID={}", idContribucion);
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            log.warn("ID de contribución inválido: {}", idContribucion);
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }

        Contribucion contribucion = repo.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        Revision revision = contribucion.getRevision();
        log.debug("Revisión actual de la contribución ID={} tiene estado={}", idContribucion, revision.getEstado());

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            log.warn("No se puede aceptar contribución ID={} porque su revisión no está en estado PENDIENTE. Estado actual: {}", idContribucion, revision.getEstado());
            throw new DatosInvalidosException("Solo se pueden aceptar revisiones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        // Asignar responsable si se proporcionó id
        if (idContribuyente != null) {;
            com.metamapa.domain.Contribuyente responsable = contribuyentesRepository.findById(idContribuyente)
                    .orElseThrow(() ->{
                        log.warn("Contribuyente no encontrado con ID: {}", idContribuyente);
                        return new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + idContribuyente);
                    } );
            revision.setResponsable(responsable);
        }

        revision.setEstado(EstadoRevision.ACEPTADA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(contribucion);
        log.info("Contribución ID={} aceptada exitosamente. Enviando a outbox para exportación.", idContribucion);
        outbox.agregar(preparador.preparar(contribucion));  // Cambio de save() a agregar()

    }

    public List<ContribucionOutputDTO> listarPendientes(){
        log.info("Obteniendo lista de contribuciones pendientes para revisión");
        List<Contribucion> contribuciones = repo.findAll();
        log.debug("Total de contribuciones encontradas: {}", contribuciones.size());
        return contribuciones.stream()
                .filter(c -> c.getRevision().getEstado() == EstadoRevision.PENDIENTE)
                .map(contribucionMapper::contribucionToOutputDTO)
                .toList();
    }


    @Transactional
    public void aceptarConSugerencias(Long idContribucion, String comentarios, Long idContribuyente) {
        // Validaciones
        log.info(" Iniciando proceso de aceptación con sugerencias para contribución ID={}", idContribucion);
        if (idContribucion == null || idContribucion <= 0) {
            log.warn("ID de contribución inválido: {}", idContribucion);
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }
        if (comentarios == null || comentarios.trim().isEmpty()) {
            log.warn("Comentarios inválidos para aceptación con sugerencias en contribución ID={}: {}", idContribucion, comentarios);
            throw new DatosInvalidosException("Los comentarios son obligatorios al aceptar con sugerencias");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        Revision revision = c.getRevision();

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            log.warn("No se puede aceptar con sugerencias contribución ID={} porque su revisión no está en estado PENDIENTE. Estado actual: {}", idContribucion, revision.getEstado());
            throw new DatosInvalidosException("Solo se pueden revisar contribuciones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        // Asignar responsable si se proporcionó id
        if (idContribuyente != null) {
            log.debug("Asignando responsable con ID={} a revisión de contribución ID={}", idContribuyente, idContribucion);
            com.metamapa.domain.Contribuyente responsable = contribuyentesRepository.findById(idContribuyente)
                    .orElseThrow(() -> {
                        log.warn("Contribuyente no encontrado con ID: {}", idContribuyente);
                        return new  RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + idContribuyente);
                    });
            revision.setResponsable(responsable);
        }

        revision.setEstado(EstadoRevision.ACEPTADA_CON_SUGERENCIA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(c);
        log.info("Contribución ID={} aceptada con sugerencias exitosamente.", idContribucion);
    }

    @Transactional
    public void rechazar(Long idContribucion, String comentarios, Long idContribuyente) {
        log.info(" Iniciando proceso de rechazo para contribución ID={}", idContribucion);
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            log.warn("ID de contribución inválido: {}", idContribucion);
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }
        if (comentarios == null || comentarios.trim().isEmpty()) {
            log.warn("Comentarios inválidos para rechazo en contribución ID={}: {}", idContribucion, comentarios);
            throw new DatosInvalidosException("El motivo del rechazo es obligatorio");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() -> {
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        Revision revision = c.getRevision();

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            log.warn("No se puede rechazar contribución ID={} porque su revisión no está en estado PENDIENTE. Estado actual: {}", idContribucion, revision.getEstado());
            throw new DatosInvalidosException("Solo se pueden rechazar contribuciones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        // Asignar responsable si se proporcionó id
        if (idContribuyente != null) {
            com.metamapa.domain.Contribuyente responsable = contribuyentesRepository.findById(idContribuyente)
                    .orElseThrow(() -> {
                        log.warn("Contribuyente no encontrado con ID: {}", idContribuyente);
                        return new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + idContribuyente);
                    });
            revision.setResponsable(responsable);
        }

        revision.setEstado(EstadoRevision.RECHAZADA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(c);
        log.info("Contribución ID={} rechazada exitosamente.", idContribucion);
    }

    public RevisionOutputDTO detalle(Long idContribucion) {
        log.info("Obteniendo detalle de revisión para contribución ID={}", idContribucion);
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            log.warn("ID de contribución inválido: {}", idContribucion);
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() ->{
                    log.warn("Contribución no encontrada con ID: {}", idContribucion);
                    return new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion);
                });

        Revision revision = c.getRevision();
        return revisionMapper.revisionToRevisionOutputDTO(revision);
    }
}
