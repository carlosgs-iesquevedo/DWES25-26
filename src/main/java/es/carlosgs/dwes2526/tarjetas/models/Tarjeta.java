package es.carlosgs.dwes2526.tarjetas.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Tarjeta {
  private final Long id;

  private final String numero;
  private final String cvc;
  private final LocalDate fechaCaducidad;
  private final String titular;
  private final Double saldo;

  private final LocalDateTime createdAt = LocalDateTime.now();
  private final LocalDateTime updatedAt = LocalDateTime.now();
  private final UUID uuid = UUID.randomUUID();
}
