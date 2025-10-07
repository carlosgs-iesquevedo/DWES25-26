package es.carlosgs.dwes2526.tarjetas.services;

import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.tarjetas.repositories.TarjetasRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TarjetasServiceImpl implements TarjetasService {
  private final TarjetasRepository tarjetasRepository;

  @Autowired
  public TarjetasServiceImpl(TarjetasRepository tarjetasRepository) {
    this.tarjetasRepository = tarjetasRepository;
  }

  @Override
  public List<Tarjeta> findAll(String numero, String titular) {
    // Si todo está vacío o nulo, devolvemos todas las tarjetas
    if ((numero == null || numero.isEmpty()) && (titular == null || titular.isEmpty())) {
      log.info("Buscando todas las tarjetas");
      return tarjetasRepository.findAll();
    }
    // Si el numero no está vacío, pero el titular si, buscamos por numero
    if ((numero != null && !numero.isEmpty()) && (titular == null || titular.isEmpty())) {
      log.info("Buscando tarjetas por numero: " + numero);
      return tarjetasRepository.findAllByNumero(numero);
    }
    // Si el numero está vacío, pero el titular no, buscamos por titular
    if (numero == null || numero.isEmpty()) {
      log.info("Buscando tarjetas por titular: " + titular);
      return tarjetasRepository.findAllByTitular(titular);
    }
    // Si el numero y el titular no están vacíos, buscamos por ambos
    log.info("Buscando tarjetas por numero: " + numero + " y titular: " + titular);
    return tarjetasRepository.findAllByNumeroAndTitular(numero, titular);
  }

  @Override
  public Tarjeta findById(Long id) {
    log.info("Buscando tarjeta por id {}", id);
    return tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
  }

  @Override
  public Tarjeta findbyUuid(String uuid) {
    log.info("Buscando tarjeta por uuid: " + uuid);
      var myUUID = UUID.fromString(uuid);
      return tarjetasRepository.findByUuid(myUUID).orElseThrow(() -> new TarjetaNotFoundException(myUUID));
  }

  @Override
  public Tarjeta save(Tarjeta tarjeta) {
    log.info("Guardando tarjeta: " + tarjeta);
    // obtenemos el id de tarjeta
    Long id = tarjetasRepository.nextId();
    // Creamos la tarjeta nueva con los datos que nos vienen
    Tarjeta nuevaTarjeta = new Tarjeta(
        id,
        tarjeta.getNumero(),
        tarjeta.getCvc(),
        tarjeta.getFechaCaducidad(),
        tarjeta.getTitular(),
        tarjeta.getSaldo(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        UUID.randomUUID()
    );

    // La guardamos en el repositorio
    return tarjetasRepository.save(nuevaTarjeta);
  }

  @Override
  public Tarjeta update(Long id, Tarjeta tarjeta) {
    log.info("Actualizando tarjeta por id: " + id);
    var tarjetaActual = this.findById(id);
    // Actualizamos la tarjeta con los datos que nos vienen
    Tarjeta tarjetaActualizada =  new Tarjeta(
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
    // La guardamos en el repositorio
    return tarjetasRepository.save(tarjetaActualizada);
  }

  @Override
  public void deleteById(Long id) {
    log.debug("Borrando tarjeta por id: " + id);
    var tarjetaEncontrada = this.findById(id);
    // La borramos del repositorio si existe
    if (tarjetaEncontrada != null)
      tarjetasRepository.deleteById(id);

  }

}
