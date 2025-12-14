package es.carlosgs.dwes2526.tarjetas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TarjetaBadRequestException extends TarjetaException {
  public TarjetaBadRequestException(String message) {
    super(message);
  }
}
