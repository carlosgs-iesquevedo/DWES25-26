package es.carlosgs.dwes2526.rest.tarjetas.services;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TarjetasService {
  Page<TarjetaResponseDto> findAll(Optional<String> numero, Optional<String> titular, Optional<Boolean> isDeleted, Pageable pageable);

  TarjetaResponseDto findById(Long id);

  TarjetaResponseDto findByUuid(String uuid);

  Page<TarjetaResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
  TarjetaResponseDto findByUsuarioId(Long usuarioId, Long idTarjeta);

  TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto);
  TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto, Long usuarioId);

  TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto);
  TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto,  Long usuarioId);

  void deleteById(Long id);
  void deleteById(Long id, Long usuarioId);

}
