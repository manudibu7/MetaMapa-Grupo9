package com.metamapa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.services.ServicioContribuyente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControladorContribuyentesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioContribuyente servicioContribuyente;

    @Test
    void testAgregarContribuyente_DeberiaRetornar201YElId() throws Exception {
        // preparo los datos de entrada
        ContribuyenteInputDTO inputDTO = new ContribuyenteInputDTO(
            "Juan",
            "Pérez",
            30,
            false
        );
        Long idEsperado = 1L;
        
        // mockeo el servicio para que retorne el id
        when(servicioContribuyente.registrarContribuyente(any(ContribuyenteInputDTO.class)))
            .thenReturn(idEsperado);

        // mockeo buscarContribuyente para que retorne el contribuyente completo
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(idEsperado);
        contribuyente.setNombre("Juan");
        contribuyente.setApellido("Pérez");
        contribuyente.setEdad(30);
        contribuyente.setAnonimo(false);

        when(servicioContribuyente.buscarContribuyente(idEsperado))
            .thenReturn(contribuyente);

        // ejecuto y verifico
        mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idEsperado))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.edad").value(30))
                .andExpect(jsonPath("$.anonimo").value(false));
    }

    @Test
    void testAgregarContribuyenteAnonimo_DeberiaRetornar201() throws Exception {
        // preparo un contribuyente anonimo
        ContribuyenteInputDTO inputDTO = new ContribuyenteInputDTO(
            null,
            null,
            null,
            true
        );
        Long idEsperado = 2L;
        
        // mockeo el servicio para que retorne el id
        when(servicioContribuyente.registrarContribuyente(any(ContribuyenteInputDTO.class)))
            .thenReturn(idEsperado);

        // mockeo buscarContribuyente para que retorne el contribuyente anonimo completo
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(idEsperado);
        contribuyente.setNombre(null);
        contribuyente.setApellido(null);
        contribuyente.setEdad(null);
        contribuyente.setAnonimo(true);

        when(servicioContribuyente.buscarContribuyente(idEsperado))
            .thenReturn(contribuyente);

        // ejecuto y verifico
        mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idEsperado))
                .andExpect(jsonPath("$.anonimo").value(true));
    }

    @Test
    void testObtenerContribuyente_DeberiaRetornar200YElContribuyente() throws Exception {
        // preparo un contribuyente de respuesta
        Long id = 1L;
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(id);
        contribuyente.setNombre("Juan");
        contribuyente.setApellido("Pérez");
        contribuyente.setEdad(30);
        contribuyente.setAnonimo(false);
        
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(contribuyente);

        // ejecuto y verifico
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
        // simulo que no existe el contribuyente
        Long id = 999L;
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(null);

        // ejecuto y verifico que retorne 404
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerContribuyenteAnonimo_DeberiaRetornar200ConDatosAnonimos() throws Exception {
        // preparo un contribuyente anonimo
        Long id = 3L;
        var contribuyente = new com.metamapa.domain.Contribuyente();
        contribuyente.setId(id);
        contribuyente.setNombre(null);  // anonimo no tiene nombre
        contribuyente.setApellido(null);  // anonimo no tiene apellido
        contribuyente.setEdad(null);  // anonimo no tiene edad
        contribuyente.setAnonimo(true);
        
        when(servicioContribuyente.buscarContribuyente(id))
            .thenReturn(contribuyente);

        // ejecuto y verifico
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.anonimo").value(true))
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.apellido").doesNotExist())
                .andExpect(jsonPath("$.edad").doesNotExist());
    }
}
