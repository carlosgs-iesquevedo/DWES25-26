package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaBadUuidException;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.mappers.TarjetaMapper;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.tarjetas.repositories.TarjetasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integra Mockito con JUnit5 para poder usar mocks, espías y capturadores en los tests
@ExtendWith(MockitoExtension.class)
class TarjetasServiceImplTest {

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

  private TarjetaResponseDto tarjetaResponse1;

  // usamos el repositorio totalmente simulado
  @Mock
  private TarjetasRepository tarjetasRepository;
  // usamos el mapper real aunque en modo espía que nos permite simular algunas partes del mismo
  @Spy
  private TarjetaMapper tarjetaMapper;
  // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
  @InjectMocks
  private TarjetasServiceImpl tarjetasService;
  // Capturador de argumentos
  @Captor
  private ArgumentCaptor<Tarjeta> tarjetaCaptor;

  @BeforeEach
  void setUp() {
    tarjetaResponse1 = tarjetaMapper.toTarjetaResponseDto(tarjeta1);
    // Quizá no la necesitemos
    // tarjetaResponse2 = tarjetaMapper.toTarjetaResponseDto(tarjeta2);
  }

  @Test
  void findAll_ShouldReturnAllTarjetas_WhenNoParametersProvided() {
    // Arrange
    List<Tarjeta> expectedTarjetas = Arrays.asList(tarjeta1, tarjeta2);
    List<TarjetaResponseDto> expectedTarjetaResponses = tarjetaMapper.toResponseDtoList(expectedTarjetas);
    when(tarjetasRepository.findAll()).thenReturn(expectedTarjetas);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(null, null);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    // verifica que findAll() se ejecuta una vez
    verify(tarjetasRepository, times(1)).findAll();
  }

  @Test
  void findAll_ShouldReturnTarjetasByNumero_WhenNumeroParameterProvided() {
    // Arrange
    String numero = "1234-5678-1234-5678";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = tarjetaMapper.toResponseDtoList(expectedTarjetas);
    when(tarjetasRepository.findAllByNumero(numero)).thenReturn(expectedTarjetas);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(numero, null);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    // Verifica que solo se ejecuta este método
    verify(tarjetasRepository, only()).findAllByNumero(numero);
  }

  @Test
  void findAll_ShouldReturnTarjetasByTitular_WhenTitularParameterProvided() {
    // Arrange
    String titular = "Jose";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = tarjetaMapper.toResponseDtoList(expectedTarjetas);
    when(tarjetasRepository.findAllByTitular(titular)).thenReturn(expectedTarjetas);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(null, titular);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, only()).findAllByTitular(titular);
  }

  @Test
  void findAll_ShouldReturnTarjetasByNumeroAndTitular_WhenBothParametersProvided() {
    // Arrange
    String numero = "1234-5678-1234-5678";
    String titular = "Jose";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = tarjetaMapper.toResponseDtoList(expectedTarjetas);
    when(tarjetasRepository.findAllByNumeroAndTitular(numero, titular)).thenReturn(expectedTarjetas);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(numero, titular);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, only()).findAllByNumeroAndTitular(numero, titular);
  }

  @Test
  void findById_ShouldReturnTarjeta_WhenValidIdProvided() {
    // Arrange
    Long id = 1L;
    TarjetaResponseDto expectedTarjetaResponse = tarjetaResponse1;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(tarjeta1));

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.findById(id);

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, only()).findById(id);
  }

  @Test
  void findById_ShouldThrowTarjetaNotFound_WhenInvalidIdProvided() {
    // Arrange
    Long id = 1L;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    var res = assertThrows(TarjetaNotFoundException.class, () -> tarjetasService.findById(id));
    assertEquals("Tarjeta con id " + id + " no encontrada", res.getMessage());

    // Verify
    // verifica que se ejecuta el método
    verify(tarjetasRepository).findById(id);
  }


  @Test
  void findByUuid_ShouldReturnTarjeta_WhenValidUuidProvided() {
    // Arrange
    UUID expectedUuid = tarjeta1.getUuid();
    TarjetaResponseDto expectedTarjetaResponse = tarjetaResponse1;
    when(tarjetasRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(tarjeta1));

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.findByUuid(expectedUuid.toString());

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, only()).findByUuid(expectedUuid);
  }

  @Test
  void findByUuid_ShouldThrowTarjetaBadUuid_WhenInvalidUuidProvided() {
    // Arrange
    //String uuid = "3a31d097-23cf-4b8d-989a-96e380cc996b";
    String uuid = "1234";
    // Act & Assert
    var res = assertThrows(TarjetaBadUuidException.class, () -> tarjetasService.findByUuid(uuid));
    assertEquals("El UUID " + uuid + " no es válido", res.getMessage());

    // Verify
    // verifica que no se ha ejecutado
    verify(tarjetasRepository, never()).findByUuid(any());
  }

  @Test
  void save_ShouldReturnSavedTarjeta_WhenValidTarjetaCreateDtoProvided() {
    // Arrange
    TarjetaCreateDto tarjetaCreateDto = TarjetaCreateDto.builder()
        .numero("1111-2222-3333-4444")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Ana")
        .saldo(123.0)
        .build();
    Tarjeta expectedTarjeta = Tarjeta.builder()
        .id(1L)
        .numero("1111-2222-3333-4444")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Ana")
        .saldo(123.0)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .uuid(UUID.randomUUID())
        .build();
    TarjetaResponseDto expectedTarjetaResponse = tarjetaMapper.toTarjetaResponseDto(expectedTarjeta);

    when(tarjetasRepository.nextId()).thenReturn(1L);
    when(tarjetasRepository.save(any(Tarjeta.class))).thenReturn(expectedTarjeta);

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.save(tarjetaCreateDto);

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository).nextId();
    verify(tarjetasRepository).save(tarjetaCaptor.capture());

    Tarjeta tarjetaCaptured = tarjetaCaptor.getValue();
    assertEquals(expectedTarjeta.getNumero(), tarjetaCaptured.getNumero());
    // equivalente con AsssertJ en lugar de JUnit
    //assertThat(tarjetaCaptured.getNumero()).isEqualTo(expectedTarjeta.getNumero());


  }

  @Test
  void update_ShouldReturnUpdatedTarjeta_WhenValidIdAndtarjetaUpdateDtoProvided() {
    // Arrange
    Long id = 1L;
    Double saldo = 500.0;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(tarjeta1));

    TarjetaUpdateDto tarjetaUpdateDto = TarjetaUpdateDto.builder()
        .saldo(saldo)
        .build();
    Tarjeta tarjetaUpdate = tarjetaMapper.toTarjeta(tarjetaUpdateDto,tarjeta1);
    when(tarjetasRepository.save(any(Tarjeta.class))).thenReturn(tarjetaUpdate);

    tarjetaResponse1.setSaldo(saldo);
    TarjetaResponseDto expectedTarjetaResponse = tarjetaResponse1;

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.update(id, tarjetaUpdateDto);

    // Assert
    // con Junit da error
    //assertEquals(expectedTarjetaResponse, actualTarjetaResponse);
    // con AssertJ podemos excluir algún campo
    assertThat(actualTarjetaResponse)
        .usingRecursiveComparison()
        .ignoringFields("updatedAt")
        .isEqualTo(expectedTarjetaResponse);

    // Verify
    verify(tarjetasRepository).findById(id);
    verify(tarjetasRepository).save(any());
  }

  @Test
  void update_ShouldThrowTarjetaNotFound_WhenInvalidIdProvided() {
    // Arrange
    Long id = 1L;
    TarjetaUpdateDto tarjetaUpdateDto = TarjetaUpdateDto.builder()
        .saldo(500.0)
        .build();
    when(tarjetasRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    // con AssertJ
    assertThatThrownBy(
        () -> tarjetasService.update(id, tarjetaUpdateDto))
        .isInstanceOf(TarjetaNotFoundException.class)
        .hasMessage("Tarjeta con id " + id + " no encontrada");
    // con JUnit
    //var res = assertThrows(TarjetaNotFoundException.class,
    //    () -> tarjetasService.update(id, tarjetaUpdateDto));
    //assertEquals("Tarjeta con id " + id + " no encontrada", res.getMessage());

    // Verify
    verify(tarjetasRepository).findById(id);
    verify(tarjetasRepository, never()).save(any());
  }

  @Test
  void deleteById_ShouldDeleteTarjeta_WhenValidIdProvided() {
    // Arrange
    Long id = 1L;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(tarjeta1));

    // Act
    // con AssertJ
    assertThatCode(() -> tarjetasService.deleteById(id))
        .doesNotThrowAnyException();

    // Assert
    verify(tarjetasRepository).deleteById(id);
  }

  @Test
  void deleteById_ShouldThrowTarjetaNotFound_WhenInvalidIdProvided() {
    // Arrange
    Long id = 1L;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    // con JUnit
    //var res = assertThrows(TarjetaNotFoundException.class, () -> tarjetasService.deleteById(id));
    //assertEquals("Tarjeta con id " + id + " no encontrada", res.getMessage());
    // El equivalente con AssertJ
    assertThatThrownBy(() -> tarjetasService.deleteById(id))
        .isInstanceOf(TarjetaNotFoundException.class)
        .hasMessage("Tarjeta con id " + id + " no encontrada");

    // Verify
    verify(tarjetasRepository, never()).deleteById(id);
  }
}