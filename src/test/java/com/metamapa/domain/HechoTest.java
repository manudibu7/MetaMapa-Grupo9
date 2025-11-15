package com.metamapa.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para verificar el comportamiento de Hecho con respecto a adjuntos y tipos
 */
class HechoTest {

    @Test
    void testHechoNuevo_DeberiaSerTipoTexto() {
        // Arrange & Act
        Hecho hecho = new Hecho();

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(hecho.getAdjuntos()).isEmpty();
    }

    @Test
    void testAgregarUnAdjunto_DeberiaCambiarATipoMultimedia() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento de prueba");
        hecho.setDescripcion("Descripción");
        hecho.setFecha(LocalDate.now());

        Archivo archivo = new Archivo();
        archivo.setUrl("http://example.com/imagen.jpg");
        archivo.setTipo(TipoMedia.IMAGEN);

        // Act
        hecho.agregarAdjunto(archivo);

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
        assertThat(hecho.getAdjuntos()).hasSize(1);
        assertThat(hecho.getAdjuntos().get(0)).isEqualTo(archivo);
        assertThat(archivo.getHecho()).isEqualTo(hecho);
    }

    @Test
    void testAgregarMultiplesAdjuntos_DeberiaMantenerTipoMultimedia() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento con varios archivos");
        hecho.setDescripcion("Descripción");
        hecho.setFecha(LocalDate.now());

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/imagen.jpg");
        archivo1.setTipo(TipoMedia.IMAGEN);

        Archivo archivo2 = new Archivo();
        archivo2.setUrl("http://example.com/video.mp4");
        archivo2.setTipo(TipoMedia.VIDEO);

        Archivo archivo3 = new Archivo();
        archivo3.setUrl("http://example.com/audio.mp3");
        archivo3.setTipo(TipoMedia.AUDIO);

        // Act
        hecho.agregarAdjunto(archivo1);
        hecho.agregarAdjunto(archivo2);
        hecho.agregarAdjunto(archivo3);

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
        assertThat(hecho.getAdjuntos()).hasSize(3);
        assertThat(hecho.getAdjuntos()).containsExactly(archivo1, archivo2, archivo3);

        // Verificar relación bidireccional
        assertThat(archivo1.getHecho()).isEqualTo(hecho);
        assertThat(archivo2.getHecho()).isEqualTo(hecho);
        assertThat(archivo3.getHecho()).isEqualTo(hecho);
    }

    @Test
    void testAgregarAdjuntoNull_NoDeberiaAfectarElHecho() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento sin adjuntos");

        // Act
        hecho.agregarAdjunto(null);

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(hecho.getAdjuntos()).isEmpty();
    }

    @Test
    void testSetAdjuntosConListaVacia_DeberiaSerTipoTexto() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento sin archivos");

        // Act
        hecho.setAdjuntos(new ArrayList<>());

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(hecho.getAdjuntos()).isEmpty();
    }

    @Test
    void testSetAdjuntosConListaNull_DeberiaCrearListaVaciaYTipoTexto() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento sin archivos");

        // Act
        hecho.setAdjuntos(null);

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(hecho.getAdjuntos()).isNotNull();
        assertThat(hecho.getAdjuntos()).isEmpty();
    }

    @Test
    void testSetAdjuntosConListaConElementos_DeberiaCambiarATipoMultimedia() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento multimedia");

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/imagen1.jpg");
        archivo1.setTipo(TipoMedia.IMAGEN);

        Archivo archivo2 = new Archivo();
        archivo2.setUrl("http://example.com/imagen2.jpg");
        archivo2.setTipo(TipoMedia.IMAGEN);

        // Act
        hecho.setAdjuntos(Arrays.asList(archivo1, archivo2));

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
        assertThat(hecho.getAdjuntos()).hasSize(2);

        // Verificar relación bidireccional
        assertThat(archivo1.getHecho()).isEqualTo(hecho);
        assertThat(archivo2.getHecho()).isEqualTo(hecho);
    }

    @Test
    void testCambiarDeMultimediaATexto_AlQuitarTodosLosAdjuntos() {
        // Arrange
        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento que cambia de tipo");

        Archivo archivo = new Archivo();
        archivo.setUrl("http://example.com/imagen.jpg");
        archivo.setTipo(TipoMedia.IMAGEN);

        hecho.agregarAdjunto(archivo);
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);

        // Act - Quitar todos los adjuntos
        hecho.setAdjuntos(new ArrayList<>());

        // Assert
        assertThat(hecho.getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(hecho.getAdjuntos()).isEmpty();
    }
}

