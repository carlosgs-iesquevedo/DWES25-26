package es.carlosgs.dwes2526.tarjetas.controllers;

import es.carlosgs.dwes2526.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.tarjetas.repositories.TarjetasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/tarjetas") // Es la ruta del controlador
public class TarjetasRestController {
  // Repositorio de tarjetas
  private final TarjetasRepository tarjetasRepository;

  @Autowired
  public TarjetasRestController(TarjetasRepository tarjetasRepository) {
    this.tarjetasRepository = tarjetasRepository;
  }

  @GetMapping()
  public ResponseEntity<List<Tarjeta>> getAll(@RequestParam(required = false) String numero) {
    if (numero != null) {
      return ResponseEntity.ok(tarjetasRepository.findAllByNumero(numero));
    } else {
      //return ResponseEntity.ok(tarjetasRepository.findAll());
      return ResponseEntity.status(HttpStatus.OK).body(tarjetasRepository.findAll());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Tarjeta> getById(@PathVariable Long id) {
    // Estilo estructurado
        /*
        Optional<Tarjeta> tarjeta = tarjetasRepository.findById(id);
        if (tarjeta.isPresent()) {
            return ResponseEntity.ok(tarjeta.get());
        }  else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
         */

    // Estilo funcional
    return tarjetasRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping()
  public ResponseEntity<Tarjeta> create(@RequestBody Tarjeta tarjeta) {
    var saved = tarjetasRepository.save(tarjeta);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Tarjeta> update(@PathVariable Long id, @RequestBody Tarjeta tarjeta) {
    // La buscamos, si existe la actualizamos, si no devolvemos Not Found
    return tarjetasRepository.findById(id)
        .map(t -> {
          var updated = tarjetasRepository.save(tarjeta);
          return ResponseEntity.status(HttpStatus.CREATED).body(updated);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Tarjeta> updatePartial(@PathVariable Long id, @RequestBody Tarjeta tarjeta) {
    // La buscamos, si existe la actualizamos, si no devolvemos Not Found
    return tarjetasRepository.findById(id)
        .map(t -> {
          var updated = tarjetasRepository.save(tarjeta);
          return ResponseEntity.status(HttpStatus.CREATED).body(updated);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    // La buscamos, si existe la borramos, si no devolvemos Not Found
    return tarjetasRepository.findById(id)
        .map(t -> {
          tarjetasRepository.deleteById(id);
          return ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build();
        })
        .orElse(ResponseEntity.notFound().build());
  }

}
