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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.metamapa.repository.IContribucionesRepository;

import java.time.LocalDate;
import java.util.List;

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


    @Transactional
    public void aceptar(Long idContribucion, String comentarios) {
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }

        Contribucion contribucion = repo.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Revision revision = contribucion.getRevision();

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            throw new DatosInvalidosException("Solo se pueden aceptar revisiones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        revision.setEstado(EstadoRevision.ACEPTADA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(contribucion);

        outbox.agregar(preparador.preparar(contribucion));  // Cambio de save() a agregar()
    }

    public List<ContribucionOutputDTO> listarPendientes(){
        List<Contribucion> contribuciones = repo.findAll();
        return contribuciones.stream()
                .filter(c -> c.getRevision().getEstado() == EstadoRevision.PENDIENTE)
                .map(contribucionMapper::contribucionToOutputDTO)
                .toList();
    }


    @Transactional
    public void aceptarConSugerencias(Long idContribucion, String comentarios) {
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }
        if (comentarios == null || comentarios.trim().isEmpty()) {
            throw new DatosInvalidosException("Los comentarios son obligatorios al aceptar con sugerencias");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Revision revision = c.getRevision();

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            throw new DatosInvalidosException("Solo se pueden revisar contribuciones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        revision.setEstado(EstadoRevision.ACEPTADA_CON_SUGERENCIA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(c);
    }

    @Transactional
    public void rechazar(Long idContribucion, String comentarios) {
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }
        if (comentarios == null || comentarios.trim().isEmpty()) {
            throw new DatosInvalidosException("El motivo del rechazo es obligatorio");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Revision revision = c.getRevision();

        // Validar que esté en estado pendiente
        if (revision.getEstado() != EstadoRevision.PENDIENTE) {
            throw new DatosInvalidosException("Solo se pueden rechazar contribuciones en estado PENDIENTE. Estado actual: " + revision.getEstado());
        }

        revision.setEstado(EstadoRevision.RECHAZADA);
        revision.setMensaje(comentarios);
        revision.setFecha(LocalDate.now());
        repo.save(c);
    }

    public RevisionOutputDTO detalle(Long idContribucion) {
        // Validaciones
        if (idContribucion == null || idContribucion <= 0) {
            throw new DatosInvalidosException("El ID de contribución debe ser un número positivo");
        }

        Contribucion c = repo.findById(idContribucion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contribución no encontrada con ID: " + idContribucion));

        Revision revision = c.getRevision();
        return revisionMapper.revisionToRevisionOutputDTO(revision);
    }
}
