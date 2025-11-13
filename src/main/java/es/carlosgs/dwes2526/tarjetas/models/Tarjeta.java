package es.carlosgs.dwes2526.tarjetas.models;

import es.carlosgs.dwes2526.titulares.models.Titular;
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
public class Tarjeta {
  @Id // Indicamos que es el ID de la tabla
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicamos que es autoincremental y por el script de datos
  private Long id;
  @Column(nullable = false, length = 19)
  private String numero;
  @Column(nullable = false, length = 3)
  private String cvc;
  @Column(nullable = false)
  private LocalDate fechaCaducidad;
  @Column(nullable = false)
  private Double saldo;
  @Builder.Default
  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  private LocalDateTime updatedAt =  LocalDateTime.now();
  @Column(unique = true, updatable = false, nullable = false)
  @Builder.Default
  private UUID uuid = UUID.randomUUID();

  // nueva columna
  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  private Boolean isDeleted = false;

  // Relación con titulat, muchas tarjetas pueden tener un titular
  @ManyToOne
  @JoinColumn(name = "titular_id") // Así se va a llamar en la BD
  private Titular titular;
}
