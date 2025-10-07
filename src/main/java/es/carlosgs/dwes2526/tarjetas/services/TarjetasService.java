package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;

import java.util.List;

public interface TarjetasService {
  List<Tarjeta> findAll(String numero, String titular);

  Tarjeta findById(Long id);

  Tarjeta findbyUuid(String uuid);

  Tarjeta save(Tarjeta tarjeta);

  Tarjeta update(Long id, Tarjeta tarjeta);

  void deleteById(Long id);

}
