package es.carlosgs.dwes2526.tarjetas.dto;

import es.carlosgs.dwes2526.tarjetas.validators.CreditCardNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Tarjeta a crear")
public class TarjetaCreateDto {
  //@Pattern(regexp = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}",
  //    message = "El número de la tarjeta debe tener 16 dígitos en grupos de 4 separados por guiones")
  @CreditCardNumber
  @Schema(description = "Número de la tarjeta", example = "1234-5678-1234-5678")
  private final String numero;
  @Pattern(regexp = "\\d{3}", message = "El CVC debe tener 3 dígitos")
  @Schema(description = "Codigo de verificación de la tarjeta", example = "123")
  private final String cvc;
  @Future(message = "La fecha de caducidad debe ser posterior a la fecha actual")
  @Schema(description = "Fecha de caducidad", example = "2025-03-01")
  private final LocalDate fechaCaducidad;
  @NotBlank(message = "El titular no puede estar vacío")
  @Schema(description = "Titular de la tarjeta", example = "Jeromito")
  private final String titular;
  @Schema(description = "Saldo actual", example = "200.0")
  private final Double saldo;
}
