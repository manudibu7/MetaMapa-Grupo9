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
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.exceptions.RecursoNoEncontradoException;
import com.metamapa.services.ServicioContribuciones;
import com.metamapa.services.ServicioContribuyente;
import com.metamapa.services.ServicioRevisiones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    // =====================================================================
    // TESTS PARA OBTENER CONTRIBUCIONES POR CONTRIBUYENTE
    // =====================================================================

    @Test
    void testObtenerContribucionesPorContribuyente_DeberiaRetornar200YLista() throws Exception {
        Long contribuyenteId = 1L;

        // Crear contribuciones de prueba
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Contribución 1");
        hecho1.setDescripcion("Descripción 1");
        hecho1.setFecha(LocalDate.of(2023, 1, 15));
        hecho1.setCategoria("Historia");

        HechoOutputDTO hecho2 = new HechoOutputDTO();
        hecho2.setTitulo("Contribución 2");
        hecho2.setDescripcion("Descripción 2");
        hecho2.setFecha(LocalDate.of(2023, 2, 20));
        hecho2.setCategoria("Cultura");

        ContribucionOutputDTO contribucion1 = new ContribucionOutputDTO(contribuyenteId, hecho1, 10L);
        ContribucionOutputDTO contribucion2 = new ContribucionOutputDTO(contribuyenteId, hecho2, 11L);

        List<ContribucionOutputDTO> contribuciones = Arrays.asList(contribucion1, contribucion2);

        when(servicioContribuciones.obtenerContribucionesPorContribuyente(contribuyenteId))
            .thenReturn(contribuciones);

        mockMvc.perform(get("/contribuciones/contribuyente/{contribuyenteId}", contribuyenteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idContribucion").value(10))
                .andExpect(jsonPath("$[0].hecho.titulo").value("Contribución 1"))
                .andExpect(jsonPath("$[1].idContribucion").value(11))
                .andExpect(jsonPath("$[1].hecho.titulo").value("Contribución 2"));

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorContribuyente(contribuyenteId);
    }

    @Test
    void testObtenerContribucionesPorContribuyente_SinContribuciones_DeberiaRetornarListaVacia() throws Exception {
        Long contribuyenteId = 2L;

        when(servicioContribuciones.obtenerContribucionesPorContribuyente(contribuyenteId))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/contribuciones/contribuyente/{contribuyenteId}", contribuyenteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorContribuyente(contribuyenteId);
    }

    @Test
    void testObtenerContribucionesPorContribuyente_ContribuyenteNoExiste_DeberiaRetornar404() throws Exception {
        Long contribuyenteIdInexistente = 999L;

        when(servicioContribuciones.obtenerContribucionesPorContribuyente(contribuyenteIdInexistente))
            .thenThrow(new RecursoNoEncontradoException("Contribuyente no encontrado con ID: " + contribuyenteIdInexistente));

        mockMvc.perform(get("/contribuciones/contribuyente/{contribuyenteId}", contribuyenteIdInexistente))
                .andExpect(status().isNotFound());

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorContribuyente(contribuyenteIdInexistente);
    }

    @Test
    void testObtenerContribucionesPorKeycloakId_DeberiaRetornar200YLista() throws Exception {
        String keycloakId = "keycloak-user-123";
        Long contribuyenteId = 1L;

        // Crear contribuciones de prueba
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Contribución Keycloak 1");
        hecho1.setDescripcion("Descripción desde Keycloak");
        hecho1.setFecha(LocalDate.of(2023, 3, 10));
        hecho1.setCategoria("Tecnología");

        ContribucionOutputDTO contribucion1 = new ContribucionOutputDTO(contribuyenteId, hecho1, 20L);

        List<ContribucionOutputDTO> contribuciones = Arrays.asList(contribucion1);

        when(servicioContribuciones.obtenerContribucionesPorKeycloakId(keycloakId))
            .thenReturn(contribuciones);

        mockMvc.perform(get("/contribuciones/keycloak/{keycloakId}", keycloakId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idContribucion").value(20))
                .andExpect(jsonPath("$[0].hecho.titulo").value("Contribución Keycloak 1"));

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorKeycloakId(keycloakId);
    }

    @Test
    void testObtenerContribucionesPorKeycloakId_SinContribuciones_DeberiaRetornarListaVacia() throws Exception {
        String keycloakId = "keycloak-user-456";

        when(servicioContribuciones.obtenerContribucionesPorKeycloakId(keycloakId))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/contribuciones/keycloak/{keycloakId}", keycloakId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorKeycloakId(keycloakId);
    }

    @Test
    void testObtenerContribucionesPorKeycloakId_KeycloakIdNoExiste_DeberiaRetornar404() throws Exception {
        String keycloakIdInexistente = "keycloak-inexistente-999";

        when(servicioContribuciones.obtenerContribucionesPorKeycloakId(keycloakIdInexistente))
            .thenThrow(new RecursoNoEncontradoException("Contribuyente no encontrado con keycloakId: " + keycloakIdInexistente));

        mockMvc.perform(get("/contribuciones/keycloak/{keycloakId}", keycloakIdInexistente))
                .andExpect(status().isNotFound());

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorKeycloakId(keycloakIdInexistente);
    }

    @Test
    void testObtenerContribucionesPorContribuyente_MultipleContribuciones_DeberiaRetornarTodas() throws Exception {
        Long contribuyenteId = 5L;

        // Crear 5 contribuciones de prueba
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Contribución A");
        hecho1.setFecha(LocalDate.of(2023, 1, 1));
        hecho1.setCategoria("Cat1");

        HechoOutputDTO hecho2 = new HechoOutputDTO();
        hecho2.setTitulo("Contribución B");
        hecho2.setFecha(LocalDate.of(2023, 2, 1));
        hecho2.setCategoria("Cat2");

        HechoOutputDTO hecho3 = new HechoOutputDTO();
        hecho3.setTitulo("Contribución C");
        hecho3.setFecha(LocalDate.of(2023, 3, 1));
        hecho3.setCategoria("Cat3");

        List<ContribucionOutputDTO> contribuciones = Arrays.asList(
            new ContribucionOutputDTO(contribuyenteId, hecho1, 100L),
            new ContribucionOutputDTO(contribuyenteId, hecho2, 101L),
            new ContribucionOutputDTO(contribuyenteId, hecho3, 102L)
        );

        when(servicioContribuciones.obtenerContribucionesPorContribuyente(contribuyenteId))
            .thenReturn(contribuciones);

        mockMvc.perform(get("/contribuciones/contribuyente/{contribuyenteId}", contribuyenteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].hecho.titulo").value("Contribución A"))
                .andExpect(jsonPath("$[1].hecho.titulo").value("Contribución B"))
                .andExpect(jsonPath("$[2].hecho.titulo").value("Contribución C"));

        verify(servicioContribuciones, times(1)).obtenerContribucionesPorContribuyente(contribuyenteId);
    }
}
