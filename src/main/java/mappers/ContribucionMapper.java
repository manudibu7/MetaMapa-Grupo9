package mappers;

import domain.Contribucion;
import dtos.output.ContribucionOutputDTO;
import dtos.output.HechoOutputDTO;
import org.springframework.stereotype.Component;

@Component
public class ContribucionMapper {
    HechoMapper hechoMapper = new HechoMapper();

    public ContribucionOutputDTO contribucionToOutputDTO(Contribucion c){
        ContribucionOutputDTO dto = new ContribucionOutputDTO();
        HechoOutputDTO hechoDto = hechoMapper.hechoToHechoOutputDTO(c.getHecho());
        dto.setHecho(hechoDto);
        dto.setIdContribucion(c.getId());
        dto.setIdContribuyente(c.getContribuyente().getId());

        return dto;
    }
}
