package es.carlosgs.dwes2526.tarjetas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de tarjeta no encontrada
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNotFoundException  extends TarjetaException{
  public TarjetaNotFoundException(Long id) {
    super("Tarjeta con id " + id + " no encontrada");
  }
  public TarjetaNotFoundException(UUID uuid) {
    super("Tarjeta con uuid " + uuid + " no encontrada");
  }
}
