package es.carlosgs.dwes2526.rest.titulares.controllers;

import es.carlosgs.dwes2526.rest.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.rest.titulares.models.Titular;
import es.carlosgs.dwes2526.rest.titulares.services.TitularesService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/titulares") // Es la ruta del controlador
public class TitularesRestController {
  private final TitularesService titularesService;
  private final PaginationLinksUtils paginationLinksUtils;

  @GetMapping()
  public ResponseEntity<PageResponse<Titular>> getAll(
      @RequestParam(required = false) Optional<String> nombre,
      @RequestParam(required = false) Optional<Boolean> isDeleted,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String direction,
      HttpServletRequest request
  ) {
    log.info("Buscando todos los titulares con nombre={} isDeleted={}", nombre, isDeleted);
    // Creamos el objeto de ordenación
    Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    // Creamos cómo va a ser la paginación
    Pageable pageable = PageRequest.of(page, size, sort);
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
    Page<Titular> pageResult = titularesService.findAll(nombre, isDeleted, pageable);
    return ResponseEntity.ok()
        .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
        .body(PageResponse.of(pageResult, sortBy, direction));

  }

  @GetMapping("/{id}")
  public ResponseEntity<Titular> getById(@PathVariable Long id) {
    log.info("Buscando titular por id={}", id);
    return ResponseEntity.ok(titularesService.findById(id));
  }

  @PostMapping()
  public ResponseEntity<Titular> create(@Valid @RequestBody TitularRequestDto titularRequestDto) {
    log.info("Creando titular : {}", titularRequestDto);
    var saved = titularesService.save(titularRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Titular> update(@PathVariable Long id, @Valid @RequestBody TitularRequestDto titularRequestDto) {
    log.info("Actualizando titular id={} con titular={}", id, titularRequestDto);
    return ResponseEntity.ok(titularesService.update(id, titularRequestDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    log.info("Borrando titular por id: {}", id);
    titularesService.deleteById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
