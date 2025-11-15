package com.metamapa.infrastructure.Outbox;

import com.metamapa.domain.Contribucion;
import com.metamapa.domain.Hecho;
import com.metamapa.domain.Ubicacion;
import com.metamapa.dtos.output.AdjuntoOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.UbicacionOutputDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PreparadorParaAgregador {

    public HechoOutputDTO preparar (Contribucion c){
        Hecho hecho = c.getHecho();
        Ubicacion u = hecho.getLugarDeOcurrencia();

        UbicacionOutputDTO ubicacionDTO = new UbicacionOutputDTO();
        ubicacionDTO.setLatitud(u.getLatitud());
        ubicacionDTO.setLongitud(u.getLongitud());

        List<AdjuntoOutputDTO> adjuntosDTO = null;
        if(hecho.getAdjuntos() != null && !hecho.getAdjuntos().isEmpty()) {
            adjuntosDTO = hecho.getAdjuntos().stream()
                .map(archivo -> {
                    AdjuntoOutputDTO adjuntoDTO = new AdjuntoOutputDTO();
                    adjuntoDTO.setId(archivo.getId());
                    adjuntoDTO.setTipo(archivo.getTipo().toString());
                    adjuntoDTO.setUrl(archivo.getUrl());
                    return adjuntoDTO;
                })
                .collect(Collectors.toList());
        }

        HechoOutputDTO dto = new HechoOutputDTO();
        dto.setTitulo(hecho.getTitulo());
        dto.setDescripcion(hecho.getDescripcion());
        dto.setFecha(hecho.getFecha());
        dto.setUbicacion(ubicacionDTO);
        dto.setCategoria(hecho.getCategoria().getNombre());
        dto.setAdjuntos(adjuntosDTO);
        dto.setTipoDeHecho(hecho.getTipoDeHecho().toString());

        return dto;
    }
}
