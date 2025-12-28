package es.carlosgs.dwes2526.rest.titulares.services;

import es.carlosgs.dwes2526.rest.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.rest.titulares.models.Titular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TitularesService {
  Page<Titular> findAll(Optional<String> nombre,  Optional<Boolean> isDeleted, Pageable pageable);

  Titular findByNombre(String nombre);

  Titular findById(Long id);

  Titular save(TitularRequestDto titularRequestDto);

  Titular update(Long id, TitularRequestDto titularRequestDto);

  void deleteById(Long id);
}
