// services/ServicioDeRevision.java
package services;

import Infrastructure.Outbox.BandejaDeSalida;
import Infrastructure.Outbox.PreparadorParaAgregador;
import domain.*;
import dtos.output.ContribucionOutputDTO;
import dtos.output.HechoOutputDTO;
import dtos.output.RevisionOutputDTO;
import lombok.RequiredArgsConstructor;
import mappers.ContribucionMapper;
import mappers.RevisionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.IContribucionesRepository;

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
        Contribucion contribucion = repo.buscarPorId(idContribucion);
        Revision revision = contribucion.getRevision();
        revision.setEstado(EstadoRevision.ACEPTADA);
        revision.setMensaje(comentarios);

        outbox.agregar(preparador.preparar(contribucion));
    }

    public List<ContribucionOutputDTO> listarPendientes(){
        List<Contribucion> contribuciones = repo.buscarTodas();
        return contribuciones.stream()
                .filter(c -> c.getRevision().getEstado() == EstadoRevision.PENDIENTE)
                .map(contribucionMapper::contribucionToOutputDTO)
                .toList();
    }


    @Transactional
    public void aceptarConSugerencias(Long idContribucion, String comentarios) {
        Contribucion c = repo.buscarPorId(idContribucion);
        Revision revision = c.getRevision();
        revision.setEstado(EstadoRevision.ACEPTADA_CON_SUGERENCIA);
        revision.setMensaje(comentarios);
    }

    @Transactional
    public void rechazar(Long idContribucion, String comentarios) {
        Contribucion c = repo.buscarPorId(idContribucion);
        Revision revision = c.getRevision();
        revision.setEstado(EstadoRevision.RECHAZADA);
    }

    public RevisionOutputDTO detalle(Long idContribucion) {
        Contribucion c = repo.buscarPorId(idContribucion);
        Revision revision =  c.getRevision();
        return revisionMapper.revisionToRevisionOutputDTO(revision);
    }
}
