package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TarjetasService {
  Page<TarjetaResponseDto> findAll(Optional<String> numero, Optional<String> titular, Optional<Boolean> isDeleted, Pageable pageable);

  TarjetaResponseDto findById(Long id);

  TarjetaResponseDto findByUuid(String uuid);

  TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto);

  TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto);

  void deleteById(Long id);

}
