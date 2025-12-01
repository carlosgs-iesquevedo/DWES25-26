package es.carlosgs.dwes2526.websockets.notifications.dto;


public record TarjetaNotificationResponse(
    Long id,
    String numero,
    String cvc,
    String fechaCaducidad,
    String titular,
    Double saldo,

    String createdAt,
    String updatedAt,
    String uuid
) {
}
