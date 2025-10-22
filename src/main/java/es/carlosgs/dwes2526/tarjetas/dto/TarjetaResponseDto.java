package es.carlosgs.dwes2526.tarjetas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TarjetaResponseDto {
  private Long id;

  private String numero;
  private String cvc;
  private LocalDate fechaCaducidad;
  private String titular;
  private Double saldo;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private UUID uuid;
}
