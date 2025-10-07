package es.carlosgs.dwes2526.tarjetas.exceptions;

public abstract class TarjetaException extends RuntimeException {
  public TarjetaException(String message) {
    super(message);
  }
}
