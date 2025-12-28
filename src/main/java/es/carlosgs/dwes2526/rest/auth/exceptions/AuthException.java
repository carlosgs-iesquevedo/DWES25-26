package es.carlosgs.dwes2526.rest.auth.exceptions;

public abstract class AuthException extends RuntimeException {
  public AuthException(String message) {
    super(message);
  }
}
