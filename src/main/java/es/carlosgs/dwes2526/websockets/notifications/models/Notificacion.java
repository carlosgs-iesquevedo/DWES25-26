package es.carlosgs.dwes2526.websockets.notifications.models;

public record Notificacion<T>(
    String entity,
    Tipo type,
    T data,
    String createdAt
) {

  public enum Tipo {CREATE, UPDATE, DELETE}

}
