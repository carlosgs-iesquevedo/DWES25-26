package es.carlosgs.dwes2526.tarjetas.repositories;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// No es obligatorio @Repository ya que Spring al ver que hereda de JpaRepository le añade este aspecto.
// Aún así conviene ponerlo para facilitarle el trabajo
@Repository
public interface TarjetasRepository extends JpaRepository<Tarjeta, Long>, JpaSpecificationExecutor<Tarjeta> {
  // Otras consultas aparte de las básicas que proporciona la interfaz JpaRepository

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

  @Query("SELECT t FROM Tarjeta t WHERE t.titular.usuario.id = :usuarioId")
  Page<Tarjeta> findByUsuarioId(Long usuarioId, Pageable pageable);

  @Query("SELECT t FROM Tarjeta t WHERE t.titular.usuario.id = :usuarioId")
  List<Tarjeta> findByUsuarioId(Long usuarioId);

  // Obtiene si existe una tarjeta con el id del usuario
  @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tarjeta t WHERE t.titular.usuario.id = :id")
  Boolean existsByUsuarioId(Long id);

}
