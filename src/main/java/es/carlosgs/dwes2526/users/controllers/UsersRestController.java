package es.carlosgs.dwes2526.users.controllers;


import es.carlosgs.dwes2526.users.dto.UserRequest;
import es.carlosgs.dwes2526.users.dto.UserResponse;
import es.carlosgs.dwes2526.users.exceptions.UserNotFound;
import es.carlosgs.dwes2526.users.exceptions.UserNameOrEmailExists;
import es.carlosgs.dwes2526.users.services.UsersService;
import es.carlosgs.dwes2526.utils.pagination.PageResponse;
import es.carlosgs.dwes2526.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/users") // Es la ruta del controlador
public class UsersRestController {
  private final UsersService usersService;
  private final PaginationLinksUtils paginationLinksUtils;

  /**
   * Obtiene todos los usuarios
   *
   * @param username  username del usuario
   * @param email     email del usuario
   * @param isDeleted si está borrado o no
   * @param page      página
   * @param size      tamaño
   * @param sortBy    campo de ordenación
   * @param direction dirección de ordenación
   * @param request   petición
   * @return Respuesta con la página de usuarios
   */
  @GetMapping
  public ResponseEntity<PageResponse<UserResponse>> getAll(
      @RequestParam(required = false) Optional<String> username,
      @RequestParam(required = false) Optional<String> email,
      @RequestParam(required = false) Optional<Boolean> isDeleted,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String direction,
      HttpServletRequest request
  ) {
    log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
        username, email, isDeleted, page, size, sortBy, direction);
    // Creamos el objeto de ordenación
    Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    // Creamos cómo va a ser la paginación
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
    Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
    return ResponseEntity.ok()
        .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
        .body(PageResponse.of(pageResult, sortBy, direction));
  }

  /**
   * Obtiene un usuario por su id
   *
   * @param id del usuario, se pasa como parámetro de la URL /{id}
   * @return Usuario si existe
   * @throws UserNotFound si no existe el usuario (404)
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
    log.info("findById: id: {}", id);
    return ResponseEntity.ok(usersService.findById(id));
  }

  /**
   * Crea un nuevo usuario
   *
   * @param userRequest usuario a crear
   * @return Usuario creado
   * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación
   */
  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
    log.info("save: userRequest: {}", userRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
  }

  /**
   * Actualiza un usuario
   *
   * @param id          id del usuario
   * @param userRequest usuario a actualizar
   * @return Usuario actualizado
   * @throws UserNotFound                        si no existe el usuario (404)
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen (400)
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
    log.info("update: id: {}, userRequest: {}", id, userRequest);
    return ResponseEntity.ok(usersService.update(id, userRequest));
  }

  /**
   * Borra un usuario
   *
   * @param id id del usuario
   * @return Respuesta vacía
   * @throws UserNotFound si no existe el usuario (404)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    log.info("delete: id: {}", id);
    usersService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Manejador de excepciones de Validación: 400 Bad Request
   *
   * @param ex excepción
   * @return Mapa de errores de validación con el campo y el mensaje
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    BindingResult result = ex.getBindingResult();
    problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
        + "'. " + "Núm. errores: " + result.getErrorCount());

    Map<String, String> errores = new HashMap<>();
    result.getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errores.put(fieldName, errorMessage);
    });

    problemDetail.setProperty("errores", errores);
    return problemDetail;
  }
}
