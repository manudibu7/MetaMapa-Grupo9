package com.metamapa.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoInputDTO {
    Long idArchivo;
    String tipo; //serai tipoMedia
    String url;
}
