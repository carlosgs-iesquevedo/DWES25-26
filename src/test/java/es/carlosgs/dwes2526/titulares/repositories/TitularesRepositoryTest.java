package es.carlosgs.dwes2526.titulares.repositories;

import es.carlosgs.dwes2526.titulares.models.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Reseteamos la base de datos para partir de una situación conocida
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class TitularesRepositoryTest {

  private final Titular titular = Titular.builder().nombre("Jose").build();

  @Autowired
  private TitularesRepository repositorio;
  @Autowired
  private TestEntityManager entityManager; // EntityManager para hacer las pruebas

  @BeforeEach
  void setUp() {
    // Insertamos un titular antes de cada test
    entityManager.persist(titular);
    entityManager.flush();
  }

  @Test
  void findAll() {
    // Act
    List<Titular> titulares = repositorio.findAll();

    // Assert
    assertAll("findAll",
        () -> assertNotNull(titulares),
        () -> assertFalse(titulares.isEmpty())
    );
  }

  @Test
  void findByNombre() {
    // Act
    List<Titular> titulares = repositorio.findByNombreContainingIgnoreCase("Jose");

    // Assert
    assertAll("findAllByNombre",
        () -> assertNotNull(titulares),
        () -> assertFalse(titulares.isEmpty()),
        () -> assertEquals("Jose", titulares.get(0).getNombre())
    );
  }

  @Test
  void findById() {
    // Act
    Titular titular = repositorio.findById(1L).orElse(null);

    // Assert
    assertAll("findById",
        () -> assertNotNull(titular),
        () -> assertEquals("Jose", titular.getNombre())
    );
  }

  @Test
  void findByIdNotFound() {
    // Act
    Titular titular = repositorio.findById(100L).orElse(null);

    // Assert
    assertNull(titular);
  }

  @Test
  void save() {
    // Act
    Titular titular = repositorio.save(Titular.builder().nombre("Pepe").build());

    // Assert
    assertAll("save",
        () -> assertNotNull(titular),
        () -> assertEquals("Pepe", titular.getNombre())
    );
  }

  @Test
  void update() {
    // Act
    var titularExistente = repositorio.findById(1L).orElse(null);
    Titular titularActualizar = Titular.builder()
        .id(titularExistente.getId())
        .nombre("Pepe").build();
    Titular titularActualizado = repositorio.save(titularActualizar);

    // Assert
    assertAll("update",
        () -> assertNotNull(titularActualizado),
        () -> assertEquals("Pepe", titularActualizado.getNombre())
    );
  }

  @Test
  void delete() {
    // Act
    var titularBorrar = repositorio.findById(1L).orElse(null);
    repositorio.delete(titularBorrar);
    Titular titularBorrado = repositorio.findById(1L).orElse(null);

    // Assert
    assertNull(titularBorrado);
  }

}