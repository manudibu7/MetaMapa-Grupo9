package com.metamapa.mappers;

import com.metamapa.domain.Archivo;
import com.metamapa.dtos.input.ArchivoInputDTO;
import org.springframework.stereotype.Component;

@Component
public class ArchivoMapper {
    public Archivo archivoDtoToArchivo(ArchivoInputDTO dto){
        Archivo archivo = new Archivo();
        archivo.setTipoFromString(dto.getTipo());
        archivo.setUrl(dto.getUrl());
        return archivo;
    }
}
