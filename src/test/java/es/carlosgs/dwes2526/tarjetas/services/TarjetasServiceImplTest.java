package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaBadUuidException;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.mappers.TarjetaMapper;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.tarjetas.repositories.TarjetasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

  private final TarjetaResponseDto tarjetaResponse1 = TarjetaResponseDto.builder()
      .id(1L)
      .numero("1234-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Jose")
      .saldo(100.0)
      .build();

  private final TarjetaResponseDto tarjetaResponse2 = TarjetaResponseDto.builder()
      .id(2L)
      .numero("4321-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Juan")
      .saldo(100.0)
      .build();


  @Mock
  private TarjetasRepository tarjetasRepository;
  @Mock
  private TarjetaMapper tarjetaMapper;
  @InjectMocks
  private TarjetasServiceImpl tarjetasService;
  @Captor // Captor de argumentos
  private ArgumentCaptor<Tarjeta> tarjetaCaptor;

  @Test
  void findAll_ShouldReturnAllTarjetas_WhenNoParametersProvided() {
    // Arrange
    List<Tarjeta> expectedTarjetas = Arrays.asList(tarjeta1, tarjeta2);
    List<TarjetaResponseDto> expectedTarjetaResponses =
        Arrays.asList(tarjetaResponse1, tarjetaResponse2);
    when(tarjetasRepository.findAll()).thenReturn(expectedTarjetas);
    when(tarjetaMapper.toResponseDtoList(anyList())).thenReturn(expectedTarjetaResponses);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(null, null);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, times(1)).findAll();
    verify(tarjetaMapper, times(1)).toResponseDtoList(anyList());
  }

  @Test
  void findAll_ShouldReturnTarjetasByNumero_WhenNumeroParameterProvided() {
    // Arrange
    String numero = "1234-5678-1234-5678";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = List.of(tarjetaResponse1);
    when(tarjetasRepository.findAllByNumero(numero)).thenReturn(expectedTarjetas);
    when(tarjetaMapper.toResponseDtoList(anyList())).thenReturn(expectedTarjetaResponses);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(numero, null);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, times(1)).findAllByNumero(numero);
    verify(tarjetaMapper, times(1)).toResponseDtoList(anyList());
  }

  @Test
  void findAll_ShouldReturnTarjetasByTitular_WhenTitularParameterProvided() {
    // Arrange
    String titular = "Jose";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = List.of(tarjetaResponse1);
    when(tarjetasRepository.findAllByTitular(titular)).thenReturn(expectedTarjetas);
    when(tarjetaMapper.toResponseDtoList(anyList())).thenReturn(expectedTarjetaResponses);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(null, titular);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, times(1)).findAllByTitular(titular);
    verify(tarjetaMapper, times(1)).toResponseDtoList(anyList());
  }

  @Test
  void findAll_ShouldReturnTarjetasByNumeroAndTitular_WhenBothParametersProvided() {
    // Arrange
    String numero = "1234-5678-1234-5678";
    String titular = "Jose";
    List<Tarjeta> expectedTarjetas = List.of(tarjeta1);
    List<TarjetaResponseDto> expectedTarjetaResponses = List.of(tarjetaResponse1);
    when(tarjetasRepository.findAllByNumeroAndTitular(numero, titular)).thenReturn(expectedTarjetas);
    when(tarjetaMapper.toResponseDtoList(anyList())).thenReturn(expectedTarjetaResponses);

    // Act
    List<TarjetaResponseDto> actualTarjetaResponses = tarjetasService.findAll(numero, titular);

    // Assert
    assertIterableEquals(expectedTarjetaResponses, actualTarjetaResponses);

    // Verify
    verify(tarjetasRepository, times(1)).findAllByNumeroAndTitular(numero, titular);
    verify(tarjetaMapper, times(1)).toResponseDtoList(anyList());
  }

  @Test
  void findById_ShouldReturnTarjeta_WhenValidIdProvided() {
    // Arrange
    Long id = 1L;
    TarjetaResponseDto expectedTarjetaResponse = tarjetaResponse1;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(tarjeta1));
    when(tarjetaMapper.toTarjetaResponseDto(any(Tarjeta.class))).thenReturn(expectedTarjetaResponse);

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.findById(id);

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, times(1)).findById(id);
    verify(tarjetaMapper, times(1)).toTarjetaResponseDto(any(Tarjeta.class));
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
    verify(tarjetasRepository, times(1)).findById(id);
  }


  @Test
  void findByUuid_ShouldReturnTarjeta_WhenValidUuidProvided() {
    // Arrange
    UUID expectedUuid = tarjeta1.getUuid();
    TarjetaResponseDto expectedTarjetaResponse = tarjetaResponse1;
    when(tarjetasRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(tarjeta1));
    when(tarjetaMapper.toTarjetaResponseDto(any(Tarjeta.class))).thenReturn(expectedTarjetaResponse);

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.findByUuid(expectedUuid.toString());

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, times(1)).findByUuid(expectedUuid);
    verify(tarjetaMapper, times(1)).toTarjetaResponseDto(any(Tarjeta.class));
  }

  @Test
  void findByUuid_ShouldThrowTarjetaBadUuid_WhenInvalidUuidProvided() {
    // Arrange
    String uuid = "01234567-3ae5-4e50-a606-db397a8772bZ";

    // Act & Assert
    var res = assertThrows(TarjetaBadUuidException.class, () -> tarjetasService.findByUuid(uuid));
    assertEquals("El UUID " + uuid + " no es válido", res.getMessage());

    // Verify
    // verify(tarjetasRepository, times(1)).findByUuid(UUID.fromString(uuid));
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
    TarjetaResponseDto expectedTarjetaResponse = TarjetaResponseDto.builder()
        .id(1L)
        .numero("1111-2222-3333-4444")
        .titular("Ana")
        .cvc("123")
        .saldo(123.0)
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .uuid(UUID.randomUUID())
        .build();

    when(tarjetasRepository.nextId()).thenReturn(1L);
    when(tarjetaMapper.toTarjeta(1L, tarjetaCreateDto)).thenReturn(expectedTarjeta);
    when(tarjetasRepository.save(expectedTarjeta)).thenReturn(expectedTarjeta);
    when(tarjetaMapper.toTarjetaResponseDto(any(Tarjeta.class))).thenReturn(expectedTarjetaResponse);

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.save(tarjetaCreateDto);

    // Assert
    assertEquals(expectedTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, times(1)).nextId();
    verify(tarjetasRepository, times(1)).save(tarjetaCaptor.capture());
    verify(tarjetaMapper, times(1)).toTarjeta(1L, tarjetaCreateDto);
    verify(tarjetaMapper, times(1)).toTarjetaResponseDto(any(Tarjeta.class));
  }

  @Test
  void update_ShouldReturnUpdatedTarjeta_WhenValidIdAndtarjetaUpdateDtoProvided() {
    // Arrange
    Long id = 1L;
    TarjetaUpdateDto tarjetaUpdateDto = TarjetaUpdateDto.builder()
        .saldo(500.0)
        .build();
    Tarjeta existingTarjeta = tarjeta1;
    TarjetaResponseDto existingTarjetaResponse = TarjetaResponseDto.builder()
        .id(1L)
        .numero("1234-5678-1234-5678")
        .cvc("555")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Jose")
        .saldo(500.0)
        .build();

    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(existingTarjeta));
    when(tarjetasRepository.save(existingTarjeta)).thenReturn(existingTarjeta);
    when(tarjetaMapper.toTarjeta(tarjetaUpdateDto, tarjeta1)).thenReturn(existingTarjeta);
    when(tarjetaMapper.toTarjetaResponseDto(any(Tarjeta.class))).thenReturn(existingTarjetaResponse);

    // Act
    TarjetaResponseDto actualTarjetaResponse = tarjetasService.update(id, tarjetaUpdateDto);

    // Assert
    assertEquals(existingTarjetaResponse, actualTarjetaResponse);

    // Verify
    verify(tarjetasRepository, times(1)).findById(id);
    verify(tarjetasRepository, times(1)).save(tarjetaCaptor.capture());
    verify(tarjetaMapper, times(1)).toTarjeta(tarjetaUpdateDto, tarjeta1);
    verify(tarjetaMapper, times(1)).toTarjetaResponseDto(any(Tarjeta.class));
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
    var res = assertThrows(TarjetaNotFoundException.class,
        () -> tarjetasService.update(id, tarjetaUpdateDto));
    assertEquals("Tarjeta con id " + id + " no encontrada", res.getMessage());

    // Verify
    verify(tarjetasRepository, times(0)).save(any(Tarjeta.class));
  }

  @Test
  void deleteById_ShouldDeleteTarjeta_WhenValidIdProvided() {
    // Arrange
    Long id = 1L;
    Tarjeta existingTarjeta = tarjeta1;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.of(existingTarjeta));

    // Act
    tarjetasService.deleteById(id);

    // Assert
    verify(tarjetasRepository, times(1)).deleteById(id);
  }

  @Test
  void deleteById_ShouldThrowTarjetaNotFound_WhenInvalidIdProvided() {
    // Arrange
    Long id = 1L;
    when(tarjetasRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    var res = assertThrows(TarjetaNotFoundException.class, () -> tarjetasService.deleteById(id));
    assertEquals("Tarjeta con id " + id + " no encontrada", res.getMessage());

    // Verify
    verify(tarjetasRepository, times(0)).deleteById(id);
  }
}