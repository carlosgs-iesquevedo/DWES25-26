package es.carlosgs.dwes2526.tarjetas.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TarjetaResponseDto {
  private final Long id;

  private final String numero;
  private final String cvc;
  private final LocalDate fechaCaducidad;
  private final String titular;
  private final Double saldo;

  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
  private final UUID uuid;
}
