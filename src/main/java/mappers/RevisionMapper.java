package mappers;

import domain.Revision;
import dtos.output.RevisionOutputDTO;
import org.springframework.stereotype.Component;

@Component
public class RevisionMapper {
    public RevisionOutputDTO revisionToRevisionOutputDTO(Revision revision){
        RevisionOutputDTO dto = new RevisionOutputDTO();
        dto.setIdContribucion(revision.getContribucion().getId());
        dto.setEstado(revision.getEstado().toString());
        dto.setMensaje(revision.getMensaje());
        return dto;
    }

}
