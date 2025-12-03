package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.titulares.models.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Reseteamos la base de datos para partir de una situación conocida
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
// Vamos a probar el repositorio, pero moqueamos la base de datos JPA
@DataJpaTest
class TarjetasRepositoryTest {

  private final Titular titular1 = Titular.builder().nombre("Jose").build();
  private final Titular titular2 = Titular.builder().nombre("Juan").build();

  private final Tarjeta tarjeta1 = Tarjeta.builder()
      .numero("1234-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular(titular1)
      .saldo(100.0)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
      .build();

  private final Tarjeta tarjeta2 = Tarjeta.builder()
      .numero("4321-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular(titular2)
      .saldo(100.0)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
      .build();

  @Autowired
  private TarjetasRepository repositorio;
  @Autowired
  private TestEntityManager entityManager; // EntityManager para hacer las pruebas

  @BeforeEach
  void setUp() {
    // Vamos a salvar un titular
    entityManager.persist(titular1);
    entityManager.persist(titular2);
    // Vamos a salvar dos tarjetas
    entityManager.persist(tarjeta1);
    entityManager.persist(tarjeta2);
    entityManager.flush();
  }

  @Test
  void findAll() {
    // Act
    List<Tarjeta> tarjetas = repositorio.findAll();

    // Assert
    assertAll("findAll",
        () -> assertNotNull(tarjetas),
        () -> assertEquals(2, tarjetas.size())
    );
  }



  @Test
  void findAllByNumero() {
    // Act
    String numero = "4321-5678-1234-5678";
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Tarjeta> page = repositorio.findByNumero(numero, pageable);

    // Assert
    assertAll("findAllByNumero",
        () -> assertNotNull(page),
        () -> assertFalse(page.isEmpty()),
        () -> assertEquals(1, page.getTotalElements()),
        () -> assertEquals(numero, page.getContent().getFirst().getNumero())
    );
  }


  @Test
  void findAllByTitular() {
    // Act
    String titular = "Jose";
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Tarjeta> page = repositorio.findByTitularContainsIgnoreCase(titular.toLowerCase(), pageable);

    // Assert
    assertAll("findAllByTitular",
        () -> assertNotNull(page),
        () -> assertFalse(page.isEmpty()),
        () -> assertEquals(1, page.getTotalElements()),
        () -> assertEquals(titular, page.getContent().getFirst().getTitular().getNombre())
    );
  }

  @Test
  void findAllByNumeroAndTitular() {
    // Act
    String numero = "4321-5678-1234-5678";
    String titular = "Juan";
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Tarjeta> page = repositorio.findByNumeroAndTitularContainsIgnoreCase(numero, titular.toLowerCase(), pageable);
    // Assert
    assertAll(
        () -> assertNotNull(page),
        () -> assertFalse(page.isEmpty()),
        () -> assertEquals(1, page.getTotalElements()),
        () -> assertEquals(numero, page.getContent().getFirst().getNumero()),
        () -> assertEquals(titular, page.getContent().getFirst().getTitular().getNombre())
    );
  }

  @Test
  void findById_existingId_returnsOptionalWithTarjeta() {
    // Act
    Long id = 1L;
    Optional<Tarjeta> optionalTarjeta = repositorio.findById(id);

    // Assert
    assertAll("findById_existingId_returnsOptionalWithTarjeta",
        () -> assertNotNull(optionalTarjeta),
        () -> assertTrue(optionalTarjeta.isPresent()),
        () -> assertEquals(id, optionalTarjeta.get().getId())
    );
  }

  @Test
  void findById_nonExistingId_returnsEmptyOptional() {
    // Act
    Long id = 4L;
    Optional<Tarjeta> optionalTarjeta = repositorio.findById(id);

    // Assert
    assertAll("findById_nonExistingId_returnsEmptyOptional",
        () -> assertNotNull(optionalTarjeta),
        () -> assertTrue(optionalTarjeta.isEmpty())
    );
  }

  @Test
  void findByUuid_existingUuid_returnsOptionalWithTarjeta() {
    // Act
    UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
    Optional<Tarjeta> optionalTarjeta = repositorio.findByUuid(uuid);

    // Assert
    assertAll("findByUuid_existingUuid_returnsOptionalWithTarjeta",
        () -> assertNotNull(optionalTarjeta),
        () -> assertTrue(optionalTarjeta.isPresent()),
        () -> assertEquals(uuid, optionalTarjeta.get().getUuid())
    );
  }

  @Test
  void findByUuid_nonExistingUuid_returnsEmptyOptional() {
    // Act
    UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
    Optional<Tarjeta> optionalTarjeta = repositorio.findByUuid(uuid);

    // Assert
    assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
        () -> assertNotNull(optionalTarjeta),
        () -> assertTrue(optionalTarjeta.isEmpty())
    );
  }


  @Test
  void existsById_existingId_returnsTrue() {
    // Act
    Long id = 1L;
    boolean exists = repositorio.existsById(id);

    // Assert
    assertTrue(exists);
  }

  @Test
  void existsById_nonExistingId_returnsFalse() {
    // Act
    Long id = 4L;
    boolean exists = repositorio.existsById(id);

    // Assert
    assertFalse(exists);
  }


  @Test
  void existsByUuid_existingUuid_returnsTrue() {
    // Act
    UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
    boolean exists = repositorio.existsByUuid(uuid);

    // Assert
    assertTrue(exists);
  }

  @Test
  void existsByUuid_nonExistingUuid_returnsFalse() {
    // Act
    UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
    boolean exists = repositorio.existsByUuid(uuid);

    // Assert
    assertFalse(exists);
  }

  @Test
  void save_notExists() {
    // Arrange
    Tarjeta tarjeta = Tarjeta.builder()
        .numero("2222-5678-1234-5678")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2029,12,31))
        .titular(titular1)
        .saldo(300.0)
        .build();

    // Act
    Tarjeta savedTarjeta = repositorio.save(tarjeta);
    var all = repositorio.findAll();

    // Assert
    // Comprueba que ha creado una nueva tarjeta
    assertAll("save",
        () -> assertNotNull(savedTarjeta),
        () -> assertEquals(tarjeta, savedTarjeta),
        () -> assertEquals(3, all.size())
    );

  }

  @Test
  void save_butExists() {
    // Arrange
    // Una tarjeta con un id de tarjeta existente
    Long id = 1L;
    Tarjeta tarjetaExistente = Tarjeta.builder()
        .id(id)
        .numero("1234-5678-1234-5678")
        .cvc("555")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular(titular1)
        .saldo(200.0)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
        .build();

    //Act
    Tarjeta savedTarjeta = repositorio.save(tarjetaExistente);
    var all = repositorio.findAll();

    // Assert
    // Comprueba que actualiza una tarjeta existente
    assertAll("save",
        () -> assertNotNull(savedTarjeta),
      //  () -> assertEquals(tarjetaExistente, savedTarjeta),
        () -> assertTrue(repositorio.existsById(id)),
        () -> assertTrue(all.size() >= 2)
    );
  }

  @Test
  void deleteById_existingId() {
    // Act
    Long id = 1L;
    repositorio.deleteById(id);
    var all = repositorio.findAll();

    // Assert
    assertAll("deleteById_existingId",
        () -> assertEquals(1, all.size()),
        () -> assertFalse(repositorio.existsById(id))
    );
  }


  @Test
  void deleteByUuid_existingUuid() {
    // Act
    UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
    repositorio.deleteByUuid(uuid);
    var all = repositorio.findAll();

    // Assert
    assertAll("deleteByUuid_existingUuid",
        () -> assertEquals(1, all.size()),
        () -> assertFalse(repositorio.existsByUuid(uuid))
    );
  }

}