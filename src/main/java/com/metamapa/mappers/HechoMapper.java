package com.metamapa.mappers;

import com.metamapa.domain.Categoria;
import com.metamapa.domain.Hecho;
import com.metamapa.dtos.input.HechoInputDTO;
import com.metamapa.dtos.output.AdjuntoOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.UbicacionOutputDTO;
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
        dto.setCategoria(hecho.getCategoria().getNombre());
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
        hecho.setCategoria(new Categoria(dto.getCategoria()));
        hecho.setLugarDeOcurrencia(ubicacionMapper.ubicacionDtoToUbicacion(dto.getUbicacion()));
        return hecho;
    }
}
