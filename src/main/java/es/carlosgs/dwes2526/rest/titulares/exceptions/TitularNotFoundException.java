package es.carlosgs.dwes2526.rest.titulares.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de titular no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TitularNotFoundException extends TitularException {
  public TitularNotFoundException(Long id) {
    super("Titular con id " + id + " no encontrado");
  }

  public TitularNotFoundException(String titular) {
    super("Titular " + titular + " no encontrado");
  }
}
