package Infrastructure.Outbox;

import domain.Contribucion;
import domain.Hecho;
import domain.Ubicacion;
import dtos.output.AdjuntoOutputDTO;
import dtos.output.HechoOutputDTO;
import dtos.output.UbicacionOutputDTO;
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
        dto.setEtiqueta(hecho.getEtiqueta().getNombre());
        dto.setAdjunto(adjuntoDTO);

        return dto;
    }
}
