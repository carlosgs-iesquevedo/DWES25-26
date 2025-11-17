package es.carlosgs.dwes2526.titulares.controllers;

import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.models.Titular;
import es.carlosgs.dwes2526.titulares.services.TitularesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/titulares") // Es la ruta del controlador
public class TitularesRestController {
  private final TitularesService titularesService;

  @GetMapping()
  public ResponseEntity<List<Titular>> getAll(@RequestParam(required = false) String nombre) {
    log.info("Buscando todos los titulares con nombre: {}", nombre);
    return ResponseEntity.ok(titularesService.findAll(nombre));
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
