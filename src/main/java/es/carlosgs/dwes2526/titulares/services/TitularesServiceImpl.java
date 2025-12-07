package es.carlosgs.dwes2526.titulares.services;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.exceptions.TitularConflictException;
import es.carlosgs.dwes2526.titulares.exceptions.TitularNotFoundException;
import es.carlosgs.dwes2526.titulares.mappers.TitularesMapper;
import es.carlosgs.dwes2526.titulares.models.Titular;
import es.carlosgs.dwes2526.titulares.repositories.TitularesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"titulares"})
public class TitularesServiceImpl implements TitularesService {
  private final TitularesRepository titularesRepository;
  private final TitularesMapper titularesMapper;

  @Override
  public Page<Titular> findAll(Optional<String> nombre, Optional<Boolean> isDeleted,  Pageable pageable) {
    log.info("Buscando tarjetas por nombre: {}, isDeleted {}", nombre, isDeleted);
    // Criterio de búsqueda por número
    Specification<Titular> specNombreTitular = (root, query, criteriaBuilder) ->
        nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay nombre, no filtramos

    // Criterio de búsqueda por isDeleted
    Specification<Titular> specIsDeleted = (root, query, criteriaBuilder) ->
        isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    // Combinamos las especificaciones
    Specification<Titular> criterio = Specification.allOf(specNombreTitular, specIsDeleted);
    return titularesRepository.findAll(criterio, pageable);
  }

  @Override
  public Titular findByNombre(String nombre) {
    log.info("Buscando titulares por nombre: {}", nombre);
    return titularesRepository.findByNombreEqualsIgnoreCase(nombre)
        .orElseThrow(() -> new TitularNotFoundException(nombre));
  }

  @Override
  @Cacheable(key = "#id")
  public Titular findById(Long id) {
    log.info("Buscando titular por id: {}", id);
    return titularesRepository.findById(id).orElseThrow(() -> new TitularNotFoundException(id));
  }

  @Override
  @CachePut(key = "#result.id")
  public Titular save(TitularRequestDto titularRequestDto) {
    log.info("Guardando titular: {}", titularRequestDto);
    // No debe existir dos titulares con el mismo nombre
    titularesRepository.findByNombreEqualsIgnoreCase(titularRequestDto.getNombre()).ifPresent(tit -> {
      throw new TitularConflictException("Ya existe un titular con el nombre " + titularRequestDto.getNombre());
    });
    return titularesRepository.save(titularesMapper.toTitular(titularRequestDto));
  }

  @Override
  @CachePut(key = "#result.id")
  public Titular update(Long id, TitularRequestDto titularRequestDto) {
    log.info("Actualizando titular: {}", titularRequestDto);
    Titular titularActual = findById(id);
    // No debe existir dos titulares con el mismo nombre
    titularesRepository.findByNombreEqualsIgnoreCase(titularRequestDto.getNombre()).ifPresent(tit -> {
      if (!tit.getId().equals(id)) {
        throw new TitularConflictException("Ya existe un titular con el nombre " + titularRequestDto.getNombre());
      }
    });
    // Actualizamos los datos
    return titularesRepository.save(titularesMapper.toTitular(titularRequestDto, titularActual));
  }

  @Override
  @CacheEvict(key = "#id")
  @Transactional // Para que se haga todo o nada y no se quede a medias (por el update)
  public void deleteById(Long id) {
    log.info("Borrando titular por id: {}", id);
    Titular titular = findById(id);
    //titularesRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada, no podemos borrar titulares con tarjetas!!!
    // La otra forma es que comprobáramos si hay tarjetas para borrarlas antes
    // tarjetasRepository.updateIsDeletedToTrueById(id);
    if (titularesRepository.existsTarjetaById(id)) {
      String mensaje = "No se puede borrar el titular con id: " + id + " porque tiene tarjetas asociadas";
      log.warn(mensaje);
      throw new TitularConflictException(mensaje);
    } else {
      titularesRepository.deleteById(id);
    }

  }


}
