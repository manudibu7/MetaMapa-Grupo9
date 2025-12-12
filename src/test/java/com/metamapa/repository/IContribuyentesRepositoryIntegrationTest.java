package com.metamapa.repository;

import com.metamapa.domain.Contribuyente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * pruebas de integracion para IContribuyentesRepository
 * verifico que las operaciones crud funcionen correctamente con la base de datos
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IContribuyentesRepositoryIntegrationTest {

    @Autowired
    private IContribuyentesRepository contribuyentesRepository;

    @BeforeEach
    void setUp() {
        // limpio la base de datos antes de cada prueba
        contribuyentesRepository.deleteAll();
    }

    /**
     * test create: guardo un contribuyente en la base de datos
     */
    @Test
    void testGuardar_DeberiaCrearContribuyenteEnBD() {
        // preparo el contribuyente
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Juan");
        contribuyente.setApellido("Pérez");
        contribuyente.setEdad(30);

        // guardo en bd
        Contribuyente guardado = contribuyentesRepository.save(contribuyente);

        // verifico que se creo correctamente
        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Juan");
        assertThat(guardado.getApellido()).isEqualTo("Pérez");
        assertThat(guardado.getEdad()).isEqualTo(30);
    }

    /**
     * test read: busco un contribuyente por id
     */
    @Test
    void testBuscarPorId_DeberiaEncontrarContribuyente() {
        // primero guardo un contribuyente
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("María");
        contribuyente.setApellido("González");
        contribuyente.setEdad(25);
        Contribuyente guardado = contribuyentesRepository.save(contribuyente);

        // busco por id
        Optional<Contribuyente> encontrado = contribuyentesRepository.findById(guardado.getId());

        // verifico que lo encontre
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(guardado.getId());
        assertThat(encontrado.get().getNombre()).isEqualTo("María");
        assertThat(encontrado.get().getApellido()).isEqualTo("González");
    }

    /**
     * test read: busco un contribuyente que no existe
     */
    @Test
    void testBuscarPorId_NoDeberiaEncontrarContribuyenteInexistente() {
        // busco un id que no existe
        Optional<Contribuyente> encontrado = contribuyentesRepository.findById(999L);

        // verifico que no lo encuentre
        assertThat(encontrado).isEmpty();
    }

    /**
     * test read all: listo todos los contribuyentes
     */
    @Test
    void testFindAll_DeberiaListarTodosLosContribuyentes() {
        // creo multiples contribuyentes
        Contribuyente c1 = new Contribuyente();
        c1.setNombre("Carlos");
        c1.setApellido("López");
        c1.setEdad(35);

        Contribuyente c2 = new Contribuyente();
        c2.setNombre("Ana");
        c2.setApellido("Martínez");
        c2.setEdad(28);

        Contribuyente c3 = new Contribuyente();
        c3.setNombre("Pedro");
        c3.setApellido("Sánchez");
        c3.setEdad(40);

        contribuyentesRepository.saveAll(List.of(c1, c2, c3));

        // listo todos
        List<Contribuyente> todos = contribuyentesRepository.findAll();

        // verifico que esten los 3
        assertThat(todos).hasSize(3);
        assertThat(todos).extracting(Contribuyente::getNombre)
                .containsExactlyInAnyOrder("Carlos", "Ana", "Pedro");
    }

    /**
     * test update: modifico un contribuyente existente
     */
    @Test
    void testActualizar_DeberiaModificarContribuyenteExistente() {
        // creo y guardo un contribuyente
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Luis");
        contribuyente.setApellido("Rodríguez");
        contribuyente.setEdad(45);
        Contribuyente guardado = contribuyentesRepository.save(contribuyente);

        // modifico y actualizo
        guardado.setNombre("Luis Alberto");
        guardado.setEdad(46);
        Contribuyente actualizado = contribuyentesRepository.save(guardado);

        // verifico los cambios
        assertThat(actualizado.getId()).isEqualTo(guardado.getId());
        assertThat(actualizado.getNombre()).isEqualTo("Luis Alberto");
        assertThat(actualizado.getEdad()).isEqualTo(46);

        // verifico en la bd
        Optional<Contribuyente> verificado = contribuyentesRepository.findById(guardado.getId());
        assertThat(verificado).isPresent();
        assertThat(verificado.get().getNombre()).isEqualTo("Luis Alberto");
    }

    /**
     * test delete: elimino un contribuyente
     */
    @Test
    void testEliminar_DeberiaEliminarContribuyenteExistente() {
        // creo y guardo
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Elena");
        contribuyente.setApellido("Fernández");
        contribuyente.setEdad(32);
        Contribuyente guardado = contribuyentesRepository.save(contribuyente);
        Long id = guardado.getId();

        // elimino
        contribuyentesRepository.deleteById(id);

        // verifico que ya no existe
        Optional<Contribuyente> buscado = contribuyentesRepository.findById(id);
        assertThat(buscado).isEmpty();
    }

    /**
     * test: verifico existencia de registro
     */
    @Test
    void testExistePorId_DeberiaRetornarTrueSiExiste() {
        // creo un contribuyente
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Roberto");
        contribuyente.setApellido("Gómez");
        contribuyente.setEdad(50);
        Contribuyente guardado = contribuyentesRepository.save(contribuyente);

        // verifico existencia
        assertThat(contribuyentesRepository.existsById(guardado.getId())).isTrue();
        assertThat(contribuyentesRepository.existsById(999L)).isFalse();
    }

    /**
     * test: cuento registros
     */
    @Test
    void testCount_DeberiaContarCorrectamente() {
        // verifico que este vacio
        assertThat(contribuyentesRepository.count()).isEqualTo(0);

        Contribuyente c1 = new Contribuyente();
        c1.setNombre("Test1");
        c1.setApellido("Apellido1");
        c1.setEdad(20);

        Contribuyente c2 = new Contribuyente();
        c2.setNombre("Test2");
        c2.setApellido("Apellido2");
        c2.setEdad(25);

        contribuyentesRepository.saveAll(List.of(c1, c2));

        // verifico el conteo
        assertThat(contribuyentesRepository.count()).isEqualTo(2);
    }

    /**
     * test: verifico que los cambios se persisten en bd
     */
    @Test
    void testTransaccionalidad_CambiosDeberianPersistir() {
        // creo y guardo
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setNombre("Transaccional");
        contribuyente.setApellido("Test");
        contribuyente.setEdad(33);

        Contribuyente guardado = contribuyentesRepository.save(contribuyente);
        contribuyentesRepository.flush(); // fuerzo escritura a bd

        // busco nuevamente para verificar persistencia
        Optional<Contribuyente> recuperado = contribuyentesRepository.findById(guardado.getId());
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getNombre()).isEqualTo("Transaccional");
    }
}
