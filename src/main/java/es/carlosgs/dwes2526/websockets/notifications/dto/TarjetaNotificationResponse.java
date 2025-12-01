package es.carlosgs.dwes2526.websockets.notifications.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
