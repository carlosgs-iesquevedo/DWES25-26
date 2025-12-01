package es.carlosgs.dwes2526.websockets.notifications.mappers;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.websockets.notifications.dto.TarjetaNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class TarjetaNotificationMapper {
  public TarjetaNotificationResponse toTarjetaNotificationDto(Tarjeta tarjeta) {
    return new TarjetaNotificationResponse(
        tarjeta.getId(),
        tarjeta.getNumero(),
        tarjeta.getCvc(),
        tarjeta.getFechaCaducidad().toString(),
        tarjeta.getTitular().getNombre(),
        tarjeta.getSaldo(),
        tarjeta.getCreatedAt().toString(),
        tarjeta.getUpdatedAt().toString(),
        tarjeta.getUuid().toString()
    );
  }
}
