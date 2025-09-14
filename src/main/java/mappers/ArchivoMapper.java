package mappers;

import domain.Archivo;
import dtos.input.ArchivoInputDTO;
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
