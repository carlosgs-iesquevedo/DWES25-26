package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;

import java.util.List;

public interface TarjetasService {
  List<TarjetaResponseDto> findAll(String numero, String titular);

  TarjetaResponseDto findById(Long id);

  TarjetaResponseDto findByUuid(String uuid);

  TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto);

  TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto);

  void deleteById(Long id);

}
