package com.metamapa.mappers;

import com.metamapa.domain.Ubicacion;
import com.metamapa.dtos.input.UbicacionInputDTO;

public class UbicacionMapper {
    public Ubicacion ubicacionDtoToUbicacion(UbicacionInputDTO dto){
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(dto.getLatitud());
        ubicacion.setLongitud(dto.getLongitud());
        return ubicacion;
    }
}
