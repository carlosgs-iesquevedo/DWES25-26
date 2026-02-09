package es.carlosgs.dwes2526.rest.titulares.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.users.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // JPA necesita un constructor vacío
@Entity
@Table(name = "TITULARES")
public class Titular {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false,  length = 20)
  private String nombre;

  @Builder.Default
  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  private LocalDateTime updatedAt =  LocalDateTime.now();

  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  private Boolean isDeleted = false;

  // relación bidireccional de uno a muchos con Tarjeta
  @OneToMany(mappedBy = "titular")
  @JsonIgnoreProperties("titular")
  @ToString.Exclude
  private List<Tarjeta> tarjetas;

  @OneToOne(mappedBy = "titular")
  @ToString.Exclude
  private User usuario;

}
