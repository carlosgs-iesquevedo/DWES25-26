package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class TarjetasRepositoryImpl implements TarjetasRepository {
  private final Map<Long, Tarjeta> tarjetas = new LinkedHashMap<>(
      Map.of(
          1L, new Tarjeta(1L, "1234-5678-1234-5678", "555",
              LocalDate.of(2025,12,31), "Jose", 100.0,
              LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
          2L,  new Tarjeta(2L, "4321-5678-1234-5678", "555",
              LocalDate.of(2025,12,31), "Juan", 100.0,
              LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
      ));


  @Override
  public List<Tarjeta> findAll() {
    log.info("Buscando tarjetas");
    return tarjetas.values().stream()
        .toList();
  }

  @Override
  public List<Tarjeta> findAllByNumero(String numero) {
    log.info("Buscando tarjetas por numero: " + numero);
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getNumero().toLowerCase().contains(numero.toLowerCase()))
        .toList();
  }

  @Override
  public List<Tarjeta> findAllByTitular(String titular) {
    log.info("Buscando tarjetas por titular: " + titular);
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getTitular().toLowerCase().contains(titular.toLowerCase()))
        .toList();
  }

  @Override
  public List<Tarjeta> findAllByNumeroAndTitular(String numero, String titular) {
    log.info("Buscando tarjetas por numero: {} y titular: {} ", numero, titular);
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getNumero().toLowerCase().contains(numero.toLowerCase()))
        .filter(tarjeta -> tarjeta.getTitular().toLowerCase().contains(titular.toLowerCase()))
        .toList();
  }

  @Override
  public Optional<Tarjeta> findById(Long id) {
    log.info("Buscando tarjetas por id: " + id);
    return tarjetas.get(id) != null ? Optional.of(tarjetas.get(id)) : Optional.empty();
  }

  @Override
  public Optional<Tarjeta> findByUuid(UUID uuid) {
    log.info("Buscando tarjeta por uuid: " + uuid);
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getUuid().equals(uuid))
        .findFirst();
  }

  @Override
  public boolean existsById(Long id) {
    log.info("Comprobando si existe tarjeta por id: " + id);
    return tarjetas.get(id) != null;
  }

  @Override
  public boolean existsByUuid(UUID uuid) {
    log.info("Comprobando si existe tarjeta por uuid: " + uuid);
    return tarjetas.values().stream()
        .anyMatch(tarjeta -> tarjeta.getUuid().equals(uuid));
  }

  @Override
  public Tarjeta save(Tarjeta tarjeta) {
    log.info("Guardando tarjeta: " + tarjeta);
    tarjetas.put(tarjeta.getId(), tarjeta);
    return tarjeta;
  }

  @Override
  public void deleteById(Long id) {
    log.info("Borrando tarjeta por id: " + id);
    tarjetas.remove(id);
  }

  @Override
  public void deleteByUuid(UUID uuid) {
    log.info("Borrando tarjeta por uuid: " + uuid);
    tarjetas.values().removeIf(tarjeta -> tarjeta.getUuid().equals(uuid));
  }

  @Override
  public Long nextId() {
    log.debug("Obteniendo siguiente id de tarjeta");
    return tarjetas.keySet().stream()
        .mapToLong(value -> value)
        .max()
        .orElse(0) + 1;
  }
}
