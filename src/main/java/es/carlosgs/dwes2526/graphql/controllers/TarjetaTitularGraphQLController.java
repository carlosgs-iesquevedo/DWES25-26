package es.carlosgs.dwes2526.graphql.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.repositories.TarjetasRepository;
import es.carlosgs.dwes2526.rest.titulares.models.Titular;
import es.carlosgs.dwes2526.rest.titulares.repositories.TitularesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
// @PreAuthorize("hasAnyRole('USER')") // Protección a nivel de clase
public class TarjetaTitularGraphQLController {
  private final TarjetasRepository tarjetasRepository;
  private final TitularesRepository titularesRepository;


  // --- QUERIES ---

  @QueryMapping
  public List<Tarjeta> tarjetas() {
    // Devuelve todas las tarjetas como entidades (ojo: no paginado)
    return tarjetasRepository.findAll();
  }

  @QueryMapping
  public Tarjeta tarjetaById(@Argument Long id) {
    // Devuelve una tarjeta por su id
    Optional<Tarjeta> tarjetaOpt = tarjetasRepository.findById(id);
    return tarjetaOpt.orElse(null);
  }

  @QueryMapping
  public List<Titular> titulares() {
    // Devuelve todos los titulares como entidades
    return titularesRepository.findAll();
  }

  @QueryMapping
  public Titular titularById(@Argument Long id) {
    // Devuelve un titular por id
    return titularesRepository.findById(id).orElse(null);
  }

  // titularesByNombre(nombre: String!): [Titular!]!
  @QueryMapping
  public List<Titular> titularesByNombre(@Argument String nombre) {
    // Devuelve los titulares que coinciden con el nombre (case insensitive)
    return titularesRepository.findByNombreContainingIgnoreCase(nombre);
    // En caso de que no encuentre ninguna, devuelve una lista vacía
  }

  // --- RESOLVERS RELACIONES ---

  @SchemaMapping(typeName = "Tarjeta", field = "titular")
  public Titular titular(Tarjeta tarjeta) {
    // Devuelve el titular de la tarjeta (ya viene cargada en la entidad)
    return tarjeta.getTitular();
  }

  @SchemaMapping(typeName = "Titular", field = "tarjetas")
  public List<Tarjeta> tarjetas(Titular titular) {
    // Devuelve las tarjetas de un titular
    return tarjetasRepository.findByTitular(titular);
  }
}


