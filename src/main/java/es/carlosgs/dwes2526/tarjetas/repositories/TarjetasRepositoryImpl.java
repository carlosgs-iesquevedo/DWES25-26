package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    return tarjetas.values().stream()
        .toList();
  }

  @Override
  public List<Tarjeta> findAllByNumero(String numero) {
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getNumero().toLowerCase().contains(numero.toLowerCase()))
        .toList();
  }

  @Override
  public Optional<Tarjeta> findById(Long id) {
    return tarjetas.get(id) != null ? Optional.of(tarjetas.get(id)) : Optional.empty();
  }

  @Override
  public Optional<Tarjeta> findByUuid(UUID uuid) {
    return tarjetas.values().stream()
        .filter(tarjeta -> tarjeta.getUuid().equals(uuid))
        .findFirst();
  }

  @Override
  public boolean existsById(Long id) {
    return tarjetas.get(id) != null;
  }

  @Override
  public boolean existsByUuid(UUID uuid) {
    return tarjetas.values().stream()
        .anyMatch(tarjeta -> tarjeta.getUuid().equals(uuid));
  }

  @Override
  public Tarjeta save(Tarjeta tarjeta) {
    // Si existe actualizamos los valores
    if (tarjeta.getId() != null && existsById(tarjeta.getId())) {
      return update(tarjeta);
    } else {
      // Si no existe, creamos
      return create(tarjeta);
    }
  }

  private Tarjeta create(Tarjeta tarjeta) {
    // Obtenemos el nuevo id:
    Long id = tarjetas.keySet().stream()
        .mapToLong(value -> value)
        .max()
        .orElse(0) + 1;

    // Creamos la nueva tarjeta asignando el id y los campos que nos pasan, por inmutabilidad (create, update y uuid ya se asignan)
    Tarjeta nuevaTarjeta = new Tarjeta(id, tarjeta.getNumero(), tarjeta.getCvc(),
        tarjeta.getFechaCaducidad(), tarjeta.getTitular(), tarjeta.getSaldo(),
        LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());
    // Lo añadimos a la colección
    tarjetas.put(id, nuevaTarjeta);
    // Devolvemos la tarjeta creada
    return nuevaTarjeta;
  }

  private Tarjeta update(Tarjeta tarjeta) {
    // Obtenemos la tarjeta actual
    Tarjeta tarjetaActual = tarjetas.get(tarjeta.getId());

    // Creamos la tarjeta actualizada con los campos que nos llegan actualizando el updateAt
    // y si son null no se actualizan y se quedan los anteriores
    Tarjeta tarjetaActualizada = new Tarjeta(
        tarjetaActual.getId(),
        tarjeta.getNumero() != null ? tarjeta.getNumero() : tarjetaActual.getNumero(),
        tarjeta.getCvc() != null ? tarjeta.getCvc() : tarjetaActual.getCvc(),
        tarjeta.getFechaCaducidad() != null ? tarjeta.getFechaCaducidad() : tarjetaActual.getFechaCaducidad(),
        tarjeta.getTitular() != null ? tarjeta.getTitular() : tarjetaActual.getTitular(),
        tarjeta.getSaldo() != null ? tarjeta.getSaldo() : tarjetaActual.getSaldo(),
        tarjetaActual.getCreatedAt(),
        LocalDateTime.now(),
        tarjetaActual.getUuid()
    );

    // Lo actualizamos en la colección
    tarjetas.put(tarjeta.getId(), tarjetaActualizada);
    // Devolvemos la tarjeta actualizada
    return tarjetaActualizada;
  }

  @Override
  public void deleteById(Long id) {
    tarjetas.remove(id);
  }

  @Override
  public void deleteByUuid(UUID uuid) {
    tarjetas.values().removeIf(tarjeta -> tarjeta.getUuid().equals(uuid));
  }
}
