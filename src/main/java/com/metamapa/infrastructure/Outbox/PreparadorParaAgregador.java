package com.metamapa.infrastructure.Outbox;

import com.metamapa.domain.Contribucion;
import com.metamapa.domain.Hecho;
import com.metamapa.domain.Ubicacion;
import com.metamapa.dtos.output.AdjuntoOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.UbicacionOutputDTO;
import org.springframework.stereotype.Component;

@Component
public class PreparadorParaAgregador {

    public HechoOutputDTO preparar (Contribucion c){
        Hecho hecho = c.getHecho();
        Ubicacion u = hecho.getLugarDeOcurrencia();

        UbicacionOutputDTO ubicacionDTO = new UbicacionOutputDTO();
        ubicacionDTO.setLatitud(u.getLatitud());
        ubicacionDTO.setLongitud(u.getLongitud());

        AdjuntoOutputDTO adjuntoDTO = null;
        if(hecho.getAdjunto() != null) {
            adjuntoDTO = new AdjuntoOutputDTO();
            adjuntoDTO.setId(hecho.getAdjunto().getId());
            adjuntoDTO.setTipo(hecho.getAdjunto().getTipo().toString());
            adjuntoDTO.setUrl(hecho.getAdjunto().getUrl());
        }
        HechoOutputDTO dto = new HechoOutputDTO();
        dto.setTitulo(hecho.getTitulo());
        dto.setDescripcion(hecho.getDescripcion());
        dto.setFecha(hecho.getFecha());
        dto.setUbicacion(ubicacionDTO);
        dto.setCategoria(hecho.getCategoria().getNombre());
        dto.setAdjunto(adjuntoDTO);

        return dto;
    }
}
