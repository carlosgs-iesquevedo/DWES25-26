package es.carlosgs.dwes2526.titulares.mappers;

import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.models.Titular;
import org.springframework.stereotype.Component;

@Component
public class TitularesMapper {
  public Titular toTitular(TitularRequestDto dto) {
    return Titular.builder()
        .id(null)
        .nombre(dto.getNombre())
        .build();
  }

  public Titular toTitular(TitularRequestDto dto, Titular titular) {
    return Titular.builder()
        .id(titular.getId())
        .nombre(dto.getNombre() != null ? dto.getNombre() : titular.getNombre())
        .createdAt(titular.getCreatedAt())
        //.updatedAt(LocalDateTime.now())
        .isDeleted(dto.getIsDeleted()  != null ? dto.getIsDeleted() : titular.getIsDeleted())
        .build();
  }
}
