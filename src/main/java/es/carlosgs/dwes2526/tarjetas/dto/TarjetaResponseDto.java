package es.carlosgs.dwes2526.tarjetas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Tarjeta a devolver como respuesta")
public class TarjetaResponseDto {
  @Schema(description = "Identificador de la tarjeta", example = "1")
  private Long id;
  @Schema(description = "Número de la tarjeta", example = "1234-5678-1234-5678")
  private String numero;
  @Schema(description = "Codigo de verificación de la tarjeta", example = "123")
  private String cvc;
  @Schema(description = "Fecha de caducidad", example = "2025-03-01")
  private LocalDate fechaCaducidad;
  @Schema(description = "Titular de la tarjeta", example = "Jeromito")
  private String titular;
  @Schema(description = "Saldo actual", example = "200.0")
  private Double saldo;
  @Schema(description = "Fecha de creación de la tarjeta", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime createdAt;
  @Schema(description = "Fecha de actualización de la tarjeta", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime updatedAt;
  @Schema(description = "UUID de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;
}
