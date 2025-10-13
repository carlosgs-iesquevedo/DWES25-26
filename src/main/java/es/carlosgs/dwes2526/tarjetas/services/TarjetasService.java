package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;

import java.util.List;

public interface TarjetasService {
  List<Tarjeta> findAll(String numero, String titular);

  Tarjeta findById(Long id);

  Tarjeta findbyUuid(String uuid);

  TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto);

  TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto);

  void deleteById(Long id);

}
