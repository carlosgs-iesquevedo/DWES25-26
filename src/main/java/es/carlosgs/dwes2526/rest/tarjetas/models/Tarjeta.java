package es.carlosgs.dwes2526.rest.tarjetas.models;

import es.carlosgs.dwes2526.rest.titulares.models.Titular;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // JPA necesita un constructor vacío
@Entity
@Table(name = "TARJETAS")
@Schema(name = "Tarjetas") // Para indicar el nombre de la tabla en la documentación
public class Tarjeta {
  @Id // Indicamos que es el ID de la tabla
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicamos que es autoincremental y por el script de datos
  @Schema(description = "Identificador de la tarjeta", example = "1")
  private Long id;
  @Column(nullable = false, length = 19)
  @Schema(description = "Número de la tarjeta", example = "1234-5678-1234-5678")
  private String numero;
  @Column(nullable = false, length = 3)
  @Schema(description = "Codigo de verificación de la tarjeta", example = "123")
  private String cvc;
  @Column(nullable = false)
  @Schema(description = "Fecha de caducidad", example = "2025-03-01")
  private LocalDate fechaCaducidad;
  @Column(nullable = false)
  @Schema(description = "Saldo actual", example = "200.0")
  private Double saldo;
  @Builder.Default
  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Schema(description = "Fecha de creación de la tarjeta", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  @Schema(description = "Fecha de actualización de la tarjeta", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime updatedAt =  LocalDateTime.now();
  @Column(unique = true, updatable = false, nullable = false)
  @Builder.Default
  @Schema(description = "UUID de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid = UUID.randomUUID();

  // nueva columna
  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  @Schema(description = "Si la tarjeta está eliminada", example = "false")
  private Boolean isDeleted = false;

  // Relación con titular, muchas tarjetas pueden tener un titular
  @ManyToOne
  @JoinColumn(name = "titular_id") // Así se va a llamar en la BD
  @Schema(description = "Titular de la tarjeta", example = "Jeromito")
  private Titular titular;
}
