package es.carlosgs.dwes2526.tarjetas.controllers;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/tarjetas") // Es la ruta del controlador
public class TarjetasRestController {
  @GetMapping()
  public ResponseEntity<List<Tarjeta>> getAll() {
    return ResponseEntity.status(HttpStatus.OK).body(List.of());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Tarjeta> getById(@PathVariable Long id) {
    if (id == 2) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
      // equivalente a:
      //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PostMapping()
  public ResponseEntity<Tarjeta> create(@RequestBody Tarjeta tarjeta) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tarjeta);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Tarjeta> update(@PathVariable Long id, @RequestBody Tarjeta tarjeta) {
    return ResponseEntity.status(HttpStatus.OK).body(tarjeta);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Tarjeta> updatePartial(@PathVariable Long id, @RequestBody Tarjeta tarjeta) {
    return ResponseEntity.status(HttpStatus.OK).body(tarjeta);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
