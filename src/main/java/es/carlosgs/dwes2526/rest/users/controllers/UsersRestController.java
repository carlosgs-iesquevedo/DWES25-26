package es.carlosgs.dwes2526.rest.users.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.rest.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import es.carlosgs.dwes2526.rest.users.dto.UserInfoResponse;
import es.carlosgs.dwes2526.rest.users.dto.UserRequest;
import es.carlosgs.dwes2526.rest.users.dto.UserResponse;
import es.carlosgs.dwes2526.rest.users.exceptions.UserNameOrEmailExists;
import es.carlosgs.dwes2526.rest.users.exceptions.UserNotFound;
import es.carlosgs.dwes2526.rest.users.models.User;
import es.carlosgs.dwes2526.rest.users.services.UsersService;
import es.carlosgs.dwes2526.utils.pagination.PageResponse;
import es.carlosgs.dwes2526.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
public class UsersRestController {
  private final UsersService usersService;
  private final PaginationLinksUtils paginationLinksUtils;
  private final TarjetasService tarjetasService;

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
  @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
  public ResponseEntity<PageResponse<UserResponse>> findAll(
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
  @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
  public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
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
  @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
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
  @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
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
  @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    log.info("delete: id: {}", id);
    usersService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Obtiene el usuario actual
   *
   * @param user usuario autenticado
   * @return Datos del usuario
   */
  @GetMapping("/me/profile")
  @PreAuthorize("hasRole('USER')") // Solo los user pueden acceder
  public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user) {
    log.info("Obteniendo usuario");
    // Está autenticado, por lo que devolvemos sus datos ya sabemos su id
    return ResponseEntity.ok(usersService.findById(user.getId()));
  }

  /**
   * Actualiza el usuario actual
   *
   * @param user        usuario autenticado
   * @param userRequest usuario a actualizar
   * @return Usuario actualizado
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @PutMapping("/me/profile")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody UserRequest userRequest) {
    log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
    return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
  }

  /**
   * Borra el usuario actual
   *
   * @param user usuario autenticado
   * @return Respuesta vacía
   */
  @DeleteMapping("/me/profile")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
    log.info("deleteMe: user: {}", user);
    usersService.deleteById(user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Obtiene las tarjetas del usuario actual
   *
   * @param user      usuario autenticado
   * @param page      página
   * @param size      tamaño
   * @param sortBy    campo de ordenación
   * @param direction dirección de ordenación
   * @return Respuesta con la página de tarjetas
   */
  @GetMapping("/me/tarjetas")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<PageResponse<TarjetaResponseDto>> getTarjetasByUsuario(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String direction
  ) {
    log.info("Obteniendo tarjetas del usuario con id: {}", user.getId());
    Sort sort = direction.equalsIgnoreCase(
        Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(PageResponse.of(
        tarjetasService.findByUsuarioId(user.getId(), pageable), sortBy, direction));
  }

  /**
   * Obtiene una tarjeta del usuario actual
   *
   * @param user     usuario autenticado
   * @param idTarjeta id de la tarjeta
   * @return Tarjeta
   * @throws TarjetaNotFoundException si no existe la tarjeta
   */
  @GetMapping("/me/tarjetas/{id}")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<TarjetaResponseDto> getTarjeta(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long idTarjeta
  ) {
    log.info("Obteniendo tarjeta con id: {}", idTarjeta);
    // Solo puedo verla si es una tarjeta mía
    return ResponseEntity.ok(tarjetasService.findByUsuarioId(user.getId(), idTarjeta));
  }

  /**
   * Crea una tarjeta para el usuario actual
   *
   * @param user   usuario autenticado
   * @param tarjeta tarjeta a crear
   * @return Tarjeta creada
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @PostMapping("/me/tarjetas")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<TarjetaResponseDto> saveTarjeta(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody TarjetaCreateDto tarjeta
  ) {
    log.info("Creando tarjeta: {}", tarjeta);
    return ResponseEntity.status(HttpStatus.CREATED).body(tarjetasService.save(tarjeta, user.getId()));
  }

  /**
   * Actualiza una tarjeta del usuario actual
   *
   * @param user     usuario autenticado
   * @param idTarjeta id de la tarjeta
   * @param  tarjeta a actualizar
   * @return Tarjeta actualizada
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   * @throws TarjetaNotFoundException                      si no existe la tarjeta (404)
   */
  @PutMapping("/me/tarjetas/{id}")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<TarjetaResponseDto> updateTarjeta(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long idTarjeta,
      @Valid @RequestBody TarjetaUpdateDto tarjeta) {
    log.info("Actualizando tarjeta con id: {}", idTarjeta);
    return ResponseEntity.ok(tarjetasService.update(idTarjeta, tarjeta,  user.getId()));
  }

  /**
   * Borra una tarjeta del usuario actual
   *
   * @param user     usuario autenticado
   * @param idTarjeta id de la tarjeta
   * @return Tarjeta borrada
   * @throws TarjetaNotFoundException  si no existe el tarjeta (404)
   * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
   */
  @DeleteMapping("/me/tarjetas/{id}")
  @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
  public ResponseEntity<Void> deleteTarjeta(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Long idTarjeta
  ) {
    log.info("Borrando tarjeta con id: {}", idTarjeta);
    tarjetasService.deleteById(idTarjeta,  user.getId());
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
