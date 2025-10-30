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
}

