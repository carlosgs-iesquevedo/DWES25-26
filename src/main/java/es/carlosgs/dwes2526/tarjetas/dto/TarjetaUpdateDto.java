package es.carlosgs.dwes2526.tarjetas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TarjetaUpdateDto {
  private final String numero;
  private final String cvc;
  private final LocalDate fechaCaducidad;
  private final String titular;
  private final Double saldo;
}
