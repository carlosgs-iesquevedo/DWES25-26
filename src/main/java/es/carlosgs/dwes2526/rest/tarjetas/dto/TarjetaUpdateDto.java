package es.carlosgs.dwes2526.rest.tarjetas.dto;

import es.carlosgs.dwes2526.rest.tarjetas.validators.CreditCardNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Tarjeta a actualizar")
public class TarjetaUpdateDto {
  @CreditCardNumber
  @Schema(description = "Número de la tarjeta", example = "1234-5678-1234-5678")
  private final String numero;
  @Pattern(regexp = "\\d{3}", message = "El CVC debe tener 3 dígitos")
  @Schema(description = "Codigo de verificación de la tarjeta", example = "123")
  private final String cvc;
  @Future(message = "La fecha de caducidad debe ser posterior a la fecha actual")
  private final LocalDate fechaCaducidad;
  // Una vez creada la tarjeta, no se puede cambiar el titular
  //private final String titular;
  @Schema(description = "Saldo actual", example = "200.0")
  private final Double saldo;
}
