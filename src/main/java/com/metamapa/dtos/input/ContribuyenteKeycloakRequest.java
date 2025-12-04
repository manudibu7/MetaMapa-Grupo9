package com.metamapa.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para sincronizar un contribuyente con Keycloak.
 * Contiene el keycloakId (obligatorio) y opcionalmente nombre y apellido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteKeycloakRequest {

    /**
     * ID externo proveniente de Keycloak (obligatorio).
     */
    private String keycloakId;

    /**
     * Nombre del contribuyente (opcional, puede ser null o vacío).
     */
    private String nombre;

    /**
     * Apellido del contribuyente (opcional, puede ser null o vacío).
     */
    private String apellido;
}

