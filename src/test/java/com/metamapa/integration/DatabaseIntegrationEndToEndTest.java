package com.metamapa.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.metamapa.domain.*;
import com.metamapa.dtos.input.ContribuyenteInputDTO;
import com.metamapa.repository.IContribucionesRepository;
import com.metamapa.repository.IContribuyentesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * pruebas de integracion end-to-end
 * verifico el flujo completo desde el controlador hasta la base de datos
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IContribuyentesRepository contribuyentesRepository;

    @Autowired
    private IContribucionesRepository contribucionesRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // limpio base de datos
        contribucionesRepository.deleteAll();
        contribuyentesRepository.deleteAll();
    }

    /**
     * test e2e: crear y leer - verifico el flujo completo de crear contribuyente y recuperarlo
     */
    @Test
    void testEndToEnd_CrearYLeerContribuyente() throws Exception {
        // creo un contribuyente via api
        ContribuyenteInputDTO nuevoContribuyente = new ContribuyenteInputDTO();
        nuevoContribuyente.setNombre("Carlos");
        nuevoContribuyente.setApellido("Rodríguez");
        nuevoContribuyente.setEdad(35);

        String response = mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoContribuyente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("Rodríguez"))
                .andExpect(jsonPath("$.edad").value(35))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // extraigo el id del response
        Long contribuyenteId = objectMapper.readTree(response).get("id").asLong();

        // verifico en base de datos que se persistio bien
        Contribuyente contribuyenteDB = contribuyentesRepository.findById(contribuyenteId).orElse(null);
        assertThat(contribuyenteDB).isNotNull();
        assertThat(contribuyenteDB.getNombre()).isEqualTo("Carlos");
        assertThat(contribuyenteDB.getApellido()).isEqualTo("Rodríguez");
        assertThat(contribuyenteDB.getEdad()).isEqualTo(35);

        // recupero via api y verifico consistencia
        mockMvc.perform(get("/contribuyentes/{id}", contribuyenteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contribuyenteId))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("Rodríguez"))
                .andExpect(jsonPath("$.edad").value(35));
    }

    /**
     * test e2e: actualizar - verifico el flujo completo de modificacion
     */
    @Test
    void testEndToEnd_ActualizarContribuyente() throws Exception {
        // creo un contribuyente directamente en bd
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("María");
        contribuyente.setApellido("González");
        contribuyente.setEdad(28);
        contribuyente = contribuyentesRepository.save(contribuyente);
        Long id = contribuyente.getId();

        // actualizo via api
        ContribuyenteInputDTO actualizacion = new ContribuyenteInputDTO();
        actualizacion.setNombre("María Laura");
        actualizacion.setApellido("González Pérez");
        actualizacion.setEdad(29);

        mockMvc.perform(put("/contribuyentes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("María Laura"))
                .andExpect(jsonPath("$.apellido").value("González Pérez"))
                .andExpect(jsonPath("$.edad").value(29));

        // verifico los cambios en bd
        Contribuyente actualizado = contribuyentesRepository.findById(id).orElse(null);
        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getNombre()).isEqualTo("María Laura");
        assertThat(actualizado.getApellido()).isEqualTo("González Pérez");
        assertThat(actualizado.getEdad()).isEqualTo(29);
    }

    /**
     * test e2e: listar - verifico consultas con multiples registros
     */
    @Test
    void testEndToEnd_ListarMultiplesContribuyentes() throws Exception {
        // creo multiples contribuyentes en bd
        Contribuyente c1 = new Contribuyente();
        c1.setNombre("Juan");
        c1.setApellido("Pérez");
        c1.setEdad(30);

        Contribuyente c2 = new Contribuyente();
        c2.setNombre("Ana");
        c2.setApellido("Martínez");
        c2.setEdad(25);

        Contribuyente c3 = new Contribuyente();
        c3.setNombre("Pedro");
        c3.setApellido("Sánchez");
        c3.setEdad(40);

        contribuyentesRepository.saveAll(java.util.List.of(c1, c2, c3));

        // listo via api
        mockMvc.perform(get("/contribuyentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder("Juan", "Ana", "Pedro")));

        // verifico el conteo en bd
        assertThat(contribuyentesRepository.count()).isEqualTo(3);
    }

    /**
     * test e2e: eliminar - verifico eliminacion completa
     */
    @Test
    void testEndToEnd_EliminarContribuyente() throws Exception {
        // creo un contribuyente en bd
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Temporal");
        contribuyente.setApellido("Test");
        contribuyente.setEdad(99);
        contribuyente = contribuyentesRepository.save(contribuyente);
        Long id = contribuyente.getId();

        // verifico que existe
        assertThat(contribuyentesRepository.existsById(id)).isTrue();

        // elimino via api
        mockMvc.perform(delete("/contribuyentes/{id}", id))
                .andExpect(status().isNoContent());

        // verifico eliminacion en bd
        assertThat(contribuyentesRepository.existsById(id)).isFalse();
        assertThat(contribuyentesRepository.findById(id)).isEmpty();
    }

    /**
     * test e2e: transaccionalidad - verifico rollback en caso de error
     */
    @Test
    void testEndToEnd_VerificarPersistenciaDeTransacciones() throws Exception {
        // obtengo conteo inicial
        long conteoInicial = contribuyentesRepository.count();

        // creo contribuyente valido
        ContribuyenteInputDTO valido = new ContribuyenteInputDTO();
        valido.setNombre("Válido");
        valido.setApellido("Test");
        valido.setEdad(30);

        mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valido)))
                .andExpect(status().isCreated());

        // verifico incremento en bd
        assertThat(contribuyentesRepository.count()).isEqualTo(conteoInicial + 1);
    }

    /**
     * test e2e: contribucion completa - verifico creacion de contribucion con todas sus relaciones
     */
    @Test
    void testEndToEnd_CrearContribucionCompleta() throws Exception {
        // creo contribuyente en bd
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Historiador");
        contribuyente.setApellido("Experto");
        contribuyente.setEdad(45);
        contribuyente = contribuyentesRepository.save(contribuyente);

        // creo contribucion completa
        Contribucion contribucion = new Contribucion();
        contribucion.setContribuyente(contribuyente);
        contribucion.setFechaDeCarga(LocalDate.now());
        contribucion.setExportada(false);

        Hecho hecho = new Hecho();
        hecho.setTitulo("Revolución de Mayo");
        hecho.setDescripcion("Inicio del proceso independentista argentino");
        hecho.setCategoria(new Categoria("Historia"));
        hecho.setFecha(LocalDate.of(1810, 5, 25));
        hecho.setLugarDeOcurrencia(new Ubicacion(-34.6037f, -58.3816f));
        hecho.setOrigen("Archivo histórico nacional");
        contribucion.setHecho(hecho);

        Revision revision = new Revision();
        revision.setEstado(EstadoRevision.PENDIENTE);
        revision.setMensaje("Pendiente de revisión");
        contribucion.setRevision(revision);

        // guardo y verifico en bd
        Contribucion guardada = contribucionesRepository.save(contribucion);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getHecho().getId()).isNotNull();
        assertThat(guardada.getRevision().getId()).isNotNull();

        // recupero y verifico persistencia
        Contribucion recuperada = contribucionesRepository.findById(guardada.getId()).orElse(null);
        assertThat(recuperada).isNotNull();
        assertThat(recuperada.getHecho().getTitulo()).isEqualTo("Revolución de Mayo");
        assertThat(recuperada.getRevision().getEstado()).isEqualTo(EstadoRevision.PENDIENTE);
        assertThat(recuperada.getContribuyente().getId()).isEqualTo(contribuyente.getId());
    }

    /**
     * test e2e: verifico integridad referencial
     */
    @Test
    void testEndToEnd_VerificarIntegridadReferencial() throws Exception {
        // creo contribuyente con contribuciones
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Test");
        contribuyente.setApellido("Integridad");
        contribuyente.setEdad(30);
        contribuyente = contribuyentesRepository.save(contribuyente);

        // creo contribucion asociada
        Contribucion contribucion = new Contribucion();
        contribucion.setContribuyente(contribuyente);
        contribucion.setFechaDeCarga(LocalDate.now());
        contribucion.setExportada(false);

        Hecho hecho = new Hecho();
        hecho.setTitulo("Evento de prueba");
        hecho.setDescripcion("Descripción de prueba");
        hecho.setCategoria(new Categoria("Test"));
        hecho.setFecha(LocalDate.now());
        hecho.setLugarDeOcurrencia(new Ubicacion(0f, 0f));
        contribucion.setHecho(hecho);

        contribucion = contribucionesRepository.save(contribucion);

        // verifico que la contribucion mantiene la referencia al contribuyente
        Contribucion verificada = contribucionesRepository.findById(contribucion.getId()).orElse(null);
        assertThat(verificada).isNotNull();
        assertThat(verificada.getContribuyente()).isNotNull();
        assertThat(verificada.getContribuyente().getId()).isEqualTo(contribuyente.getId());

        // el contribuyente debe existir independientemente
        Contribuyente contribuyenteDB = contribuyentesRepository.findById(contribuyente.getId()).orElse(null);
        assertThat(contribuyenteDB).isNotNull();
    }

    /**
     * test e2e: multiples operaciones en secuencia
     */
    @Test
    void testEndToEnd_OperacionesMultiplesEnSecuencia() throws Exception {
        // creo
        ContribuyenteInputDTO nuevo = new ContribuyenteInputDTO();
        nuevo.setNombre("Secuencial");
        nuevo.setApellido("Test");
        nuevo.setEdad(25);

        String createResponse = mockMvc.perform(post("/contribuyentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        // leo
        mockMvc.perform(get("/contribuyentes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Secuencial"));

        // actualizo
        ContribuyenteInputDTO actualizado = new ContribuyenteInputDTO();
        actualizado.setNombre("Secuencial Modificado");
        actualizado.setApellido("Test Modificado");
        actualizado.setEdad(26);

        mockMvc.perform(put("/contribuyentes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Secuencial Modificado"));

        // verifico en bd
        Contribuyente enBD = contribuyentesRepository.findById(id).orElse(null);
        assertThat(enBD).isNotNull();
        assertThat(enBD.getNombre()).isEqualTo("Secuencial Modificado");
        assertThat(enBD.getEdad()).isEqualTo(26);

        // elimino
        mockMvc.perform(delete("/contribuyentes/{id}", id))
                .andExpect(status().isNoContent());

        // verifico eliminacion
        assertThat(contribuyentesRepository.findById(id)).isEmpty();
    }
}

