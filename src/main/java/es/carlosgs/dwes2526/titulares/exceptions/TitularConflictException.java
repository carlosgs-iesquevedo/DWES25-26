package es.carlosgs.dwes2526.titulares.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de conflicto en titular
 * Status 409
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class TitularConflictException extends TitularException {

  public TitularConflictException(String message) {
    super(message);
  }
}