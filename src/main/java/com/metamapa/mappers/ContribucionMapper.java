package com.metamapa.mappers;

import com.metamapa.domain.Contribucion;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import org.springframework.stereotype.Component;

@Component
public class ContribucionMapper {
    HechoMapper hechoMapper = new HechoMapper();

    public ContribucionOutputDTO contribucionToOutputDTO(Contribucion c){
        ContribucionOutputDTO dto = new ContribucionOutputDTO();
        HechoOutputDTO hechoDto = hechoMapper.hechoToHechoOutputDTO(c.getHecho());
        dto.setHecho(hechoDto);
        dto.setAnonimo(c.getAnonimo());
        dto.setIdContribucion(c.getId());
        dto.setNombreContribuyente(c.getContribuyente().getNombre() + " " + c.getContribuyente().getApellido());
        dto.setIdContribuyente(c.getContribuyente().getId());

        return dto;
    }
}
