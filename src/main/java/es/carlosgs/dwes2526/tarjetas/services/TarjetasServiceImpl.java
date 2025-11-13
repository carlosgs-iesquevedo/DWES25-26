package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaBadUuidException;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.mappers.TarjetaMapper;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.tarjetas.repositories.TarjetasRepository;
import es.carlosgs.dwes2526.titulares.services.TitularesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"tarjetas"})
@Slf4j
@RequiredArgsConstructor
@Service
public class TarjetasServiceImpl implements TarjetasService {
  private final TarjetasRepository tarjetasRepository;
  private final TarjetaMapper tarjetaMapper;
  private final TitularesService titularesService;

  @Override
  public List<TarjetaResponseDto> findAll(String numero, String titular) {
    // Si todos los args están vacíos o nulos, devolvemos todas las tarjetas
    if ((numero == null || numero.isEmpty()) && (titular == null || titular.isEmpty())) {
      log.info("Buscando todas las tarjetas");
      return tarjetaMapper.toResponseDtoList(tarjetasRepository.findAll());
    }
    // Si el numero no está vacío, pero el titular si, buscamos por numero
    if ((numero != null && !numero.isEmpty()) && (titular == null || titular.isEmpty())) {
      log.info("Buscando tarjetas por numero: {}", numero);
      return tarjetaMapper.toResponseDtoList(tarjetasRepository.findByNumero(numero));
    }
    // Si el numero está vacío, pero el titular no, buscamos por titular
    if (numero == null || numero.isEmpty()) {
      log.info("Buscando tarjetas por titular: {}", titular);
      return tarjetaMapper.toResponseDtoList(
          tarjetasRepository.findByTitularContainsIgnoreCase(titular.toLowerCase()));
    }
    // Si el numero y el titular no están vacíos, buscamos por ambos
    log.info("Buscando tarjetas por numero: {} y titular: {}", numero, titular);
    return tarjetaMapper.toResponseDtoList(
        tarjetasRepository.findByNumeroAndTitularContainsIgnoreCase(numero, titular.toLowerCase()));
  }

  // Cachea con el id como key
  @Cacheable(key = "#id")
  @Override
  public TarjetaResponseDto findById(Long id) {
    log.info("Buscando tarjeta por id {}", id);

    return tarjetaMapper.toTarjetaResponseDto(tarjetasRepository.findById(id)
        .orElseThrow(()-> new TarjetaNotFoundException(id)));
  }

  // Cachea con el uuid como key
  @Cacheable(key = "#uuid")
  @Override
  public TarjetaResponseDto findByUuid(String uuid) {
    log.info("Buscando tarjeta por uuid: {}", uuid);
    try {
      var myUUID = UUID.fromString(uuid);
      return tarjetaMapper.toTarjetaResponseDto(tarjetasRepository.findByUuid(myUUID)
          .orElseThrow(() -> new TarjetaNotFoundException(myUUID)));
    } catch (IllegalArgumentException e) {
      throw new TarjetaBadUuidException(uuid);
    }

  }

  // Cachea con el id del resultado de la operación como key
  @CachePut(key = "#result.id")
  @Override
  public TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto) {
    log.info("Guardando tarjeta: {}", tarjetaCreateDto);
    // Buscamos el titular por su nombre
    var titular = titularesService.findByNombre(tarjetaCreateDto.getTitular());
    // Creamos la tarjeta nueva con los datos que nos vienen
    Tarjeta nuevaTarjeta = tarjetaMapper.toTarjeta(tarjetaCreateDto, titular);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetasRepository.save(nuevaTarjeta));
  }

  @CachePut(key = "#result.id")
  @Override
  public TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto) {
    log.info("Actualizando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    var tarjetaActual = tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // Actualizamos la tarjeta con los datos que nos vienen
    Tarjeta tarjetaActualizada =  tarjetaMapper.toTarjeta(tarjetaUpdateDto, tarjetaActual);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetasRepository.save(tarjetaActualizada));
  }

  // El key es opcional, si no se indica
  @CacheEvict(key = "#id")
  @Override
  public void deleteById(Long id) {
    log.debug("Borrando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // La borramos del repositorio si existe
    tarjetasRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada
    //tarjetasRepository.updateIsDeletedToTrueById(id);

  }

}
