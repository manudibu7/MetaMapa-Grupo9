package com.metamapa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.metamapa.controllers.ControladorRevisiones.ComentariosDTO;
import com.metamapa.dtos.output.ContribucionOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.RevisionOutputDTO;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ControladorRevisionesTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicioRevisiones servicioRevisiones;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testListarPendientes_DeberiaRetornar200ConListaVacia() throws Exception {
        // Arrange
        when(servicioRevisiones.listarPendientes())
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/revisiones/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testListarPendientes_DeberiaRetornar200ConContribuciones() throws Exception {
        // Arrange
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Evento Pendiente 1");
        hecho1.setDescripcion("Descripción 1");
        hecho1.setFecha(LocalDate.of(2023, 6, 15));
        hecho1.setCategoria("Historia");
        
        HechoOutputDTO hecho2 = new HechoOutputDTO();
        hecho2.setTitulo("Evento Pendiente 2");
        hecho2.setDescripcion("Descripción 2");
        hecho2.setFecha(LocalDate.of(2024, 1, 20));
        hecho2.setCategoria("Cultura");
        
        ContribucionOutputDTO contrib1 = new ContribucionOutputDTO(1L, hecho1, 101L);
        ContribucionOutputDTO contrib2 = new ContribucionOutputDTO(2L, hecho2, 102L);
        
        List<ContribucionOutputDTO> pendientes = Arrays.asList(contrib1, contrib2);
        
        when(servicioRevisiones.listarPendientes())
            .thenReturn(pendientes);

        // Act & Assert
        mockMvc.perform(get("/revisiones/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idContribucion").value(101))
                .andExpect(jsonPath("$[0].hecho.titulo").value("Evento Pendiente 1"))
                .andExpect(jsonPath("$[1].idContribucion").value(102))
                .andExpect(jsonPath("$[1].hecho.titulo").value("Evento Pendiente 2"));

        verify(servicioRevisiones, times(1)).listarPendientes();
    }

    @Test
    void testVerDetalle_DeberiaRetornar200ConDetalleRevision() throws Exception {
        // Arrange
        Long idContribucion = 101L;
        
        RevisionOutputDTO revisionDTO = new RevisionOutputDTO();
        revisionDTO.setIdContribucion(idContribucion);
        revisionDTO.setEstado("PENDIENTE");
        revisionDTO.setMensaje("Contribución en revisión");
        
        when(servicioRevisiones.detalle(idContribucion))
            .thenReturn(revisionDTO);

        // Act & Assert
        mockMvc.perform(get("/revisiones/{idContribucion}", idContribucion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContribucion").value(idContribucion))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.mensaje").value("Contribución en revisión"));

        verify(servicioRevisiones, times(1)).detalle(idContribucion);
    }

    @Test
    void testAceptar_SinComentarios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 101L;
        
        doNothing().when(servicioRevisiones).aceptar(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/aceptar", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1)).aceptar(eq(idContribucion), eq(null));
    }

    @Test
    void testAceptar_ConComentarios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 102L;
        ComentariosDTO comentarios = new ComentariosDTO();
        comentarios.setComentarios("Excelente contribución, muy bien documentada");
        
        doNothing().when(servicioRevisiones).aceptar(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/aceptar", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarios)))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1))
            .aceptar(eq(idContribucion), eq("Excelente contribución, muy bien documentada"));
    }

    @Test
    void testAceptarConCambios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 103L;
        ComentariosDTO comentarios = new ComentariosDTO();
        comentarios.setComentarios("Aceptado con modificaciones menores en la descripción");
        
        doNothing().when(servicioRevisiones).aceptarConSugerencias(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/aceptar-con-cambios", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarios)))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1))
            .aceptarConSugerencias(eq(idContribucion), eq("Aceptado con modificaciones menores en la descripción"));
    }

    @Test
    void testAceptarConCambios_SinComentarios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 104L;
        
        doNothing().when(servicioRevisiones).aceptarConSugerencias(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/aceptar-con-cambios", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1)).aceptarConSugerencias(eq(idContribucion), eq(null));
    }

    @Test
    void testRechazar_ConComentarios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 105L;
        ComentariosDTO comentarios = new ComentariosDTO();
        comentarios.setComentarios("La información no está suficientemente verificada");
        
        doNothing().when(servicioRevisiones).rechazar(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/rechazar", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarios)))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1))
            .rechazar(eq(idContribucion), eq("La información no está suficientemente verificada"));
    }

    @Test
    void testRechazar_SinComentarios_DeberiaRetornar204() throws Exception {
        // Arrange
        Long idContribucion = 106L;
        
        doNothing().when(servicioRevisiones).rechazar(eq(idContribucion), anyString());

        // Act & Assert
        mockMvc.perform(post("/revisiones/{idContribucion}/rechazar", idContribucion)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1)).rechazar(eq(idContribucion), eq(null));
    }

    @Test
    void testFlujoCompletoRevision_AceptarConComentarios() throws Exception {
        // Arrange - Primero listar pendientes
        HechoOutputDTO hecho = new HechoOutputDTO();
        hecho.setTitulo("Evento Importante");
        hecho.setDescripcion("Descripción detallada");
        hecho.setFecha(LocalDate.of(2024, 10, 28));
        hecho.setCategoria("Historia");
        
        ContribucionOutputDTO contrib = new ContribucionOutputDTO(1L, hecho, 200L);
        when(servicioRevisiones.listarPendientes())
            .thenReturn(List.of(contrib));

        // Act & Assert - Listar pendientes
        mockMvc.perform(get("/revisiones/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idContribucion").value(200));

        // Arrange - Ver detalle
        RevisionOutputDTO revisionDTO = new RevisionOutputDTO();
        revisionDTO.setIdContribucion(200L);
        revisionDTO.setEstado("PENDIENTE");
        revisionDTO.setMensaje("En espera de revisión");
        
        when(servicioRevisiones.detalle(200L))
            .thenReturn(revisionDTO);

        // Act & Assert - Ver detalle
        mockMvc.perform(get("/revisiones/{idContribucion}", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        // Arrange - Aceptar con comentarios
        ComentariosDTO comentarios = new ComentariosDTO();
        comentarios.setComentarios("Contribución valiosa y bien documentada");
        
        doNothing().when(servicioRevisiones).aceptar(eq(200L), anyString());

        // Act & Assert - Aceptar
        mockMvc.perform(post("/revisiones/{idContribucion}/aceptar", 200L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarios)))
                .andExpect(status().isNoContent());

        verify(servicioRevisiones, times(1)).aceptar(eq(200L), anyString());
    }
}
