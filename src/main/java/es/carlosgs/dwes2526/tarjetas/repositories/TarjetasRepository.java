package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// No es obligatorio @Repository ya que Spring al ver que hereda de JpaRepository le añade este aspecto.
// Aún así conviene ponerlo para facilitarle el trabajo
@Repository
public interface TarjetasRepository extends JpaRepository<Tarjeta, Long> {
  // Otras consultas aparte de las básicas que proporciona la interfaz JpaRepository

  // Por número
  Page<Tarjeta> findByNumero(String numero, Pageable pageable);
  // Por número y que isDeleted sea false
  //List<Tarjeta> findByNumeroAndIsDeletedFalse(String numero);

  // Por titular
  @Query("SELECT t FROM Tarjeta t WHERE LOWER(t.titular.nombre) LIKE %:titular%")
  Page<Tarjeta> findByTitularContainsIgnoreCase(String titular, Pageable pageable);
  // Por titular y que isDeleted sea false
  //List<Tarjeta> findByTitularContainsIgnoreCaseAndIsDeletedFalse(String titular);

  // Por número y titular
  @Query("SELECT t FROM Tarjeta t WHERE t.numero = :numero AND LOWER(t.titular.nombre) like %:titular%")
  Page<Tarjeta> findByNumeroAndTitularContainsIgnoreCase(String numero, String titular, Pageable pageable);
  //List<Tarjeta> findByNumeroAndTitularContainsIgnoreCaseAndIsDeletedFalse(String numero, String titular);

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
