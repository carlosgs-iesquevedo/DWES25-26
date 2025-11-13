package es.carlosgs.dwes2526.titulares.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class TitularRequestDto {
  @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
  private final String nombre;
  private final Boolean isDeleted;
}
