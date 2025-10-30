package com.metamapa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.metamapa.dtos.input.ArchivoInputDTO;
import com.metamapa.dtos.input.ContribucionInputDTO;
import com.metamapa.dtos.input.HechoInputDTO;
import com.metamapa.dtos.input.UbicacionInputDTO;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.UbicacionOutputDTO;
import com.metamapa.services.ServicioContribuciones;
import com.metamapa.services.ServicioContribuyente;
import com.metamapa.services.ServicioRevisiones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ControladorContribucionesTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioContribuciones servicioContribuciones;

    @MockitoBean
    private ServicioContribuyente servicioContribuyente;

    @MockitoBean
    private ServicioRevisiones servicioRevisiones;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCrearContribucion_DeberiaRetornar201YElId() throws Exception {
        // preparo los datos de entrada
        UbicacionInputDTO ubicacion = new UbicacionInputDTO(-34.6037f, -58.3816f);
        HechoInputDTO hecho = new HechoInputDTO(
            "Partido Histórico",
            "Final del mundial",
            LocalDate.of(2022, 12, 18),
            ubicacion,
            "Deportes"
        );
        ContribucionInputDTO inputDTO = new ContribucionInputDTO(1L, hecho);
        Long idEsperado = 10L;

        when(servicioContribuciones.crear(any(ContribucionInputDTO.class)))
            .thenReturn(idEsperado);

        // ejecuto y verifico
        mockMvc.perform(post("/contribuciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(idEsperado.toString()));

        verify(servicioContribuciones, times(1)).crear(any(ContribucionInputDTO.class));
    }

    @Test
    void testCrearContribucionSinUbicacion_DeberiaRetornar201() throws Exception {
        HechoInputDTO hecho = new HechoInputDTO(
            "Evento Sin Ubicación",
            "Descripción del evento",
            LocalDate.of(2023, 5, 10),
            null,
            "Cultura"
        );
        ContribucionInputDTO inputDTO = new ContribucionInputDTO(2L, hecho);
        Long idEsperado = 11L;

        when(servicioContribuciones.crear(any(ContribucionInputDTO.class)))
            .thenReturn(idEsperado);

        mockMvc.perform(post("/contribuciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(idEsperado.toString()));
    }

    @Test
    void testEditarContribucion_DeberiaRetornar200() throws Exception {
        Long id = 10L;
        UbicacionInputDTO ubicacion = new UbicacionInputDTO(-34.5f, -58.4f);
        HechoInputDTO hechoEditado = new HechoInputDTO(
            "Partido Editado",
            "Nueva descripción",
            LocalDate.of(2022, 12, 18),
            ubicacion,
            "Deportes"
        );
        ContribucionInputDTO inputDTO = new ContribucionInputDTO(1L, hechoEditado);

        doNothing().when(servicioContribuciones).editar(eq(id), any(HechoInputDTO.class));

        mockMvc.perform(put("/contribuciones/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk());

        verify(servicioContribuciones, times(1)).editar(eq(id), any(HechoInputDTO.class));
    }

    @Test
    void testAgregarArchivo_DeberiaRetornar200() throws Exception {
        Long id = 10L;
        ArchivoInputDTO archivo = new ArchivoInputDTO(
            1L,
            "image/jpeg",
            "http://example.com/imagen.jpg"
        );

        doNothing().when(servicioContribuciones).adjuntarArchivo(eq(id), any(ArchivoInputDTO.class));


        mockMvc.perform(patch("/contribuciones/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(archivo)))
                .andExpect(status().isOk());

        verify(servicioContribuciones, times(1)).adjuntarArchivo(eq(id), any(ArchivoInputDTO.class));
    }

    @Test
    void testVerContribucion_DeberiaRetornar200YLaContribucion() throws Exception {
        Long id = 10L;
        
        HechoOutputDTO hecho = new HechoOutputDTO();
        hecho.setTitulo("Partido Histórico");
        hecho.setDescripcion("Final del mundial");
        hecho.setFecha(LocalDate.of(2022, 12, 18));
        hecho.setCategoria("Deportes");
        
        UbicacionOutputDTO ubicacion = new UbicacionOutputDTO(-34.6037f, -58.3816f);
        hecho.setUbicacion(ubicacion);
        
        ContribucionOutputDTO outputDTO = new ContribucionOutputDTO(1L, hecho, id);

        when(servicioContribuciones.obtener(id))
            .thenReturn(outputDTO);


        mockMvc.perform(get("/contribuciones/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContribucion").value(id))
                .andExpect(jsonPath("$.hecho.titulo").value("Partido Histórico"))
                .andExpect(jsonPath("$.hecho.descripcion").value("Final del mundial"))
                .andExpect(jsonPath("$.hecho.categoria").value("Deportes"));

        verify(servicioContribuciones, times(1)).obtener(id);
    }

    @Test
    void testAgregarArchivoMultimedia_DeberiaRetornar200() throws Exception {

        Long id = 15L;
        ArchivoInputDTO archivoVideo = new ArchivoInputDTO(
            2L,
            "video/mp4",
            "http://example.com/video.mp4"
        );

        doNothing().when(servicioContribuciones).adjuntarArchivo(eq(id), any(ArchivoInputDTO.class));


        mockMvc.perform(patch("/contribuciones/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(archivoVideo)))
                .andExpect(status().isOk());

        verify(servicioContribuciones, times(1)).adjuntarArchivo(eq(id), any(ArchivoInputDTO.class));
    }
}
