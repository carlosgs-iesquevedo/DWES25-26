package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarjetasRepository extends JpaRepository<Tarjeta, Long> {
  // Otras consultas aparte de las básicas que proporciona la interfaz JpaRepository

  // Por número
  List<Tarjeta> findByNumero(String numero);
  // Por número y que isDeleted sea false
  List<Tarjeta> findByNumeroAndIsDeletedFalse(String numero);

  // Por titular
  List<Tarjeta> findByTitularContainsIgnoreCase(String titular);
  // Por titular y que isDeleted sea false
  List<Tarjeta> findByTitularContainsIgnoreCaseAndIsDeletedFalse(String titular);

  // Por número y titular
  List<Tarjeta> findByNumeroAndTitularContainsIgnoreCase(String numero, String titular);
  List<Tarjeta> findByNumeroAndTitularContainsIgnoreCaseAndIsDeletedFalse(String numero, String titular);

  // Por UUID
  Optional<Tarjeta> findByUuid(UUID uuid);
  boolean existsByUuid(UUID uuid);
  void deleteByUuid(UUID uuid);

  // Si está borrado
  List<Tarjeta> findByIsDeleted(Boolean isDeleted);

  // Actualizar la tarjeta con isDeleted a true
  @Modifying // Para indicar que es una consulta de actualización
  @Query("UPDATE Tarjeta t SET t.isDeleted = true WHERE t.id = :id")
  // Consulta de actualización
  void updateIsDeletedToTrueById(Long id);
}
