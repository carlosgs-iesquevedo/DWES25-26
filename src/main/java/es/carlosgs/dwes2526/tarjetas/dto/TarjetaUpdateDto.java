package es.carlosgs.dwes2526.tarjetas.dto;

import es.carlosgs.dwes2526.tarjetas.validators.CreditCardNumber;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TarjetaUpdateDto {
  @CreditCardNumber
  private final String numero;
  @Pattern(regexp = "\\d{3}", message = "El CVC debe tener 3 dígitos")
  private final String cvc;
  @Future(message = "La fecha de caducidad debe ser posterior a la fecha actual")
  private final LocalDate fechaCaducidad;
  // Una vez creada la tarjeta, no se puede cambiar el titular
  //private final String titular;
  private final Double saldo;
}
