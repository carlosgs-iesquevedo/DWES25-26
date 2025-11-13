package es.carlosgs.dwes2526.titulares.repositories;

import es.carlosgs.dwes2526.titulares.models.Titular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitularesRepository extends JpaRepository<Titular, Long> {
  // Encontrar por nombre exacto
  Optional<Titular> findByNombreEqualsIgnoreCase(String nombre);

  // Encontrar por nombre exacto y no borrado
  Optional<Titular> findByNombreEqualsIgnoreCaseAndIsDeletedFalse(String nombre);

  // Titulares por nombre
  List<Titular> findByNombreContainingIgnoreCase(String nombre);

  // Tirulares activos
  List<Titular> findByNombreContainingIgnoreCaseAndIsDeletedFalse(String nombre);

  // Si están borrados
  List<Titular> findByIsDeleted(Boolean isDeleted);

  // Actualizar el titular con isDeleted a true
  @Modifying // Para indicar que es una consulta de actualización
  @Query("UPDATE Titular tit SET tit.isDeleted = true WHERE tit.id = :id")
  // Consulta de actualización
  void updateIsDeletedToTrueById(Long id);

  // Obtiene si existe una tarjeta con el id del titular
  @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tarjeta t WHERE t.titular.id = :id")
  Boolean existsTarjetaById(Long id);

}