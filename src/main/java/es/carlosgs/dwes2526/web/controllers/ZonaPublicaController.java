package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
// Fijamos ya de por si una ruta por defecto para escuchar en este controlador
@RequestMapping("/public")
public class ZonaPublicaController {
  private final TarjetasService tarjetasService;


  @GetMapping({"", "/", "/index"})
  public String index(Model model,
                      @RequestParam(name = "page", defaultValue = "0") int page,
                      @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<TarjetaResponseDto> tarjetasPage = tarjetasService.findAll(
        Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    model.addAttribute("page", tarjetasPage);
    return "index";
  }

}
