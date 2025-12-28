package es.carlosgs.dwes2526.rest.users.exceptions;

public abstract class UserException extends RuntimeException {
  public UserException(String message) {
    super(message);
  }
}
