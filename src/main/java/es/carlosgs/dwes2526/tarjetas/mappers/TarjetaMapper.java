package es.carlosgs.dwes2526.tarjetas.mappers;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.titulares.models.Titular;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TarjetaMapper {
  public Tarjeta toTarjeta(TarjetaCreateDto tarjetaCreateDto, Titular titular) {
    return Tarjeta.builder()
        .id(null)
        .numero(tarjetaCreateDto.getNumero())
        .cvc(tarjetaCreateDto.getCvc())
        .fechaCaducidad(tarjetaCreateDto.getFechaCaducidad())
        .titular(titular)
        .saldo(tarjetaCreateDto.getSaldo())
        .uuid(UUID.randomUUID())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public Tarjeta toTarjeta(TarjetaUpdateDto tarjetaUpdateDto, Tarjeta tarjeta) {
    return Tarjeta.builder()
        .id(tarjeta.getId())
        .numero(tarjetaUpdateDto.getNumero() != null ? tarjetaUpdateDto.getNumero() : tarjeta.getNumero())
        .cvc(tarjetaUpdateDto.getCvc() != null ? tarjetaUpdateDto.getCvc() : tarjeta.getCvc())
        .fechaCaducidad(tarjetaUpdateDto.getFechaCaducidad() != null ? tarjetaUpdateDto.getFechaCaducidad() : tarjeta.getFechaCaducidad())
        // Una vez creada la tarjeta, no se puede cambiar el titular
        .titular(tarjeta.getTitular())
        .saldo(tarjetaUpdateDto.getSaldo() != null ? tarjetaUpdateDto.getSaldo() : tarjeta.getSaldo())
        .createdAt(tarjeta.getCreatedAt())
        // no tenemos en cuenta este campo porque hemos definido valores por defecto en la entidad
        // y automatismos en la base de datos
        // .updatedAt(LocalDateTime.now())
        .uuid(tarjeta.getUuid())
        .build();
  }

  public TarjetaResponseDto toTarjetaResponseDto(Tarjeta tarjeta) {
    return TarjetaResponseDto.builder()
        .id(tarjeta.getId())
        .numero(tarjeta.getNumero())
        .cvc(tarjeta.getCvc())
        .fechaCaducidad(tarjeta.getFechaCaducidad())
        .titular(tarjeta.getTitular().getNombre())
        .saldo(tarjeta.getSaldo())
        .createdAt(tarjeta.getCreatedAt())
        .updatedAt(tarjeta.getUpdatedAt())
        .uuid(tarjeta.getUuid())
        .build();
  }

  // Mapeamos de modelo a DTO (lista)
  public List<TarjetaResponseDto> toResponseDtoList(List<Tarjeta> tarjetas) {
    return tarjetas.stream()
        .map(this::toTarjetaResponseDto)
        .toList();
  }

}
