package com.metamapa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.services.ServicioContribuyente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ControladorContribuyentesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioContribuyente servicioContribuyente;

    @Test
    void testAgregarContribuyente_DeberiaRetornar201YElId() throws Exception {
        // Arrange
        ContribuyenteInputDTO inputDTO = new ContribuyenteInputDTO(
            "Juan",
            "Pérez",
            30,
            false
        );
        Long idEsperado = 1L;
        
        when(servicioContribuyente.registrarContribuyente(any(ContribuyenteInputDTO.class)))
            .thenReturn(idEsperado);

        // Act & Assert
        mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(idEsperado.toString()));
    }

    @Test
    void testAgregarContribuyenteAnonimo_DeberiaRetornar201() throws Exception {
        // Arrange
        ContribuyenteInputDTO inputDTO = new ContribuyenteInputDTO(
            null,
            null,
            null,
            true
        );
        Long idEsperado = 2L;
        
        when(servicioContribuyente.registrarContribuyente(any(ContribuyenteInputDTO.class)))
            .thenReturn(idEsperado);

        // Act & Assert
        mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(idEsperado.toString()));
    }

    @Test
    void testObtenerContribuyente_DeberiaRetornar200YElContribuyente() throws Exception {
        // Arrange
        Long id = 1L;
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(id);
        contribuyente.setNombre("Juan");
        contribuyente.setApellido("Pérez");
        contribuyente.setEdad(30);
        contribuyente.setAnonimo(false);
        
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(contribuyente);

        // Act & Assert
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.edad").value(30))
                .andExpect(jsonPath("$.anonimo").value(false));
    }

    @Test
    void testObtenerContribuyenteInexistente_DeberiaRetornar404() throws Exception {
        // Arrange
        Long id = 999L;
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerContribuyenteAnonimo_DeberiaRetornar200ConDatosAnonimos() throws Exception {
        // Arrange
        Long id = 3L;
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(id);
        contribuyente.setNombre(null);  // Anónimo no tiene nombre
        contribuyente.setApellido(null);  // Anónimo no tiene apellido
        contribuyente.setEdad(null);  // Anónimo no tiene edad
        contribuyente.setAnonimo(true);
        
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(contribuyente);

        // Act & Assert
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.anonimo").value(true))
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.apellido").doesNotExist())
                .andExpect(jsonPath("$.edad").doesNotExist());
    }
}
