package es.carlosgs.dwes2526.tarjetas.mappers;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TarjetaMapper {
  public Tarjeta toTarjeta(Long id, TarjetaCreateDto tarjetaCreateDto) {
    return new Tarjeta(
        id,
        tarjetaCreateDto.getNumero(),
        tarjetaCreateDto.getCvc(),
        tarjetaCreateDto.getFechaCaducidad(),
        tarjetaCreateDto.getTitular(),
        tarjetaCreateDto.getSaldo(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        UUID.randomUUID()
    );
  }

  public Tarjeta toTarjeta(TarjetaUpdateDto tarjetaUpdateDto, Tarjeta tarjeta) {
    return new Tarjeta(
        tarjeta.getId(),
        tarjetaUpdateDto.getNumero() != null ? tarjetaUpdateDto.getNumero() : tarjeta.getNumero(),
        tarjetaUpdateDto.getCvc() != null ? tarjetaUpdateDto.getCvc() : tarjeta.getCvc(),
        tarjetaUpdateDto.getFechaCaducidad() != null ? tarjetaUpdateDto.getFechaCaducidad() : tarjeta.getFechaCaducidad(),
        // Una vez creada la tarjeta, no se puede cambiar el titular
        //tarjetaUpdateDto.getTitular() != null ? tarjetaUpdateDto.getTitular() : tarjeta.getTitular(),
        tarjeta.getTitular(),
        tarjetaUpdateDto.getSaldo() != null ? tarjetaUpdateDto.getSaldo() : tarjeta.getSaldo(),
        tarjeta.getCreatedAt(),
        LocalDateTime.now(),
        tarjeta.getUuid()
    );
  }

  public TarjetaResponseDto toTarjetaResponseDto(Tarjeta tarjeta) {
    return new TarjetaResponseDto(
        tarjeta.getId(),
        tarjeta.getNumero(),
        tarjeta.getCvc(),
        tarjeta.getFechaCaducidad(),
        tarjeta.getTitular(),
        tarjeta.getSaldo(),
        tarjeta.getCreatedAt(),
        tarjeta.getUpdatedAt(),
        tarjeta.getUuid()
    );
  }

  // Mapeamos de modelo a DTO (lista)
  public List<TarjetaResponseDto> toResponseDtoList(List<Tarjeta> tarjetas) {
    return tarjetas.stream()
        .map(this::toTarjetaResponseDto)
        .toList();
  }

}
