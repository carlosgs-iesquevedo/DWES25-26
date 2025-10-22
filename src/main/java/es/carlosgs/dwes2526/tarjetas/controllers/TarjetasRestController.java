package es.carlosgs.dwes2526.tarjetas.controllers;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaBadRequestException;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.services.TarjetasService;
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

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * Usamos el repositorio de productos y lo inyectamos en el constructor con Autowired
 *
 * @RequiredArgsConstructor es una anotación Lombok que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring
 * con solo declarar las dependencias como final ya que el constructor lo genera Lombok
 */
@Slf4j
@RequiredArgsConstructor
@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/tarjetas") // Es la ruta del controlador
public class TarjetasRestController {
  // Servicio de tarjetas
  private final TarjetasService tarjetasService;

  /**
   * Obtiene todas las tarjetas
   *
   * @param numero    Número de la tarjeta
   * @param titular   Titular de la tarjeta
   * @return Lista de tarjetas
   */
  @GetMapping()
  public ResponseEntity<List<TarjetaResponseDto>> getAll(@RequestParam(required = false) String numero,
                                                      @RequestParam(required = false) String titular) {
    log.info("Buscando tarjetas por numero={}, titular={}", numero, titular);
    return ResponseEntity.ok(tarjetasService.findAll(numero, titular));
  }

  /**
   * Obtiene una tarjeta por su id
   *
   * @param id de la tarjeta, se pasa como parámetro de la URL /{id}
   * @return TarjetaResponseDto si existe
   * @throws TarjetaNotFoundException si no existe la tarjeta (404)
   */
  @GetMapping("/{id}")
  public ResponseEntity<TarjetaResponseDto> getById(@PathVariable Long id) {
    log.info("Buscando tarjeta por id={}", id);
    return ResponseEntity.ok(tarjetasService.findById(id));
  }

  /**
   * Crear una tarjeta
   *
   * @param tarjetaCreateDto a crear
   * @return TarjetaResponseDto creada
   * @throws TarjetaBadRequestException si la tarjeta no es correcta (400)
   */
  @PostMapping()
  public ResponseEntity<TarjetaResponseDto> create(@Valid @RequestBody TarjetaCreateDto tarjetaCreateDto) {
    log.info("Creando tarjeta : {}", tarjetaCreateDto);
    var saved = tarjetasService.save(tarjetaCreateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }


  /**
   * Actualiza una tarjeta
   *
   * @param id      de la tarjeta a actualizar
   * @param tarjetaUpdateDto con los datos a actualizar
   * @return TarjetaResponseDto actualizada
   * @throws TarjetaNotFoundException si no existe la tarjeta (404)
   * @throws TarjetaBadRequestException si la tarjeta no es correcta (400)
   */
  @PutMapping("/{id}")
  public ResponseEntity<TarjetaResponseDto> update(@PathVariable Long id, @Valid @RequestBody TarjetaUpdateDto tarjetaUpdateDto) {
    log.info("Actualizando tarjeta id={} con tarjeta={}", id, tarjetaUpdateDto);
    return ResponseEntity.ok(tarjetasService.update(id, tarjetaUpdateDto));
  }

  /**
   * Actualiza parcialmente una tarjeta
   *
   * @param id      de la tarjeta a actualizar
   * @param tarjetaUpdateDto con los datos a actualizar
   * @return Tarjeta actualizada
   * @throws TarjetaNotFoundException si no existe la tarjeta (404)
   * @throws TarjetaBadRequestException si la tarjeta no es correcta (400)
   */
  @PatchMapping("/{id}")
  public ResponseEntity<TarjetaResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody TarjetaUpdateDto tarjetaUpdateDto) {
    log.info("Actualizando parcialmente tarjeta con id={} con tarjeta={}",id, tarjetaUpdateDto);
    return ResponseEntity.ok(tarjetasService.update(id, tarjetaUpdateDto));
  }

  /**
   * Borra una tarjeta por su id
   *
   * @param id de la tarjeta a borrar
   * @return ResponseEntity con status 204 No Content si se ha conseguido borradr
   * @throws TarjetaNotFoundException si no existe la tarjeta (404)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    log.info("Borrando producto por id: {}", id);
    tarjetasService.deleteById(id);
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
