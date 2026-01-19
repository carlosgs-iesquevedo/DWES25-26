package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("tarjetas")
public class TarjetasController {
    private final TarjetasService tarjetasService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        TarjetaResponseDto tarjeta = tarjetasService.findById(id);
        model.addAttribute("tarjeta", tarjeta);
        return "tarjetas/detalle";
    }

  @GetMapping({"", "/", "/lista"})
  public String index(Model model,
                      @RequestParam(name = "page", defaultValue = "0") int page,
                      @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<TarjetaResponseDto> tarjetasPage = tarjetasService.findAll(
        Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", tarjetasPage);
    return "tarjetas/lista";
  }

  // Creamos una nueva tarjeta
  @GetMapping("/new")
  public String nuevaTarjetaForm(Model model) {
    // Lo añadimos al model
    model.addAttribute("tarjeta", TarjetaCreateDto.builder().build());
    return "/tarjetas/form";
  }

  @PostMapping("/new")
  public String nuevaTarjetaSubmit(@Valid @ModelAttribute TarjetaCreateDto tarjeta,
                                    BindingResult bindingResult) {

    // Si no tiene errores...
    if (bindingResult.hasErrors()) {
      return "/tarjetas/form";
    } else {
      //insertamos
      tarjetasService.save(tarjeta);
      return "redirect:/tarjetas/lista";
    }
  }

}
