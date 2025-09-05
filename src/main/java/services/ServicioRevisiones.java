// services/ServicioDeRevision.java
package services;

import Infrastructure.Outbox.BandejaDeSalida;
import Infrastructure.Outbox.PreparadorParaAgregador;
import domain.*;
import dtos.output.HechoOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.IContribucionesRepository;

@Service
@RequiredArgsConstructor     // inyección por constructor (Spring)
public class ServicioRevisiones {

    private final IContribucionesRepository repo;
    private final PreparadorParaAgregador preparador;
    private final BandejaDeSalida outbox;

    @Transactional
    public void aceptar(Long idContribucion, String comentarios) {
        Contribucion c = repo.buscarPorId(idContribucion);
        c.agregarRevision(new Revision(EstadoRevision.ACEPTADO, comentarios, null));
       // c.setEstado(EstadoContribucion.LISTA_PARA_EXPORTAR);
        repo.actualizar(c);

        HechoOutputDTO dto = preparador.preparar(c);
        outbox.agregar(dto); // ⚠️ guardamos el DTO para que lo pida el Agregador
    }

    @Transactional
    public void aceptarConSugerencias(Long idContribucion, String comentarios) {
        Contribucion c = repo.buscarPorId(idContribucion);
        c.agregarRevision(new Revision(EstadoRevision.ACEPTADO_CON_CAMBIOS, comentarios, null));
        //c.setEstado(EstadoContribucion.LISTA_PARA_EXPORTAR);
        repo.actualizar(c);

        HechoOutputDTO dto = preparador.preparar(c);
        outbox.agregar(dto);
    }

    @Transactional
    public void rechazar(Long idContribucion, String comentarios) {
        Contribucion c = repo.buscarPorId(idContribucion);
        c.agregarRevision(new Revision(EstadoRevision.RECHAZADO, comentarios, null));
        //c.setEstado(EstadoContribucion.PENDIENTE);
        repo.actualizar(c);
    }
}
