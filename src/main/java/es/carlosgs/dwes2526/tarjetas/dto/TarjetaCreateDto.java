package es.carlosgs.dwes2526.tarjetas.dto;

import es.carlosgs.dwes2526.tarjetas.validators.CreditCardNumber;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class TarjetaCreateDto {
  //@Pattern(regexp = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}",
  //    message = "El número de la tarjeta debe tener 16 dígitos en grupos de 4 separados por guiones")
  @CreditCardNumber
  private final String numero;
  @Pattern(regexp = "\\d{3}", message = "El CVC debe tener 3 dígitos")
  private final String cvc;
  @Future(message = "La fecha de caducidad debe ser posterior a la fecha actual")
  private final LocalDate fechaCaducidad;
  @NotBlank(message = "El titular no puede estar vacío")
  private final String titular;
  private final Double saldo;
}
