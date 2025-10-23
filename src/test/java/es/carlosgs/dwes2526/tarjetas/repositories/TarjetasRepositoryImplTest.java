package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TarjetasRepositoryImplTest {

  private final Tarjeta tarjeta1 = Tarjeta.builder()
      .id(1L)
      .numero("1234-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Jose")
      .saldo(100.0)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
      .build();

  private final Tarjeta tarjeta2 = Tarjeta.builder()
      .id(2L)
      .numero("4321-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Juan")
      .saldo(100.0)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
      .build();

  private TarjetasRepositoryImpl repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new TarjetasRepositoryImpl();
    repositorio.save(tarjeta1);
    repositorio.save(tarjeta2);
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
    List<Tarjeta> tarjetas = repositorio.findAllByNumero(numero);

    // Assert
    assertAll("findAllByNumero",
        () -> assertNotNull(tarjetas),
        () -> assertEquals(1, tarjetas.size()),
        () -> assertEquals(numero, tarjetas.getFirst().getNumero())
    );
  }

  @Test
  void findAllByTitular() {
    // Act
    String titular = "Jose";
    List<Tarjeta> tarjetas = repositorio.findAllByTitular(titular);

    // Assert
    assertAll("findAllByNumero",
        () -> assertNotNull(tarjetas),
        () -> assertEquals(1, tarjetas.size()),
        () -> assertEquals(titular, tarjetas.getFirst().getTitular())
    );
  }

  @Test
  void findAllByNumeroAndTitular() {
    // Act
    String numero = "4321-5678-1234-5678";
    String titular = "Juan";
    List<Tarjeta> tarjetas = repositorio.findAllByNumeroAndTitular(numero, titular);
    // Assert
    assertAll(
        () -> assertNotNull(tarjetas),
        () -> assertEquals(1, tarjetas.size()),
        () -> assertEquals(numero, tarjetas.getFirst().getNumero()),
        () -> assertEquals(titular, tarjetas.getFirst().getTitular())
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
        .id(3L)
        .numero("2222-5678-1234-5678")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2029,12,31))
        .titular("María")
        .saldo(300.0)
        .build();

    // Act
    Tarjeta savedTarjeta = repositorio.save(tarjeta);
    var all = repositorio.findAll();

    // Assert
    assertAll("save",
        () -> assertNotNull(savedTarjeta),
        () -> assertEquals(tarjeta, savedTarjeta),
        () -> assertEquals(3, all.size())
    );

  }

  @Test
  void save_butExists() {
    // Arrange
    Tarjeta tarjeta = Tarjeta.builder().id(1L).build();

    // Act
    Tarjeta savedTarjeta = repositorio.save(tarjeta);
    var all = repositorio.findAll();

    // Assert
    assertAll("save",
        () -> assertNotNull(savedTarjeta),
        () -> assertEquals(tarjeta, savedTarjeta),
        () -> assertEquals(2, all.size())
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

  @Test
  void nextId() {
    // Act
    Long nextId = repositorio.nextId();
    var all = repositorio.findAll();

    // Assert
    assertAll("nextId",
        () -> assertEquals(3L, nextId),
        () -> assertEquals(2, all.size())
    );
  }
}