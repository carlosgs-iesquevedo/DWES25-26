package es.carlosgs.dwes2526.tarjetas.mappers;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaMapperTest {

  // Inyectamos el mapper
  private final TarjetaMapper tarjetaMapper = new TarjetaMapper();

  @Test
  void toTarjeta_create() {
    // Arrange
    Long id = 1L;
    TarjetaCreateDto tarjetaCreateDto = TarjetaCreateDto.builder()
        .numero("1234-5678-1234-5678")
        .cvc("555")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Jose")
        .saldo(100.0)
        .build();
    // Act
    var res = tarjetaMapper.toTarjeta(id, tarjetaCreateDto);

    // Assert
    assertAll(
        () -> assertEquals(id, res.getId()),
        () -> assertEquals(tarjetaCreateDto.getNumero(), res.getNumero()),
        () -> assertEquals(tarjetaCreateDto.getCvc(), res.getCvc()),
        () -> assertEquals(tarjetaCreateDto.getFechaCaducidad(), res.getFechaCaducidad()),
        () -> assertEquals(tarjetaCreateDto.getTitular(), res.getTitular()),
        () -> assertEquals(tarjetaCreateDto.getSaldo(), res.getSaldo())
    );
  }

  @Test
  void toTarjeta_update() {
    // Arrange
    Long id = 1L;
    TarjetaUpdateDto tarjetaUpdateDto = TarjetaUpdateDto.builder()
        .numero("1234-5678-1234-5678")
        .cvc("555")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .saldo(100.0)
        .build();

    Tarjeta tarjeta = Tarjeta.builder()
        .id(id)
        .numero(tarjetaUpdateDto.getNumero())
        .cvc(tarjetaUpdateDto.getCvc())
        .fechaCaducidad(tarjetaUpdateDto.getFechaCaducidad())
        .saldo(tarjetaUpdateDto.getSaldo())
        .build();
    // Act
    var res = tarjetaMapper.toTarjeta(tarjetaUpdateDto, tarjeta);
    // Assert
    assertAll(
        () -> assertEquals(id, res.getId()),
        () -> assertEquals(tarjetaUpdateDto.getNumero(), res.getNumero()),
        () -> assertEquals(tarjetaUpdateDto.getCvc(), res.getCvc()),
        () -> assertEquals(tarjetaUpdateDto.getFechaCaducidad(), res.getFechaCaducidad()),
        () -> assertEquals(tarjetaUpdateDto.getSaldo(), res.getSaldo())
    );
  }

  @Test
  void toTarjetaResponseDto() {
    // Arrange
    Tarjeta tarjeta = Tarjeta.builder()
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
    // Act
    var res = tarjetaMapper.toTarjetaResponseDto(tarjeta);
    // Assert
    assertAll(
        () -> assertEquals(tarjeta.getId(), res.getId()),
        () -> assertEquals(tarjeta.getNumero(), res.getNumero()),
        () -> assertEquals(tarjeta.getCvc(), res.getCvc()),
        () -> assertEquals(tarjeta.getFechaCaducidad(), res.getFechaCaducidad()),
        () -> assertEquals(tarjeta.getTitular(), res.getTitular()),
        () -> assertEquals(tarjeta.getSaldo(), res.getSaldo())
    );
  }
}