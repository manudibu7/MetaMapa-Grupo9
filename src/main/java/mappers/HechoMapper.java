package mappers;

import domain.Etiqueta;
import domain.Hecho;
import dtos.input.HechoInputDTO;
import dtos.output.AdjuntoOutputDTO;
import dtos.output.HechoOutputDTO;
import dtos.output.UbicacionOutputDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HechoMapper {
    private UbicacionMapper ubicacionMapper = new UbicacionMapper();
    private ArchivoMapper archivoMapper = new ArchivoMapper();

    public HechoOutputDTO hechoToHechoOutputDTO(Hecho hecho){
        HechoOutputDTO dto = new HechoOutputDTO();
        dto.setTitulo(hecho.getTitulo());
        dto.setDescripcion(hecho.getDescripcion());
        dto.setFecha(LocalDate.parse(hecho.getFecha().toString()));
        dto.setEtiqueta(hecho.getEtiqueta().getNombre());
        if (hecho.getAdjunto() != null){
            AdjuntoOutputDTO adjuntoDto = new AdjuntoOutputDTO();
            adjuntoDto.setUrl(hecho.getAdjunto().getUrl());
            adjuntoDto.setTipo(hecho.getAdjunto().getTipo().toString());
            dto.setAdjunto(adjuntoDto);
        }
        UbicacionOutputDTO ubicacionDto= new UbicacionOutputDTO();
        ubicacionDto.setLatitud(hecho.getLugarDeOcurrencia().getLatitud());
        ubicacionDto.setLongitud(hecho.getLugarDeOcurrencia().getLongitud());
        dto.setUbicacion(ubicacionDto);
        return dto;
    }
    public Hecho hechoDtoToHecho(HechoInputDTO dto){
        Hecho hecho = new Hecho();
        hecho.setTitulo(dto.getTitulo());
        hecho.setDescripcion(dto.getDescripcion());
        hecho.setFecha(dto.getFecha());
        hecho.setEtiqueta(new Etiqueta(dto.getEtiqueta()));
        hecho.setLugarDeOcurrencia(ubicacionMapper.ubicacionDtoToUbicacion(dto.getUbicacion()));
        return hecho;
    }
}
