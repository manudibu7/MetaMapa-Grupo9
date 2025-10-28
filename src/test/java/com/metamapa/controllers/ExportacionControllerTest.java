package com.metamapa.controllers;

import com.metamapa.dtos.output.AdjuntoOutputDTO;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.dtos.output.UbicacionOutputDTO;
import com.metamapa.infrastructure.Outbox.BandejaDeSalida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExportacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BandejaDeSalida bandejaDeSalida;

    @BeforeEach
    void setUp() {
        Mockito.reset(bandejaDeSalida);
    }

    @Test
    void testObtenerHechos_ConListaVacia_DeberiaRetornar200() throws Exception {
        // Arrange
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bandejaDeSalida, times(1)).pendientesDeEnvio();
        verify(bandejaDeSalida, times(1)).limpiar();
    }

    @Test
    void testObtenerHechos_ConUnHecho_DeberiaRetornar200YLimpiar() throws Exception {
        // Arrange
        HechoOutputDTO hecho = new HechoOutputDTO();
        hecho.setTitulo("Evento Histórico");
        hecho.setDescripcion("Descripción del evento");
        hecho.setFecha(LocalDate.of(2024, 10, 28));
        hecho.setCategoria("Historia");
        hecho.setUbicacion(new UbicacionOutputDTO(-34.6f, -58.4f));
        
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(List.of(hecho));

        // Act & Assert
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Evento Histórico"))
                .andExpect(jsonPath("$[0].descripcion").value("Descripción del evento"))
                .andExpect(jsonPath("$[0].categoria").value("Historia"));

        verify(bandejaDeSalida, times(1)).pendientesDeEnvio();
        verify(bandejaDeSalida, times(1)).limpiar();
    }

    @Test
    void testObtenerHechos_ConVariosHechos_DeberiaRetornarTodos() throws Exception {
        // Arrange
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Partido de Fútbol");
        hecho1.setDescripcion("Final del mundial");
        hecho1.setFecha(LocalDate.of(2022, 12, 18));
        hecho1.setCategoria("Deportes");
        hecho1.setUbicacion(new UbicacionOutputDTO(-25.2637f, 51.5328f));
        
        AdjuntoOutputDTO adjunto = new AdjuntoOutputDTO(1L, "http://example.com/imagen.jpg", "image/jpeg");
        hecho1.setAdjunto(adjunto);
        
        HechoOutputDTO hecho2 = new HechoOutputDTO();
        hecho2.setTitulo("Conferencia");
        hecho2.setDescripcion("Conferencia sobre IA");
        hecho2.setFecha(LocalDate.of(2024, 3, 15));
        hecho2.setCategoria("Tecnología");
        hecho2.setUbicacion(new UbicacionOutputDTO(-34.6f, -58.4f));
        hecho2.setAdjunto(null); // Sin adjunto
        
        HechoOutputDTO hecho3 = new HechoOutputDTO();
        hecho3.setTitulo("Evento Cultural");
        hecho3.setDescripcion("Festival de música");
        hecho3.setFecha(LocalDate.of(2024, 7, 20));
        hecho3.setCategoria("Cultura");
        hecho3.setUbicacion(null); // Sin ubicación
        
        List<HechoOutputDTO> hechos = Arrays.asList(hecho1, hecho2, hecho3);
        
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(hechos);

        // Act & Assert
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].titulo").value("Partido de Fútbol"))
                .andExpect(jsonPath("$[0].adjunto").exists())
                .andExpect(jsonPath("$[0].adjunto.url").value("http://example.com/imagen.jpg"))
                .andExpect(jsonPath("$[1].titulo").value("Conferencia"))
                .andExpect(jsonPath("$[1].adjunto").doesNotExist())
                .andExpect(jsonPath("$[2].titulo").value("Evento Cultural"))
                .andExpect(jsonPath("$[2].ubicacion").doesNotExist());

        verify(bandejaDeSalida, times(1)).pendientesDeEnvio();
        verify(bandejaDeSalida, times(1)).limpiar();
    }

    @Test
    void testObtenerHechos_DebeLimpiarOutboxDespuesDeObtener() throws Exception {
        // Arrange
        HechoOutputDTO hecho = new HechoOutputDTO();
        hecho.setTitulo("Hecho de Prueba");
        hecho.setDescripcion("Para verificar limpieza");
        hecho.setFecha(LocalDate.of(2024, 10, 28));
        hecho.setCategoria("Test");
        
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(List.of(hecho));

        // Act
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk());

        // Assert - Verificar orden de llamadas
        var inOrder = inOrder(bandejaDeSalida);
        inOrder.verify(bandejaDeSalida).pendientesDeEnvio();
        inOrder.verify(bandejaDeSalida).limpiar();
    }

    @Test
    void testObtenerHechos_ConHechoConTodosLosCampos() throws Exception {
        // Arrange
        HechoOutputDTO hecho = new HechoOutputDTO();
        hecho.setTitulo("Evento Completo");
        hecho.setDescripcion("Descripción completa del evento");
        hecho.setFecha(LocalDate.of(2024, 10, 28));
        hecho.setCategoria("Historia");
        
        UbicacionOutputDTO ubicacion = new UbicacionOutputDTO(-34.6037f, -58.3816f);
        hecho.setUbicacion(ubicacion);
        
        AdjuntoOutputDTO adjunto = new AdjuntoOutputDTO(5L, "http://example.com/video.mp4", "video/mp4");
        hecho.setAdjunto(adjunto);
        
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(List.of(hecho));

        // Act & Assert
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Evento Completo"))
                .andExpect(jsonPath("$[0].descripcion").value("Descripción completa del evento"))
                .andExpect(jsonPath("$[0].fecha").value("2024-10-28"))
                .andExpect(jsonPath("$[0].categoria").value("Historia"))
                .andExpect(jsonPath("$[0].ubicacion.latitud").value(-34.6037))
                .andExpect(jsonPath("$[0].ubicacion.longitud").value(-58.3816))
                .andExpect(jsonPath("$[0].adjunto.id").value(5))
                .andExpect(jsonPath("$[0].adjunto.url").value("http://example.com/video.mp4"))
                .andExpect(jsonPath("$[0].adjunto.tipo").value("video/mp4"));

        verify(bandejaDeSalida, times(1)).pendientesDeEnvio();
        verify(bandejaDeSalida, times(1)).limpiar();
    }

    @Test
    void testObtenerHechos_MultiplesLlamadas_DebeLimpiarCadaVez() throws Exception {
        // Arrange
        HechoOutputDTO hecho1 = new HechoOutputDTO();
        hecho1.setTitulo("Primera llamada");
        hecho1.setFecha(LocalDate.of(2024, 1, 1));
        hecho1.setCategoria("Test");
        
        HechoOutputDTO hecho2 = new HechoOutputDTO();
        hecho2.setTitulo("Segunda llamada");
        hecho2.setFecha(LocalDate.of(2024, 2, 1));
        hecho2.setCategoria("Test");
        
        when(bandejaDeSalida.pendientesDeEnvio())
            .thenReturn(List.of(hecho1))
            .thenReturn(List.of(hecho2));

        // Act & Assert - Primera llamada
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Primera llamada"));

        // Act & Assert - Segunda llamada
        mockMvc.perform(get("/export/hechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Segunda llamada"));

        // Verificar que se llamó limpiar dos veces
        verify(bandejaDeSalida, times(2)).pendientesDeEnvio();
        verify(bandejaDeSalida, times(2)).limpiar();
    }
}
