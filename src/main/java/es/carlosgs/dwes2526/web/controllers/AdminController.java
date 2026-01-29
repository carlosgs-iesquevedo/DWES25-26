package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final TarjetasService tarjetasService;


  @GetMapping("/tarjetas")
  public String tarjetas(Model model,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<TarjetaResponseDto> tarjetasPage = tarjetasService.findAll(
      Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", tarjetasPage);
    return "admin/tarjetas/lista";
  }

  @GetMapping("/tarjetas/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Tarjeta tarjeta = tarjetasService.buscarPorId(id).orElse(null);
    model.addAttribute("tarjeta", tarjeta);
    return "admin/tarjetas/detalle";
  }

  // Creamos una nueva tarjeta
  @GetMapping("/tarjetas/new")
  public String nuevaTarjetaForm(Model model) {
    // Lo añadimos al model
    model.addAttribute("tarjeta", TarjetaCreateDto.builder().build());
    model.addAttribute("modoEditar", false );
    return "admin/tarjetas/form";
  }

  @PostMapping("/tarjetas/new")
  public String nuevaTarjetaSubmit(@Valid @ModelAttribute("tarjeta") TarjetaCreateDto tarjeta,
                                    BindingResult bindingResult) {

    log.info("Datos recibidos del formulario: {}", tarjeta);
    // Si no tiene errores...
    if (bindingResult.hasErrors()) {
      log.info("hay errores en la validación");
      return "admin/tarjetas/form";
    } else {
      //insertamos
      tarjetasService.save(tarjeta);
      return "redirect:/admin/tarjetas";
    }
  }

  @GetMapping("/tarjetas/{id}/edit")
  public String editarTarjetaForm(@PathVariable Long id, Model model) {
    Tarjeta tarjetaEncontrada = tarjetasService.buscarPorId(id).orElse(null);
    if (tarjetaEncontrada == null) {
      return "redirect:admin/tarjetas/new";
    } else {
      TarjetaUpdateDto tarjeta = TarjetaUpdateDto.builder()
          .numero(tarjetaEncontrada.getNumero())
          .cvc(tarjetaEncontrada.getCvc())
          .fechaCaducidad(tarjetaEncontrada.getFechaCaducidad())
          .saldo(tarjetaEncontrada.getSaldo())
          .build();
      model.addAttribute("tarjeta", tarjeta);
      model.addAttribute("tarjetaId", id );
      model.addAttribute("modoEditar", true);
      return "admin/tarjetas/form";
    }
  }

  @PostMapping("/tarjetas/{id}/edit")
  public String editarTarjetaSubmit(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("tarjeta") TarjetaUpdateDto tarjeta,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      redirectAttributes.addFlashAttribute("error",
          "Ha ocurrido un error al actualizar el tarjeta.");
      model.addAttribute("tarjetaId", id );
      model.addAttribute("modoEditar", true);
      return "admin/tarjetas/form";
    }

    tarjetasService.update(id, tarjeta);
    redirectAttributes.addFlashAttribute("message",
        "Tarjeta actualizada correctamente.");
    return "redirect:/admin/tarjetas/{id}";
  }

    @GetMapping("/tarjetas/{id}/delete")
    public String borrarTarjeta(@PathVariable Long id) {

        // TO DO Borrar con confirmación mediante ventana modal

        tarjetasService.deleteById(id);
        return "redirect:/admin/tarjetas";
    }

}
