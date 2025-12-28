package es.carlosgs.dwes2526.rest.tarjetas.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.carlosgs.dwes2526.config.websockets.WebSocketConfig;
import es.carlosgs.dwes2526.config.websockets.WebSocketHandler;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.rest.tarjetas.exceptions.TarjetaBadRequestException;
import es.carlosgs.dwes2526.rest.tarjetas.exceptions.TarjetaBadUuidException;
import es.carlosgs.dwes2526.rest.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.rest.tarjetas.mappers.TarjetaMapper;
import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.repositories.TarjetasRepository;
import es.carlosgs.dwes2526.rest.titulares.models.Titular;
import es.carlosgs.dwes2526.rest.titulares.repositories.TitularesRepository;
import es.carlosgs.dwes2526.websockets.notifications.dto.TarjetaNotificationResponse;
import es.carlosgs.dwes2526.websockets.notifications.mappers.TarjetaNotificationMapper;
import es.carlosgs.dwes2526.websockets.notifications.models.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@CacheConfig(cacheNames = {"tarjetas"})
@Slf4j
@RequiredArgsConstructor
@Service
public class TarjetasServiceImpl implements TarjetasService, InitializingBean {
  private final TarjetasRepository tarjetasRepository;
  private final TarjetaMapper tarjetaMapper;
  private final TitularesRepository titularesRepository;

  private final WebSocketConfig webSocketConfig;
  private final ObjectMapper objectMapper;
  private final TarjetaNotificationMapper tarjetaNotificationMapper;
  private WebSocketHandler webSocketService;

  public void afterPropertiesSet() {
    this.webSocketService = this.webSocketConfig.webSocketTarjetasHandler();
  }

  // Para que en los test se pueda inicializar
  public void setWebSocketService(WebSocketHandler webSocketHandler) {
    this.webSocketService = webSocketHandler;
  }

  @Override
  public Page<TarjetaResponseDto> findAll(Optional<String> numero, Optional<String> titular, Optional<Boolean> isDeleted, Pageable pageable) {
    log.info("Buscando tarjetas por numero: {}, titular: {} , isDeleted {}", numero, titular, isDeleted);
    // Criterio de búsqueda por número
    Specification<Tarjeta> specNumeroTarjeta = (root, query, criteriaBuilder) ->
        numero.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("numero")), "%" + n.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay numero, no filtramos

    // Criterio de búsqueda por titular
    Specification<Tarjeta> specTitularTarjeta = (root, query, criteriaBuilder) ->
        titular.map(t -> {
          Join<Tarjeta, Titular> titularJoin = root.join("titular");
          return criteriaBuilder.like(criteriaBuilder.lower(titularJoin.get("nombre")), "%" + t.toLowerCase() + "%");
        }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay titular, no filtramo

    // Criterio de búsqueda por isDeleted
    Specification<Tarjeta> specIsDeleted = (root, query, criteriaBuilder) ->
        isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    // Combinamos las especificaciones
    Specification<Tarjeta> criterio = Specification.allOf(specNumeroTarjeta, specTitularTarjeta, specIsDeleted);

    return tarjetasRepository.findAll(criterio, pageable)
        .map(tarjetaMapper::toTarjetaResponseDto);

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

  @Override
  public Page<TarjetaResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
    log.info("Obteniendo tarjetas del usuario con id: {}", usuarioId);
    return tarjetasRepository.findByUsuarioId(usuarioId, pageable)
        .map(tarjetaMapper::toTarjetaResponseDto);
  }

  @Override
  public TarjetaResponseDto findByUsuarioId(Long usuarioId, Long idTarjeta) {
    log.info("Obteniendo tarjetas del usuario con id: {}", usuarioId);
    var tarjetas = tarjetasRepository.findByUsuarioId(usuarioId);
    var tarjetaEncontrada = tarjetas.stream().filter(t ->  t.getId().equals(idTarjeta))
        .findFirst().orElse(null);
    if (tarjetaEncontrada == null) {
      throw new TarjetaBadRequestException("La tarjeta " + idTarjeta + " no corresponde a este usuario");
      // O not found también valdría
      //throw new TarjetaNotFoundException(idTarjeta);
    }
    return tarjetaMapper.toTarjetaResponseDto(tarjetaEncontrada);
  }


  /**
   * Comprueba si existe el titular
   *
   * @param nombreTitular Nombre del titular
   */
  private Titular checkTitular(String nombreTitular) {
    log.info("Buscando titular por nombre: {}", nombreTitular);
    // Buscamos el titular por su nombre, debe existir y no estar borrado
    var titular = titularesRepository.findByNombreEqualsIgnoreCase(nombreTitular);
    if (titular.isEmpty() || titular.get().getIsDeleted()) {
      throw new TarjetaBadRequestException("El titular " + nombreTitular + " no existe o está borrado");
    }
    return titular.get();
  }
  // Cachea con el id del resultado de la operación como key
  @CachePut(key = "#result.id")
  @Override
  public TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto) {
    log.info("Guardando tarjeta: {}", tarjetaCreateDto);
    Titular titular = checkTitular(tarjetaCreateDto.getTitular());
    // Creamos la tarjeta nueva con los datos que nos vienen y la guardamos en el repositorio
    Tarjeta tarjetaSaved = tarjetasRepository.save(
        tarjetaMapper.toTarjeta(tarjetaCreateDto, titular));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.CREATE, tarjetaSaved);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetaSaved);
  }

  @Override
  public TarjetaResponseDto save(TarjetaCreateDto tarjetaCreateDto, Long usuarioId) {
    log.info("Guardando tarjeta: {} de usuarioId: {}", tarjetaCreateDto, usuarioId);
    Titular titular = checkTitular(tarjetaCreateDto.getTitular());
    var usuario = titular.getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new TarjetaBadRequestException("El usuario no se corresponde con el titular");
    }
    // Creamos la tarjeta nueva con los datos que nos vienen y la guardamos en el repositorio
    Tarjeta tarjetaSaved = tarjetasRepository.save(
        tarjetaMapper.toTarjeta(tarjetaCreateDto, titular));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.CREATE, tarjetaSaved);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetaSaved);
  }



  @CachePut(key = "#result.id")
  @Override
  public TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto) {
    log.info("Actualizando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    var tarjetaActual = tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // Como no podemos actualizar el titular, no comprobamos si existe
    // Actualizamos la tarjeta con los datos que nos vienen y la guardamos en el repositorio
    Tarjeta tarjetaUpdated =  tarjetasRepository.save(
        tarjetaMapper.toTarjeta(tarjetaUpdateDto, tarjetaActual));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.UPDATE, tarjetaUpdated);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetaUpdated);
  }

  @CachePut(key = "#result.id")
  @Override
  public TarjetaResponseDto update(Long id, TarjetaUpdateDto tarjetaUpdateDto, Long usuarioId) {
    log.info("Actualizando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    var tarjetaActual = tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // Como no podemos actualizar el titular, no comprobamos si existe
    // pero sí comprobamos que pertenece al usuarioId
    var usuario = tarjetaActual.getTitular().getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new TarjetaBadRequestException("La tarjeta " +
          tarjetaUpdateDto.getNumero() + " no corresponde a este usuario");
    }
    // Actualizamos la tarjeta con los datos que nos vienen y la guardamos en el repositorio
    Tarjeta tarjetaUpdated =  tarjetasRepository.save(
        tarjetaMapper.toTarjeta(tarjetaUpdateDto, tarjetaActual));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.UPDATE, tarjetaUpdated);
    // La guardamos en el repositorio
    return tarjetaMapper.toTarjetaResponseDto(tarjetaUpdated);
  }

  // El key es opcional, si no se indica
  @CacheEvict(key = "#id")
  @Override
  public void deleteById(Long id) {
    log.debug("Borrando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    Tarjeta tarjetaDeleted = tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // La borramos del repositorio si existe
    tarjetasRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada
    //tarjetasRepository.updateIsDeletedToTrueById(id);
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.DELETE, tarjetaDeleted);

  }

  @CacheEvict(key = "#id")
  @Override
  public void deleteById(Long id, Long usuarioId) {
    log.debug("Borrando tarjeta por id: {}", id);
    // Si no existe lanza excepción
    Tarjeta tarjetaDeleted = tarjetasRepository.findById(id).orElseThrow(()-> new TarjetaNotFoundException(id));
    // La borramos del repositorio si existe y si pertenece al usuario
    var usuario = tarjetaDeleted.getTitular().getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new TarjetaBadRequestException("La tarjeta " + id + " no corresponde a este usuario");
    }
    tarjetasRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada
    //tarjetasRepository.updateIsDeletedToTrueById(id);
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.DELETE, tarjetaDeleted);

  }

  void onChange(Notificacion.Tipo tipo, Tarjeta data) {
    log.debug("Servicio de productos onChange con tipo: {} y datos: {}", tipo, data);

    if (webSocketService == null) {
      log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
      webSocketService = this.webSocketConfig.webSocketTarjetasHandler();
    }

    try {
      Notificacion<TarjetaNotificationResponse> notificacion = new Notificacion<>(
          "TARJETAS",
          tipo,
          tarjetaNotificationMapper.toTarjetaNotificationDto(data),
          LocalDateTime.now().toString()
      );

      String json = objectMapper.writeValueAsString((notificacion));

      log.info("Enviando mensaje a los clientes ws");
      // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar.
      // No bloqueamos el hilo principal que atiende las peticiones http
      Thread senderThread = new Thread(() -> {
        try {
          webSocketService.sendMessage(json);
        } catch (Exception e) {
          log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
        }
      });
      senderThread.setName("WebSocketTarjeta-" + data.getId());
      senderThread.setDaemon(true); // Para que no impida que la aplicación se cierre
      senderThread.start();
      log.info("Hilo de websocket iniciado: {}", data.getId());
    } catch (JsonProcessingException e) {
      log.error("Error al convertir la notificación a JSON", e);
    }
  }

}
