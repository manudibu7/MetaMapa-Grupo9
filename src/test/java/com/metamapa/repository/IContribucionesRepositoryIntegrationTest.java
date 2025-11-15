package com.metamapa.repository;

import com.metamapa.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para IContribucionesRepository
 * Verifica operaciones CRUD completas con relaciones entre entidades
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IContribucionesRepositoryIntegrationTest {

    @Autowired
    private IContribucionesRepository contribucionesRepository;

    @Autowired
    private IContribuyentesRepository contribuyentesRepository;

    private Contribuyente contribuyentePrueba;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos
        contribucionesRepository.deleteAll();
        contribuyentesRepository.deleteAll();

        // Crear un contribuyente de prueba
        contribuyentePrueba = new Contribuyente();
        contribuyentePrueba.setNombre("Juan");
        contribuyentePrueba.setApellido("Pérez");
        contribuyentePrueba.setEdad(30);
        contribuyentePrueba.setAnonimo(false);
        contribuyentePrueba = contribuyentesRepository.save(contribuyentePrueba);
    }

    /**
     * Test: CREATE - Guardar una contribución completa con todas sus relaciones
     */
    @Test
    void testGuardar_DeberiaCrearContribucionCompletaEnBD() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        // Act
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getContribuyente()).isNotNull();
        assertThat(guardada.getHecho()).isNotNull();
        assertThat(guardada.getRevision()).isNotNull();
        assertThat(guardada.getHecho().getTitulo()).isEqualTo("Independencia de Argentina");
        assertThat(guardada.getExportada()).isFalse();
        assertThat(guardada.getFechaDeCarga()).isNotNull();
    }

    /**
     * Test: READ - Buscar contribución por ID con todas sus relaciones
     */
    @Test
    void testBuscarPorId_DeberiaEncontrarContribucionConRelaciones() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act
        Optional<Contribucion> encontrada = contribucionesRepository.findById(guardada.getId());

        // Assert
        assertThat(encontrada).isPresent();
        Contribucion contrib = encontrada.get();
        assertThat(contrib.getHecho().getTitulo()).isEqualTo("Independencia de Argentina");
        assertThat(contrib.getHecho().getCategoria().getNombre()).isEqualTo("Historia");
        assertThat(contrib.getHecho().getLugarDeOcurrencia().getLatitud()).isEqualTo(-34.6037f);
        assertThat(contrib.getRevision().getEstado()).isEqualTo(EstadoRevision.PENDIENTE);
    }

    /**
     * Test: READ ALL - Listar todas las contribuciones
     */
    @Test
    void testFindAll_DeberiaListarTodasLasContribuciones() {
        // Arrange - Crear múltiples contribuciones
        Contribucion c1 = crearContribucionCompleta();
        c1.getHecho().setTitulo("Evento 1");

        Contribucion c2 = crearContribucionCompleta();
        c2.getHecho().setTitulo("Evento 2");

        Contribucion c3 = crearContribucionCompleta();
        c3.getHecho().setTitulo("Evento 3");

        contribucionesRepository.saveAll(List.of(c1, c2, c3));

        // Act
        List<Contribucion> todas = contribucionesRepository.findAll();

        // Assert
        assertThat(todas).hasSize(3);
        assertThat(todas)
                .extracting(c -> c.getHecho().getTitulo())
                .containsExactlyInAnyOrder("Evento 1", "Evento 2", "Evento 3");
    }

    /**
     * Test: UPDATE - Modificar una contribución existente
     */
    @Test
    void testActualizar_DeberiaModificarContribucionExistente() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act - Modificar
        guardada.setExportada(true);
        guardada.getHecho().setTitulo("Título Modificado");
        guardada.getHecho().setDescripcion("Nueva descripción actualizada");
        Contribucion actualizada = contribucionesRepository.save(guardada);

        // Assert
        assertThat(actualizada.getId()).isEqualTo(guardada.getId());
        assertThat(actualizada.getExportada()).isTrue();
        assertThat(actualizada.getHecho().getTitulo()).isEqualTo("Título Modificado");

        // Verificar persistencia
        Optional<Contribucion> verificada = contribucionesRepository.findById(guardada.getId());
        assertThat(verificada).isPresent();
        assertThat(verificada.get().getHecho().getTitulo()).isEqualTo("Título Modificado");
    }

    /**
     * Test: DELETE - Eliminar una contribución
     */
    @Test
    void testEliminar_DeberiaEliminarContribucionEnCascada() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);
        Long id = guardada.getId();

        // Act
        contribucionesRepository.deleteById(id);

        // Assert
        Optional<Contribucion> buscada = contribucionesRepository.findById(id);
        assertThat(buscada).isEmpty();
    }

    /**
     * Test: CASCADE - Verificar que al guardar contribución se guardan sus relaciones
     */
    @Test
    void testCascade_DeberiaPersistirHechoYRevisionAutomaticamente() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        // Act
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert - Verificar que Hecho y Revision tienen ID asignado
        assertThat(guardada.getHecho().getId()).isNotNull();
        assertThat(guardada.getRevision().getId()).isNotNull();
    }

    /**
     * Test: ORPHAN REMOVAL - Verificar que al eliminar contribución se eliminan Hecho y Revision
     */
    @Test
    void testOrphanRemoval_DeberiaEliminarHechoAlEliminarContribucion() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);
        Long hechoId = guardada.getHecho().getId();
        Long contribucionId = guardada.getId();

        // Act - Eliminar contribución
        contribucionesRepository.deleteById(contribucionId);
        contribucionesRepository.flush();

        // Assert - La contribución ya no debe existir
        assertThat(contribucionesRepository.findById(contribucionId)).isEmpty();
        // El Hecho debería eliminarse en cascada (orphanRemoval = true)
    }

    /**
     * Test: ESTADO DE REVISIÓN - Verificar cambios en el estado de revisión
     */
    @Test
    void testEstadoRevision_DeberiaActualizarCorrectamente() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act - Cambiar estado de revisión
        guardada.getRevision().setEstado(EstadoRevision.ACEPTADA);
        guardada.getRevision().setMensaje("Contribución aceptada");
        guardada.getRevision().setFecha(LocalDate.now());
        Contribucion actualizada = contribucionesRepository.save(guardada);

        // Assert
        assertThat(actualizada.getRevision().getEstado()).isEqualTo(EstadoRevision.ACEPTADA);
        assertThat(actualizada.getRevision().getMensaje()).isEqualTo("Contribución aceptada");
        assertThat(actualizada.getRevision().getFecha()).isNotNull();
    }

    /**
     * Test: EXPORTACIÓN - Marcar contribución como exportada
     */
    @Test
    void testExportacion_DeberiaMarcarComoExportada() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        contribucion.setExportada(false);
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act
        guardada.setExportada(true);
        Contribucion actualizada = contribucionesRepository.save(guardada);

        // Assert
        assertThat(actualizada.getExportada()).isTrue();

        // Verificar en BD
        Optional<Contribucion> verificada = contribucionesRepository.findById(guardada.getId());
        assertThat(verificada).isPresent();
        assertThat(verificada.get().getExportada()).isTrue();
    }

    /**
     * Test: MÚLTIPLES CONTRIBUCIONES - Un contribuyente puede tener varias contribuciones
     */
    @Test
    void testMultiplesContribuciones_MismoContribuyente() {
        // Arrange - Crear múltiples contribuciones del mismo contribuyente
        Contribucion c1 = crearContribucionCompleta();
        c1.getHecho().setTitulo("Primera Contribución");

        Contribucion c2 = crearContribucionCompleta();
        c2.getHecho().setTitulo("Segunda Contribución");

        Contribucion c3 = crearContribucionCompleta();
        c3.getHecho().setTitulo("Tercera Contribución");

        // Act
        contribucionesRepository.saveAll(List.of(c1, c2, c3));

        // Assert
        List<Contribucion> todas = contribucionesRepository.findAll();
        assertThat(todas).hasSize(3);
        assertThat(todas).allMatch(c -> c.getContribuyente().getId().equals(contribuyentePrueba.getId()));
    }

    /**
     * Test: UBICACIÓN EMBEBIDA - Verificar persistencia de ubicación
     */
    @Test
    void testUbicacion_DeberiaGuardarCorrectamente() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        contribucion.getHecho().getLugarDeOcurrencia().setLatitud(-31.4201f);
        contribucion.getHecho().getLugarDeOcurrencia().setLongitud(-64.1888f);

        // Act
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert
        assertThat(guardada.getHecho().getLugarDeOcurrencia()).isNotNull();
        assertThat(guardada.getHecho().getLugarDeOcurrencia().getLatitud()).isEqualTo(-31.4201f);
        assertThat(guardada.getHecho().getLugarDeOcurrencia().getLongitud()).isEqualTo(-64.1888f);
    }

    /**
     * Test: CATEGORÍA EMBEBIDA - Verificar persistencia de categoría
     */
    @Test
    void testCategoria_DeberiaGuardarCorrectamente() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        contribucion.getHecho().setCategoria(new Categoria("Cultura"));

        // Act
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert
        assertThat(guardada.getHecho().getCategoria()).isNotNull();
        assertThat(guardada.getHecho().getCategoria().getNombre()).isEqualTo("Cultura");
    }

    /**
     * Test: CONTAR - Verificar conteo de contribuciones
     */
    @Test
    void testCount_DeberiaContarCorrectamente() {
        // Arrange
        assertThat(contribucionesRepository.count()).isEqualTo(0);

        Contribucion c1 = crearContribucionCompleta();
        Contribucion c2 = crearContribucionCompleta();
        contribucionesRepository.saveAll(List.of(c1, c2));

        // Act & Assert
        assertThat(contribucionesRepository.count()).isEqualTo(2);
    }

    // Método auxiliar para crear una contribución completa
    private Contribucion crearContribucionCompleta() {
        Contribucion contribucion = new Contribucion();
        contribucion.setContribuyente(contribuyentePrueba);
        contribucion.setFechaDeCarga(LocalDate.now());
        contribucion.setExportada(false);

        // Crear Hecho
        Hecho hecho = new Hecho();
        hecho.setTitulo("Independencia de Argentina");
        hecho.setDescripcion("Declaración de independencia del 9 de julio de 1816");
        hecho.setCategoria(new Categoria("Historia"));
        hecho.setFecha(LocalDate.of(1816, 7, 9));
        hecho.setLugarDeOcurrencia(new Ubicacion(-34.6037f, -58.3816f));
        hecho.setOrigen("Fuente histórica oficial");
        contribucion.setHecho(hecho);

        // Crear Revisión
        Revision revision = new Revision();
        revision.setEstado(EstadoRevision.PENDIENTE);
        revision.setMensaje("El hecho está pendiente de revisión");
        contribucion.setRevision(revision);

        return contribucion;
    }

    /**
     * Test: TIPO DE HECHO - Un hecho nuevo debería ser de tipo TEXTO por defecto
     */
    @Test
    void testTipoDeHecho_DeberiaSerTextoInicial() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        // Act
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert
        assertThat(guardada.getHecho().getTipoDeHecho()).isEqualTo(TipoDeHecho.TEXTO);
        assertThat(guardada.getHecho().getAdjuntos()).isEmpty();
    }

    /**
     * Test: AGREGAR UN ADJUNTO - Debería cambiar el tipo a MULTIMEDIA
     */
    @Test
    void testAgregarAdjunto_DeberiaCambiarATipoMultimedia() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act - Agregar un adjunto
        Archivo archivo = new Archivo();
        archivo.setUrl("http://example.com/imagen.jpg");
        archivo.setTipo(TipoMedia.IMAGEN);
        archivo.setTamanio("2MB");

        guardada.getHecho().agregarAdjunto(archivo);
        Contribucion actualizada = contribucionesRepository.save(guardada);

        // Assert
        assertThat(actualizada.getHecho().getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
        assertThat(actualizada.getHecho().getAdjuntos()).hasSize(1);
        assertThat(actualizada.getHecho().getAdjuntos().get(0).getUrl()).isEqualTo("http://example.com/imagen.jpg");
        assertThat(actualizada.getHecho().getAdjuntos().get(0).getTipo()).isEqualTo(TipoMedia.IMAGEN);
    }

    /**
     * Test: MÚLTIPLES ADJUNTOS - Un hecho puede tener varios archivos adjuntos
     */
    @Test
    void testMultiplesAdjuntos_DeberiaGuardarTodos() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/imagen1.jpg");
        archivo1.setTipo(TipoMedia.IMAGEN);
        archivo1.setTamanio("1MB");

        Archivo archivo2 = new Archivo();
        archivo2.setUrl("http://example.com/video.mp4");
        archivo2.setTipo(TipoMedia.VIDEO);
        archivo2.setTamanio("50MB");

        Archivo archivo3 = new Archivo();
        archivo3.setUrl("http://example.com/audio.mp3");
        archivo3.setTipo(TipoMedia.AUDIO);
        archivo3.setTamanio("5MB");

        // Act - Agregar múltiples adjuntos
        contribucion.getHecho().agregarAdjunto(archivo1);
        contribucion.getHecho().agregarAdjunto(archivo2);
        contribucion.getHecho().agregarAdjunto(archivo3);

        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Assert
        assertThat(guardada.getHecho().getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
        assertThat(guardada.getHecho().getAdjuntos()).hasSize(3);

        // Verificar que se guardaron todos los adjuntos
        List<Archivo> adjuntos = guardada.getHecho().getAdjuntos();
        assertThat(adjuntos).extracting(Archivo::getTipo)
                .containsExactlyInAnyOrder(TipoMedia.IMAGEN, TipoMedia.VIDEO, TipoMedia.AUDIO);

        // Verificar la relación bidireccional
        assertThat(adjuntos).allMatch(a -> a.getHecho().equals(guardada.getHecho()));
    }

    /**
     * Test: ORPHAN REMOVAL ADJUNTOS - Los adjuntos deben eliminarse con el hecho
     */
    @Test
    void testEliminarHecho_DeberiaEliminarAdjuntosEnCascada() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/imagen.jpg");
        archivo1.setTipo(TipoMedia.IMAGEN);

        Archivo archivo2 = new Archivo();
        archivo2.setUrl("http://example.com/video.mp4");
        archivo2.setTipo(TipoMedia.VIDEO);

        contribucion.getHecho().agregarAdjunto(archivo1);
        contribucion.getHecho().agregarAdjunto(archivo2);

        Contribucion guardada = contribucionesRepository.save(contribucion);
        Long contribucionId = guardada.getId();

        // Act - Eliminar la contribución
        contribucionesRepository.deleteById(contribucionId);
        contribucionesRepository.flush();

        // Assert - La contribución no debe existir
        assertThat(contribucionesRepository.findById(contribucionId)).isEmpty();
        // Los adjuntos deberían eliminarse en cascada (orphanRemoval = true)
    }

    /**
     * Test: PERSISTENCIA DE ADJUNTOS - Verificar que al recuperar se cargan los adjuntos
     */
    @Test
    void testRecuperarContribucion_DeberiaCargarAdjuntos() {
        // Arrange
        Contribucion contribucion = crearContribucionCompleta();

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/documento.pdf");
        archivo1.setTipo(TipoMedia.TEXTO);

        contribucion.getHecho().agregarAdjunto(archivo1);
        Contribucion guardada = contribucionesRepository.save(contribucion);
        Long id = guardada.getId();

        // Act - Limpiar el contexto y recuperar
        contribucionesRepository.flush();
        Optional<Contribucion> recuperada = contribucionesRepository.findById(id);

        // Assert
        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getHecho().getAdjuntos()).hasSize(1);
        assertThat(recuperada.get().getHecho().getTipoDeHecho()).isEqualTo(TipoDeHecho.MULTIMEDIA);
    }

    /**
     * Test: ACTUALIZAR ADJUNTOS - Agregar más adjuntos a un hecho existente
     */
    @Test
    void testActualizarAdjuntos_DeberiaAgregarNuevosAdjuntos() {
        // Arrange - Crear contribución con un adjunto
        Contribucion contribucion = crearContribucionCompleta();

        Archivo archivo1 = new Archivo();
        archivo1.setUrl("http://example.com/imagen1.jpg");
        archivo1.setTipo(TipoMedia.IMAGEN);

        contribucion.getHecho().agregarAdjunto(archivo1);
        Contribucion guardada = contribucionesRepository.save(contribucion);

        // Act - Agregar otro adjunto
        Archivo archivo2 = new Archivo();
        archivo2.setUrl("http://example.com/imagen2.jpg");
        archivo2.setTipo(TipoMedia.IMAGEN);

        guardada.getHecho().agregarAdjunto(archivo2);
        Contribucion actualizada = contribucionesRepository.save(guardada);

        // Assert
        assertThat(actualizada.getHecho().getAdjuntos()).hasSize(2);
        assertThat(actualizada.getHecho().getAdjuntos())
                .extracting(Archivo::getUrl)
                .containsExactlyInAnyOrder("http://example.com/imagen1.jpg", "http://example.com/imagen2.jpg");
    }
}



