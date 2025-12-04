package com.metamapa.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para la sincronización de contribuyentes con Keycloak.
 * Contiene el ID interno del sistema junto con los datos del contribuyente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteSistemaResponse {

    /**
     * ID interno del sistema (ID de la entidad Contribuyente).
     */
    private Long idSistema;

    /**
     * ID externo proveniente de Keycloak.
     */
    private String keycloakId;

    /**
     * Nombre del contribuyente.
     */
    private String nombre;

    /**
     * Apellido del contribuyente.
     */
    private String apellido;
}

