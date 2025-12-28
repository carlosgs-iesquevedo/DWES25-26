package es.carlosgs.dwes2526.rest.titulares.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class TitularRequestDto {
  @NotBlank(message = "El titular no puede estar vacío")
  @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
  private final String nombre;
  private final Boolean isDeleted;
}
